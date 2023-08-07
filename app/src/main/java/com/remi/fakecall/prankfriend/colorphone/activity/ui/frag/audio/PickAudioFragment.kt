package com.remi.fakecall.prankfriend.colorphone.activity.ui.frag.audio

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.MusicModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivityViewModel
import com.remi.fakecall.prankfriend.colorphone.adapter.ViewPagerAddFragmentsAdapter
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackCheck
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.DialogDelRecordBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.FragPickAudioBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.helpers.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class PickAudioFragment : BaseFragment<FragPickAudioBinding>(FragPickAudioBinding::inflate) {

    companion object {
        fun newInstance(): PickAudioFragment {
            val args = Bundle()

            val fragment = PickAudioFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: MainActivityViewModel by activityViewModels()

    val fragSound: SoundsFragment by lazy { SoundsFragment.newInstance() }
    val fragRecordings: RecordingsFragment by lazy { RecordingsFragment.newInstance() }
    val fragRecord: RecordFragment by lazy { RecordFragment.newInstance() }
    lateinit var callback: ICallBackItem

    private var mediaPlayer: MediaPlayer? = null
    private var oldPosition = -1
    private var isPlayAudio = false

    override fun setUp() {
        setUpView()
        binding.ivSave.setOnClickListener { popBackStackAll(55) }
        binding.ivBack.setOnClickListener { popBackStackAll(-1) }
        binding.ivAddRecord.setOnClickListener {
            handStop()
            checkPermissionRecord()
        }
        binding.ctlSound.setOnClickListener {
            swipePagerAudio(TYPE_SOUNDS)
            binding.viewPager.setCurrentItem(0, true)
        }
        binding.ctlRecord.setOnClickListener {
            swipePagerAudio(TYPE_RECORDINGS)
            binding.viewPager.setCurrentItem(1, true)
        }
        binding.ivDelAll.setOnClickListener { fragRecordings.recordingsAdapter.setAllDel() }
    }

    private fun setUpView() {
        val adapterViewPager = ViewPagerAddFragmentsAdapter(parentFragmentManager, lifecycle)

        adapterViewPager.addFrag(fragSound)
        adapterViewPager.addFrag(fragRecordings)

        fragSound.callback = object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                val music = ob as MusicModel
                callback.callBack(music, position)
                if (isPlayAudio) {
                    handStop()
                    if (oldPosition != position) handPlay(music)
                } else handPlay(music)

                oldPosition = position
            }
        }
        fragRecordings.callback = object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                fragRecordings.recordingsAdapter.setCurrent(position)
                val music = ob as MusicModel
                callback.callBack(music, position)
                if (isPlayAudio) {
                    handStop()
                    if (oldPosition != position) handPlay(music)
                } else handPlay(music)

                oldPosition = position
            }
        }
        fragRecordings.callbackLong = object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                handStop()
                when(position) {
                    START_DEL -> actionDel(true)
                    CANCEL_DEL -> actionDel(false)
                    AGREE_DEL -> showDialogDel()
                    ALL_DEL -> {
                        binding.ivDelAll.setImageResource(R.drawable.ic_del_tick)
                        binding.tvCountDel.text = fragRecordings.recordingsAdapter.getListDel().size.toString()
                    }
                    COUNT_DEL -> {
                        binding.ivDelAll.setImageResource(R.drawable.ic_del_un_tick)
                        binding.tvCountDel.text = fragRecordings.recordingsAdapter.getListDel().size.toString()
                    }
                }
            }
        }
        fragRecord.isBack = object : ICallBackCheck {
            override fun check(isCheck: Boolean) {
                viewModel.getAllRecord()
            }
        }

        binding.viewPager.apply {
            offscreenPageLimit = 2
            this.adapter = adapterViewPager
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    swipePagerAudio(if (position == 0) TYPE_SOUNDS else TYPE_RECORDINGS)
                }
            })
        }
    }

    fun handPlay(music: MusicModel) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(requireContext(), Uri.parse(music.path))
                setOnPreparedListener {
                    isPlayAudio = true
                    start()
                }
                prepareAsync()
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun handStop() {
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
    }

    @SuppressLint("SetTextI18n")
    private fun showDialogDel() {
        val bindingDel = DialogDelRecordBinding.inflate(LayoutInflater.from(requireContext()))
        bindingDel.tvDel.apply {
            createBackground(
                intArrayOf(Color.parseColor("#FF9950"), Color.parseColor("#FD6740")),
                (2.5f * w).toInt(), -1, -1
            )
            text = getString(R.string.del).uppercase()
        }
        bindingDel.tvCancel.text = getString(R.string.cancel).uppercase()

        val dialog = AlertDialog.Builder(requireContext(), R.style.SheetDialog).create()
        dialog.apply {
            setView(bindingDel.root)
            show()
        }

        bindingDel.root.layoutParams.width = (83.33f * w).toInt()
        bindingDel.root.layoutParams.height = (36.11f * w).toInt()

        val lstDel = fragRecordings.recordingsAdapter.getListDel()
        bindingDel.tvDesDel.text = "Are you sure want to delete ${lstDel.size} recordings?"
        bindingDel.tvCancel.setOnClickListener { dialog.cancel() }
        bindingDel.tvDel.setOnClickListener {
            bindingDel.ctlDel.visibility = View.GONE
            bindingDel.vLoad.visibility = View.VISIBLE

            Thread {
                for (music in lstDel) {
                    val file = File(music.path)
                    if (file.exists()) {
                        if (!file.delete()) Handler(Looper.getMainLooper()).post { showMessage(getString(R.string.cant_del)) }
                    } else Handler(Looper.getMainLooper()).post { showMessage(getString(R.string.cant_del)) }
                }
                Handler(Looper.getMainLooper()).post {
                    dialog.cancel()
                    actionDel(false)
                    viewModel.getAllRecord()
                }
            }.start()
        }
    }

    fun actionDel(isDell: Boolean) {
        if (isDell) {
            binding.ivSave.visibility = View.GONE
            binding.ctlDel.visibility = View.VISIBLE
            fragRecordings.binding.tvCancel.visibility = View.VISIBLE
            fragRecordings.binding.tvDel.visibility = View.VISIBLE

            binding.ivAddRecord.visibility = View.GONE
        } else {
            binding.ivSave.visibility = View.VISIBLE
            binding.ctlDel.visibility = View.GONE
            fragRecordings.binding.tvCancel.visibility = View.GONE
            fragRecordings.binding.tvDel.visibility = View.GONE

            binding.ivAddRecord.visibility = View.VISIBLE
            fragRecordings.recordingsAdapter.apply {
                isDel = false
                setCurrent(-1)
            }
        }
    }

    private fun swipePagerAudio(option: String) {
        handStop()
        when (option) {
            TYPE_SOUNDS -> {
                binding.ivSound.setImageResource(R.drawable.ic_sound)
                binding.tvSound.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_text
                    )
                )
                binding.vLine.visibility = View.VISIBLE

                binding.ivRecord.setImageResource(R.drawable.ic_micro)
                binding.tvRecording.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_text
                    )
                )
                binding.vLine1.visibility = View.GONE
            }

            TYPE_RECORDINGS -> {
                binding.ivRecord.setImageResource(R.drawable.ic_micro_2)
                binding.tvRecording.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_text
                    )
                )
                binding.vLine1.visibility = View.VISIBLE

                binding.ivSound.setImageResource(R.drawable.ic_settings_sound)
                binding.tvSound.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_text
                    )
                )
                binding.vLine.visibility = View.GONE
            }
        }
    }

    private fun checkPermissionRecord() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
            replaceFragment(parentFragmentManager, fragRecord, true, true)
        else {
            Dexter.withContext(requireContext())
                .withPermission("android.permission.RECORD_AUDIO")
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        replaceFragment(parentFragmentManager, fragRecord, true, true)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        showMessage(resources.getString(R.string.per_record))
                        openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken
                    ) {
                        p1.continuePermissionRequest()
                    }

                }).check()
        }
    }

    fun popBackStackAll(pos: Int) {
        handStop()
        if (pos == -1) callback.callBack("", pos)
        popBackStack(parentFragmentManager, "PickAudioFragment")
        binding.viewPager.adapter = null
    }
}