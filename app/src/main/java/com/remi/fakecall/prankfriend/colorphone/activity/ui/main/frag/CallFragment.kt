package com.remi.fakecall.prankfriend.colorphone.activity.ui.main.frag

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.CallModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.MusicModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.contact.ContactModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ThemeModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.frag.PickContactFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.frag.audio.PickAudioFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.schedule.ScheduleActivity
import com.remi.fakecall.prankfriend.colorphone.activity.ui.theme.PickThemeFragment
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackCheck
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.FragCallBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_FAKE_CONTACT
import com.remi.fakecall.prankfriend.colorphone.helpers.SCHEDULE_CALL
import com.remi.fakecall.prankfriend.colorphone.helpers.TAG
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import com.remi.fakecall.prankfriend.colorphone.utils.UtilsBitmap
import com.yalantis.ucrop.UCrop
import java.io.File

class CallFragment : BaseFragment<FragCallBinding>(FragCallBinding::inflate) {

    companion object {
        fun newInstance(): CallFragment {
            val args = Bundle()

            val fragment = CallFragment()
            fragment.arguments = args
            return fragment
        }
    }

    val fragPickContact: PickContactFragment by lazy { PickContactFragment.newInstance() }
    val fragPickAudio: PickAudioFragment by lazy { PickAudioFragment.newInstance() }
    val fragPickTheme: PickThemeFragment by lazy { PickThemeFragment.newInstance() }

    private var intent: Intent? = null
    private lateinit var callModel: CallModel
    lateinit var isCheckPerContact: ICallBackCheck
    lateinit var isCheckPerAudio: ICallBackCheck

    override fun setUp() {
        callModel = CallModel()
        setUpView()
        evenClick()
    }

    private fun evenClick() {
        binding.ivAvatar.setOnClickListener { pickImage.launch("image/*") }
        binding.ivContact.setOnClickListener { pickContact() }
        binding.ctlVoice.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED)
                    showDialogAudioPermission()
                else pickAudio()
            } else {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    showDialogAudioPermission()
                else pickAudio()
            }
        }
        binding.ctlTheme.setOnClickListener {
            fragPickTheme.callback = object : ICallBackItem {
                override fun callBack(ob: Any, position: Int) {
                    if (position != -1) {
                        val themeModel = ob as ThemeModel
                        callModel.theme = themeModel
                        if (!binding.tvNameTheme.isVisible) binding.tvNameTheme.visibility = View.VISIBLE
                        binding.tvNameTheme.text = themeModel.name
                    } else {
                        callModel.theme = ThemeModel()
                        if (binding.tvNameTheme.isVisible) binding.tvNameTheme.visibility = View.GONE
                    }
                }
            }
            replaceFragment(parentFragmentManager, fragPickTheme, true, true)
        }
        binding.ctlSchedule.setOnClickListener {
            if (binding.edtNumbPhone.text.toString() == "") {
                Utils.showToast(requireContext(), requireContext().getString(R.string.cant_empty_numb), Gravity.CENTER)
                return@setOnClickListener
            }
            callModel.contact.apply {
                isFake = id == "-1"
                name = binding.edtName.text.toString()
                contact = "Mobile ${binding.edtNumbPhone.text.toString()}"
            }
            callModel.theme.apply {
                nameNumb = binding.edtName.text.toString()
                numb = "Mobile ${binding.edtNumbPhone.text.toString()}"
            }
            if (callModel.contact.id == "-1") addFakeContact()
            DataLocalManager.setScheduleCall(SCHEDULE_CALL, callModel)
            startIntent(ScheduleActivity::class.java.name, false)
        }
    }

    private fun setUpView() {
        binding.ctlVoice.createBackground(
            intArrayOf(Color.WHITE), (2.5f * w).toInt(),
            (0.278f * w).toInt(), Color.parseColor("#949494")
        )
        binding.tvNameVoice.apply {
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        binding.ctlTheme.createBackground(
            intArrayOf(Color.WHITE), (2.5f * w).toInt(),
            (0.278f * w).toInt(), Color.parseColor("#949494")
        )
        binding.ctlSchedule.createBackground(
            intArrayOf(Color.parseColor("#FF9950"), Color.parseColor("#FD6740")),
            (2.5f * w).toInt(), -1, -1
        )
    }

    fun setImageAvatar() {
        intent?.let {
            if (UCrop.getOutput(it) == null) return
            callModel.uriAvatar = UCrop.getOutput(it).toString()
            val bitmap = UtilsBitmap.getBitmapFromUri(requireContext(), UCrop.getOutput(it))
            bitmap?.let { bm -> binding.ivAvatar.setImageBitmap(bm) }
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val uCrop =
                UCrop.of(uri, Uri.fromFile(File(requireContext().cacheDir, "remi-fake-call-avatar-${System.currentTimeMillis()}.png")))
            uCrop.apply {
                useSourceImageAspectRatio()
                withOptions(UCrop.Options().apply {
                    setCompressionFormat(Bitmap.CompressFormat.PNG)
                    setCompressionQuality(100)
                })
            }
            uCrop.start(requireActivity())
            intent = uCrop.getIntent(requireContext())
        }
    }

    private fun pickAudio() {
        fragPickAudio.callback = object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                if (position == -1) {
                    binding.tvNameVoice.visibility = View.GONE
                    callModel.voice = null
                } else {
                    val music = ob as MusicModel
                    binding.tvNameVoice.apply {
                        visibility = View.VISIBLE
                        text = music.songName
                    }
                    callModel.voice = music
                }
            }
        }

        replaceFragment(parentFragmentManager, fragPickAudio, true, true)
    }

    private fun showDialogAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.READ_MEDIA_AUDIO)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        pickAudio()
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                        Utils.showToast(requireContext(), getString(R.string.deny_per), Gravity.CENTER)
                        if (permissionDeniedResponse.isPermanentlyDenied)
                            openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissionRequest: PermissionRequest,
                        permissionToken: PermissionToken
                    ) {
                        permissionToken.continuePermissionRequest()
                    }
                }).check()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Dexter.withContext(requireContext())
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                            pickAudio()
                        }

                        override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                            Utils.showToast(requireContext(), getString(R.string.deny_per), Gravity.CENTER)
                            if (permissionDeniedResponse.isPermanentlyDenied)
                                openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permissionRequest: PermissionRequest,
                            permissionToken: PermissionToken
                        ) {
                            permissionToken.continuePermissionRequest()
                        }
                    }).check()
            } else {
                Dexter.withContext(requireContext())
                    .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            p1?.continuePermissionRequest()
                        }

                        override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                            if (p0.areAllPermissionsGranted()) pickAudio()
                            else {
                                if (p0.isAnyPermissionPermanentlyDenied) {
                                    openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    isCheckPerAudio.check(true)
                                }
                                Utils.showToast(requireContext(), getString(R.string.deny_per), Gravity.CENTER)
                            }
                        }
                    })
                    .withErrorListener {
                        isCheckPerAudio.check(true)
                        openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        Utils.showToast(requireContext(), it.name, Gravity.CENTER)
                    }
                    .check()
            }
        }
    }

    private fun pickContact() {
        fragPickContact.callback = object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                val contact = ob as ContactModel

                if (contact.thumb == "") binding.ivAvatar.setImageResource(R.drawable.ic_default_avartar)
                else {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(contact.thumb)
                        .into(binding.ivAvatar)
                    callModel.uriAvatar = contact.thumb
                }
                binding.edtName.setText(contact.name)
                binding.edtNumbPhone.setText(contact.contact.replace("Mobile ", ""))
                callModel.contact = contact
            }
        }
        fragPickContact.isCheckPer = isCheckPerContact
        replaceFragment(parentFragmentManager, fragPickContact, true, true)
    }

    private fun addFakeContact() {
        val lstFake = DataLocalManager.getListFakeContact(LIST_FAKE_CONTACT)
        if (!lstFake.contains(callModel.contact)) {
            val fakeContact = callModel.contact
            fakeContact.apply { contact = callModel.contact.contact }
            lstFake.add(callModel.contact)
            DataLocalManager.setListFakeContact(lstFake, LIST_FAKE_CONTACT)
        }
    }
}