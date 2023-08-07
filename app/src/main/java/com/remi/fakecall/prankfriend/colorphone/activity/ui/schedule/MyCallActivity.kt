package com.remi.fakecall.prankfriend.colorphone.activity.ui.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.net.rtp.AudioStream
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.CallModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseActivity
import com.remi.fakecall.prankfriend.colorphone.databinding.ActivityMyCallBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.*
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import com.remi.fakecall.prankfriend.colorphone.utils.UtilsBitmap
import com.remi.fakecall.prankfriend.colorphone.viewcustom.CounterTimer
import com.remi.fakecall.prankfriend.colorphone.viewcustom.OnCalledResult
import kotlinx.coroutines.NonCancellable.start
import java.io.IOException
import java.util.Timer
import java.util.TimerTask

class MyCallActivity : BaseActivity<ActivityMyCallBinding>(ActivityMyCallBinding::inflate) {

    override fun isVisible(): Boolean {
        return false
    }

    private var schedule: CallModel? = null

    private lateinit var audioManager: AudioManager
    private lateinit var sensorManager: SensorManager
    private lateinit var proximity: Sensor
    private lateinit var pm: PowerManager
    private var wlOn: PowerManager.WakeLock? = null
    private var wlOff: PowerManager.WakeLock? = null
    private var isSpeaker = false
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayAudio = false
    private var timerRingCall: Timer? = null
    private var timerVibrate: Timer? = null
    private var timerTalkCall: Timer? = null

    override fun setUp() {
        timerRingCall = Timer()
        timerVibrate = Timer()
        timerTalkCall = Timer()
        schedule = DataLocalManager.getScheduleCall(SCHEDULE_CALL)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        setUpView()

        binding.ivSpeaker.setOnClickListener {
            if (!isSpeaker) {
                binding.ivSpeaker.setImageResource(R.drawable.ic_call_speaker_on)
                audioManager.setStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    0)
                isSpeaker = true
            } else {
                binding.ivSpeaker.setImageResource(R.drawable.ic_call_speaker)
                audioManager.setStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                        audioManager.getStreamMinVolume(AudioManager.STREAM_VOICE_CALL)
                    else 13,
                    0)
                isSpeaker = false
            }
        }
        binding.ivEnd.setOnClickListener {
            handStop()
            checkProximity()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (DataLocalManager.getCheck(SETTINGS_SENSOR_PROXIMITY))
            sensorManager.registerListener(sensorListener, proximity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun setUpView() {
        binding.vBlur.apply {
            setBlurRadius(84f)
            setOverlayColor(Color.parseColor("#D92F2F2F"))
        }
        binding.vCall.apply {
            schedule?.let {
                when (it.theme.typeImages) {
                    TYPE_IMAGE_APP -> {
                        isImage = true
                        bmBg = Bitmap.createScaledBitmap(
                            UtilsBitmap.getBitmapFromAsset(context, "background", it.theme.uriImages)!!,
                            (w * 100).toInt(), resources.displayMetrics.heightPixels, false
                        )
                    }

                    TYPE_IMAGE_USER -> {
                        isImage = true
                        bmBg = Bitmap.createScaledBitmap(
                            UtilsBitmap.getBitmapFromUri(context, Uri.parse(it.theme.uriImages))!!,
                            (w * 100).toInt(), resources.displayMetrics.heightPixels, false
                        )
                    }

                    TYPE_GIF_ONLINE -> {
                        isImage = false

                        Glide.with(this@MyCallActivity)
                            .asGif()
                            .load(it.theme.gif.large)
                            .placeholder(R.drawable.ic_placeholder_rec)
                            .into(binding.ivGif)
                    }
                    else -> {
                        isImage = false
                    }
                }
                this.themeModel = it.theme
            }
            onCalledResult = object : OnCalledResult {
                override fun onDown(v: View) {

                }

                override fun onMove(isAnswer: Boolean, isDeny: Boolean) {
                    stopRing()
                    handStop()
                    if (isAnswer) {
                        schedule?.isAnswer = true
                        actionAnswer()
                    }
                    if (isDeny) {
                        schedule?.isAnswer = false
                        checkProximity()
                        finish()

                        if (DataLocalManager.getCheck(SETTINGS_GO_HOME_END_CALL))
                            startActivity(Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                    }
                    addHistorySchedule(schedule!!)
                }

                override fun onUp(v: View, value: Int) {

                }

            }
        }
        binding.ivEnd.setImageBitmap(
            UtilsBitmap.getBitmapFromAsset(this, "theme/${schedule?.theme?.buttonStyle?.name}", "deny.png")
        )

        startRingTime()
    }

    private fun startRingTime() {
        val ringTime = DataLocalManager.getLong(SETTINGS_RING_TIME)
        timerRingCall?.schedule(object : TimerTask() {
            override fun run() {
                handStop()
                checkProximity()
                finish()
                addHistorySchedule(schedule!!)
            }
        }, ringTime)
        if (DataLocalManager.getCheck(SETTINGS_SOUND)) {
            getRingtonePathFromContentUri(
                this@MyCallActivity,
                Uri.parse(DataLocalManager.getOption(SETTINGS_URI_RINGTONE))
            )?.let {
                handPlay(it)
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    DataLocalManager.getInt(SETTINGS_VOLUME) * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 100,
                    0)
            }
        }
        if (DataLocalManager.getCheck(SETTINGS_VIBRATE))
            timerVibrate?.schedule(object : TimerTask() {
                override fun run() {
                    Utils.makeVibrate(this@MyCallActivity)
                }
            }, 0, 1500)
    }

    private fun actionAnswer() {
        binding.vCall.visibility = View.GONE
        binding.vAnswer.visibility = View.VISIBLE

        CounterTimer(true, object : CounterTimer.OnTimerTickListener {
            @SuppressLint("SetTextI18n")
            override fun onTimerTick(duration: Array<String>) {
                binding.tvTimer.text = "${duration[2]}:${duration[1]}"
            }
        }).start()

        schedule?.let {
            if (it.uriAvatar == "") binding.ivAvatar.setImageResource(R.drawable.ic_default_avartar)
            else
                Glide.with(this@MyCallActivity)
                    .asBitmap()
                    .load(it.uriAvatar)
                    .into(binding.ivAvatar)

            binding.tvName.text = it.contact.name
            it.voice?.let { voice ->
                handPlay(voice.uri)
                mediaPlayer?.setAudioAttributes(AudioAttributes.Builder()
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setLegacyStreamType(AudioManager.STREAM_VOICE_CALL)
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
                audioManager.setStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                        audioManager.getStreamMinVolume(AudioManager.STREAM_VOICE_CALL)
                    else 13,
                    0)
            }
        }
        timerTalkCall?.schedule(object : TimerTask() {
            override fun run() {
                handStop()
                checkProximity()
                finish()
                timerTalkCall?.let {
                    it.purge()
                    it.cancel()
                }
            }
        }, DataLocalManager.getLong(SETTINGS_TALK_TIME))
    }

    private fun handPlay(uri: String) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@MyCallActivity, Uri.parse(uri))
                setOnPreparedListener {
                    isPlayAudio = true
                    start()
                }
                prepareAsync()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handStop() {
        Utils.makeVibrate(this@MyCallActivity)
        mediaPlayer?.let {
            if (isPlayAudio) {
                if (it.isPlaying) {
                    it.apply {
                        stop()
                        release()
                    }
                }
            }
            isPlayAudio = false
        }
        mediaPlayer = null
    }

    private fun addHistorySchedule(schedule: CallModel) {
        val lstScheduleHistory = DataLocalManager.getListSchedule(LIST_SCHEDULE_HISTORY)
        val lstTmp = lstScheduleHistory.filter { it.idCall == schedule.idCall }
        if (lstTmp.isNotEmpty()) {
            lstScheduleHistory.remove(lstTmp[0])
            lstScheduleHistory.add(schedule)
        } else lstScheduleHistory.add(schedule)
        DataLocalManager.setListSchedule(lstScheduleHistory, LIST_SCHEDULE_HISTORY)
    }

    private val sensorListener = object : SensorEventListener {
        @SuppressLint("WakelockTimeout")
        override fun onSensorChanged(event: SensorEvent) {
            if (isFinishing || isDestroyed) return
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                    //near
                    wlOff = pm.newWakeLock(
                        PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                        getString(R.string.app_name)
                    )
                    wlOff?.acquire()
                } else {
                    //far
                    wlOn = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.app_name))
                    wlOn?.acquire()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    private fun checkProximity() {
        wlOff?.let {
            if (it.isHeld) it.release()
        }
        wlOn?.let {
            if (it.isHeld) it.release()
        }
        sensorManager.unregisterListener(sensorListener, proximity)
    }

    private fun getRingtonePathFromContentUri(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val ringtoneCursor =
            context.contentResolver.query(contentUri, proj, null, null, null)
        ringtoneCursor?.let {
            it.moveToFirst()
            val path = try {
                it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            it.close()
            return path
        }
        return null
    }

    private fun stopRing() {
        timerVibrate?.let {
            it.purge()
            it.cancel()
        }
        timerRingCall?.let {
            it.purge()
            it.cancel()
        }
    }
}