package com.remi.fakecall.prankfriend.colorphone.activity.ui.frag

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.contact.ContactModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.ContactAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivityViewModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackCheck
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.FragPickContactBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_FAKE_CONTACT
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PickContactFragment : BaseFragment<FragPickContactBinding>(FragPickContactBinding::inflate) {

    companion object {
        fun newInstance(): PickContactFragment {
            val args = Bundle()

            val fragment = PickContactFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var contactAdapter: ContactAdapter
    private val viewModel: MainActivityViewModel by activityViewModels()

    lateinit var callback: ICallBackItem
    lateinit var isCheckPer: ICallBackCheck

    override fun setUp() {
        setUpView()
        binding.ivBack.setOnClickListener { popBackStack(parentFragmentManager, "PickContactFragment") }
        binding.tvCheckPerContact.setOnClickListener { checkPermissionContact() }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() == "") binding.ivExitSearch.visibility = View.GONE
                else binding.ivExitSearch.visibility = View.VISIBLE

                contactAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        lifecycleScope.launch {
            viewModel.getAllContact()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateContact.collect {
                    when(it) {
                        is UiState.Success -> {
                            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                                binding.ctlPerContacts.visibility = View.GONE
                                binding.ctlSearch.visibility = View.VISIBLE
                            } else {
                                binding.ctlPerContacts.visibility = View.VISIBLE
                                binding.ctlSearch.visibility = View.GONE
                            }

                            binding.ivLoading.visibility = View.GONE
                            if (it.data.isNotEmpty()) binding.tvTitleContacts.visibility = View.VISIBLE
                            else binding.tvTitleContacts.visibility = View.GONE
                            contactAdapter.setData(it.data)
                        }
                        is UiState.Error -> {}
                        is UiState.Loading -> {}
                    }
                }
            }
        }
    }

    private fun setUpView() {
        binding.ivLoading.apply {
            setAnimation(R.raw.iv_loading)
            repeatCount = LottieDrawable.INFINITE
            playAnimation()
        }
        binding.tvCheckPerContact.createBackground(
            intArrayOf(ContextCompat.getColor(requireContext(), R.color.white_background)), (2.5f * w).toInt(),
            (0.278f * w).toInt(), ContextCompat.getColor(requireContext(), R.color.main_text)
        )
        binding.edtSearch.createBackground(
            intArrayOf(Color.WHITE), (2.5f * w).toInt(),
            (0.278f * w).toInt(), Color.parseColor("#E0E0E0")
        )

        contactAdapter.newInstance(requireContext(), object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                if (position != -1) {
                    callback.callBack(ob, position)
                    popBackStack(parentFragmentManager, "PickContactFragment")
                } else delFakeContact(ob as ContactModel)
            }
        })

        binding.rcvAllContact.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvAllContact.adapter = contactAdapter
    }

    private fun delFakeContact(contact: ContactModel) {
        val lstFake = DataLocalManager.getListFakeContact(LIST_FAKE_CONTACT)
        val lstTmp = lstFake.filter { it.name == contact.name && it.contact == contact.contact }

        if (lstTmp.isNotEmpty()) lstFake.remove(lstTmp[0])
        DataLocalManager.setListFakeContact(lstFake, LIST_FAKE_CONTACT)
        viewModel.getAllContact()
    }

    private fun checkPermissionContact() {
        Dexter.withContext(requireContext())
            .withPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()) viewModel.getAllContact()
                    else {
                        if (p0.isAnyPermissionPermanentlyDenied) {
                            openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            isCheckPer.check(true)
                        }
                        Utils.showToast(requireContext(),getString(R.string.deny_per), Gravity.CENTER)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken
                ) {
                    p1.continuePermissionRequest()
                }
            })
            .withErrorListener {
                isCheckPer.check(true)
                openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                Utils.showToast(requireContext(), it.name, Gravity.CENTER)
            }
            .check()
    }
}