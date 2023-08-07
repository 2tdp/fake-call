package com.remi.fakecall.prankfriend.colorphone.activity.ui.frag.audio

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.format.DateFormat
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.DataRecord
import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.MusicModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivityViewModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackCheck
import com.remi.fakecall.prankfriend.colorphone.databinding.DialogSaveRecordBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.FragRecordBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.helpers.FOLDER_RECORD
import com.remi.fakecall.prankfriend.colorphone.helpers.NAME_RECORD
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import com.remi.fakecall.prankfriend.colorphone.viewcustom.CounterTimer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream

class RecordFragment: BaseFragment<FragRecordBinding>(FragRecordBinding::inflate) {

    companion object {
        fun newInstance(): RecordFragment {
            val args = Bundle()

            val fragment = RecordFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mediaRecord: MediaRecorder
    private lateinit var timer: CounterTimer
    var isRecording = false
    private var dirPath = ""
    private var filename = ""
    private lateinit var amplitudes: ArrayList<Float>

    lateinit var isBack: ICallBackCheck

    override fun setUp() {
        timer = CounterTimer(true, object : CounterTimer.OnTimerTickListener {
            override fun onTimerTick(duration: Array<String>) {
                binding.tvDuration.text =
                    if (duration.size == 4) "${duration[3]}:${duration[2]}:${duration[1]}"
                    else "00:${duration[2]}:${duration[1]}"
                binding.waveForm.setDataAudio(mediaRecord.maxAmplitude.toFloat())
            }
        })

        binding.ivControlRecord.setOnClickListener {
            if (isRecording) stopRecorder()
            else startRecord()
        }
        binding.ivBack.setOnClickListener {
            if (isRecording) stopRecorder()
            else {
                isBack.check(true)
                popBackStack(parentFragmentManager, "RecordFragment")
            }
        }
    }

    private fun startRecord() {
        filename = "$NAME_RECORD-${System.currentTimeMillis()}-${DateFormat.format("yyyy-MM-dd", System.currentTimeMillis())}"

        mediaRecord = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            MediaRecorder(requireContext())
        else MediaRecorder()

        dirPath = Utils.getStore(requireContext()) + "/$FOLDER_RECORD/"

        mediaRecord.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")
            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
                showMessage(resources.getString(R.string.err_record))
            }
            start()
            binding.ivControlRecord.setImageResource(R.drawable.ic_record_pause)
        }

        isRecording = true

        timer.start()
    }

    fun stopRecorder() {
        timer.stop()

        mediaRecord.apply {
            stop()
            reset()
            release()
        }

        isRecording = false

        binding.tvDuration.text = resources.getString(R.string.duration)
        amplitudes = binding.waveForm.clear()

        binding.ivControlRecord.setImageResource(R.drawable.ic_record_start)

        showDialogSave()
    }

    @SuppressLint("SetTextI18n")
    private fun showDialogSave() {
        val bindingSave = DialogSaveRecordBinding.inflate(LayoutInflater.from(requireContext()))
        bindingSave.edtName.apply {
            isSingleLine = true
            createBackground(
                intArrayOf(Color.WHITE), (2.5f * w).toInt(),
                (0.278f * w).toInt(), Color.parseColor("#E0E0E0")
            )
            setText(filename.replace(".mp3", ""))
        }

        val dialog = AlertDialog.Builder(requireContext(), R.style.SheetDialog).create()
        dialog.apply {
            setView(bindingSave.root)
            setCancelable(false)
            show()
        }

        bindingSave.root.layoutParams.width = (83.33f * w).toInt()
        bindingSave.root.layoutParams.height = (38.889f * w).toInt()

        bindingSave.tvDel.setOnClickListener {
            dialog.cancel()
            delFileRecord()
        }
        bindingSave.ivSave.setOnClickListener {
            dialog.cancel()
            saveRecord(bindingSave.edtName.text.toString())
        }
    }
    private fun saveRecord(nameFile: String) {
        Thread {
            if (nameFile != filename) {
                if (nameFile == "") {
                    showMessage(getString(R.string.file_name_not_empty))
                    return@Thread
                } else {
                    val newFile = File("$dirPath$nameFile.mp3")
                    File("$dirPath$filename.mp3").renameTo(newFile)
                }
            }
            val ampsPath = if (nameFile != "") "$dirPath$nameFile"
            else "$dirPath$filename"
            try {
                val fos = FileOutputStream(ampsPath)
                val out = ObjectOutputStream(fos)
                out.writeObject(amplitudes)
                fos.close()
                out.close()
                Handler(Looper.getMainLooper()).post {
                    isBack.check(true)
                    popBackStack(parentFragmentManager, "RecordFragment")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                showMessage(getString(R.string.cant_save))
            }
        }.start()
    }

    private fun delFileRecord() {
        Thread {
            val file = File("$dirPath$filename.mp3")
            if (file.exists()) {
                if (file.delete()) {
                    Handler(Looper.getMainLooper()).post {
                        showMessage(getString(R.string.done))
                        popBackStack(parentFragmentManager, "RecordFragment")
                        isBack.check(true)
                    }
                } else showMessage(getString(R.string.cant_del))
            } else showMessage(getString(R.string.cant_del))
        }.start()
    }
}