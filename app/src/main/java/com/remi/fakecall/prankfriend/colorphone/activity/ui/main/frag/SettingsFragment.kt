package com.remi.fakecall.prankfriend.colorphone.activity.ui.main.frag

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackCheck
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.FragSettingsBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.parcelable
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_GO_HOME_END_CALL
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_RING_TIME
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_SENSOR_PROXIMITY
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_SOUND
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_TALK_TIME
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_URI_RINGTONE
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_VIBRATE
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_VOLUME
import com.remi.fakecall.prankfriend.colorphone.helpers.TAG
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.ActionUtils
import com.remi.fakecall.prankfriend.colorphone.viewcustom.OnSeekbarResult


class SettingsFragment: BaseFragment<FragSettingsBinding>(FragSettingsBinding::inflate) {

    companion object {
        fun newInstance(): SettingsFragment {
            val args = Bundle()

            val fragment = SettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var isSwipePager: ICallBackCheck? = null
    var isPickTime: ICallBackItem? = null

    override fun setUp() {
        setUpView()
        evenClick()
    }

    private fun evenClick() {
        binding.ctlGoHome.setOnClickListener {
            binding.swGoHome.isChecked = !binding.swGoHome.isChecked
            DataLocalManager.setCheck(SETTINGS_GO_HOME_END_CALL, binding.swGoHome.isChecked)
        }
        binding.swGoHome.setOnClickListener {
            DataLocalManager.setCheck(SETTINGS_GO_HOME_END_CALL, binding.swGoHome.isChecked)
        }
        binding.ctlUseSensor.setOnClickListener {
            binding.swUseSensor.isChecked = !binding.swUseSensor.isChecked
            DataLocalManager.setCheck(SETTINGS_SENSOR_PROXIMITY, binding.swUseSensor.isChecked)
        }
        binding.swUseSensor.setOnClickListener {
            DataLocalManager.setCheck(SETTINGS_SENSOR_PROXIMITY, binding.swUseSensor.isChecked)
        }
        binding.ctlSound.setOnClickListener {
            binding.swSound.isChecked = !binding.swSound.isChecked
            DataLocalManager.setCheck(SETTINGS_SOUND, binding.swSound.isChecked)
            changeStateSound(binding.swSound.isChecked)
        }
        binding.swSound.setOnClickListener {
            changeStateSound(binding.swSound.isChecked)
            DataLocalManager.setCheck(SETTINGS_SOUND, binding.swSound.isChecked)
        }
        binding.ctlRingTone.setOnClickListener {
            if (!binding.swSound.isChecked) return@setOnClickListener
            pickRingtone.launch(Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
            })
        }
        binding.ctlVibrate.setOnClickListener {
            binding.swVibrate.isChecked = !binding.swVibrate.isChecked
            DataLocalManager.setCheck(SETTINGS_VIBRATE, binding.swVibrate.isChecked)
        }
        binding.swVibrate.setOnClickListener {
            DataLocalManager.setCheck(SETTINGS_VIBRATE, binding.swVibrate.isChecked)
        }

        binding.ctlMoreApp.setOnClickListener { ActionUtils.moreApps(requireContext()) }
        binding.ctlRateApp.setOnClickListener { ActionUtils.rateApp(requireContext()) }
        binding.ctlShareApp.setOnClickListener { ActionUtils.shareApp(requireContext()) }
        binding.ctlPP.setOnClickListener { ActionUtils.openPolicy(requireContext()) }
        binding.ctlFeedback.setOnClickListener { ActionUtils.sendFeedback(requireContext()) }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpView() {
        binding.swGoHome.apply {
            setThumbResource(R.drawable.custom_switch_thumb)
            setTrackResource(R.drawable.custom_switch_track)
        }
        binding.swUseSensor.apply {
            setThumbResource(R.drawable.custom_switch_thumb)
            setTrackResource(R.drawable.custom_switch_track)
        }
        binding.swSound.apply {
            setThumbResource(R.drawable.custom_switch_thumb)
            setTrackResource(R.drawable.custom_switch_track)
        }
        binding.sbVolume.apply {
            setMax(100)
            setProgress(
                (requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager).getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            )
            onSeekbarResult = object : OnSeekbarResult{
                override fun onDown(v: View) {

                }

                override fun onMove(v: View, value: Int) {
                    DataLocalManager.setInt(value, SETTINGS_VOLUME)
                    binding.tvVolume.text = value.toString()
                }

                override fun onUp(v: View, value: Int) {

                }
            }
            isSwipe = object : ICallBackCheck {
                override fun check(isCheck: Boolean) {
                    binding.root.isEnableScroll = !isCheck
                    isSwipePager?.check(isCheck)
                }
            }
        }
        binding.swVibrate.apply {
            setThumbResource(R.drawable.custom_switch_thumb)
            setTrackResource(R.drawable.custom_switch_track)
        }

        binding.ctlRingTimer.setOnClickListener { isPickTime?.callBack("ring_time", -1) }
        binding.ctlTalkTime.setOnClickListener { isPickTime?.callBack("talk_time", -1) }

        binding.swGoHome.isChecked = DataLocalManager.getCheck(SETTINGS_GO_HOME_END_CALL)
        binding.swUseSensor.isChecked = DataLocalManager.getCheck(SETTINGS_SENSOR_PROXIMITY)
        binding.swVibrate.isChecked = DataLocalManager.getCheck(SETTINGS_VIBRATE)
        binding.tvDesRingTimer.text = "End call after ${DataLocalManager.getLong(SETTINGS_RING_TIME) / 1000L}s"
        binding.tvDesTalkTime.text = "${DataLocalManager.getLong(SETTINGS_TALK_TIME) / 1000L}s"
        binding.swSound.isChecked = DataLocalManager.getCheck(SETTINGS_SOUND)
        binding.tvVolume.text = DataLocalManager.getInt(SETTINGS_VOLUME).toString()
        binding.sbVolume.setProgress(DataLocalManager.getInt(SETTINGS_VOLUME))
        val ringtone = RingtoneManager.getRingtone(requireContext(), Uri.parse(DataLocalManager.getOption(SETTINGS_URI_RINGTONE)))
        binding.tvDesRingTone.text = ringtone.getTitle(requireContext())
        changeStateSound(DataLocalManager.getCheck(SETTINGS_SOUND))
    }

    private fun changeStateSound(isCheck: Boolean) {
        if (isCheck) {
            binding.ivSound.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black_text), PorterDuff.Mode.SRC_IN)
            binding.tvSound.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_text))

            binding.ivRingTone.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black_text), PorterDuff.Mode.SRC_IN)
            binding.tvRingTone.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_text))
            binding.tvDesRingTone.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))

            binding.ivVolume.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black_text), PorterDuff.Mode.SRC_IN)
            binding.tvVolume.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_text))
            binding.sbVolume.apply {
                isActive = true
                colorBg = Color.parseColor("#BCBCBC")
                colorPr = ContextCompat.getColor(requireContext(), R.color.main_text)
                colorThumb = ContextCompat.getColor(requireContext(), R.color.main_text)
                invalidate()
            }
        } else {
            binding.ivSound.setColorFilter(Color.parseColor("#949494"), PorterDuff.Mode.SRC_IN)
            binding.tvSound.setTextColor(Color.parseColor("#949494"))

            binding.ivRingTone.setColorFilter(Color.parseColor("#949494"), PorterDuff.Mode.SRC_IN)
            binding.tvRingTone.setTextColor(Color.parseColor("#949494"))
            binding.tvDesRingTone.setTextColor(Color.parseColor("#949494"))

            binding.ivVolume.setColorFilter(Color.parseColor("#949494"), PorterDuff.Mode.SRC_IN)
            binding.tvVolume.setTextColor(Color.parseColor("#949494"))
            binding.sbVolume.apply {
                isActive = false
                colorBg = Color.parseColor("#949494")
                colorPr = Color.parseColor("#67686D")
                colorThumb = Color.parseColor("#67686D")
                invalidate()
            }
        }
    }

    private val pickRingtone = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let {
                val uri = it.parcelable<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)

                DataLocalManager.setOption(uri.toString(), SETTINGS_URI_RINGTONE)
                val ringtone = RingtoneManager.getRingtone(requireContext(), uri)
                binding.tvDesRingTone.text = ringtone.getTitle(requireContext())
            }
        }
    }
}