package com.remi.fakecall.prankfriend.colorphone.activity.ui.frag.audio

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.RecordingsAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.SoundsAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivityViewModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemFragAudioBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.helpers.AGREE_DEL
import com.remi.fakecall.prankfriend.colorphone.helpers.CANCEL_DEL
import com.remi.fakecall.prankfriend.colorphone.helpers.FOLDER_RECORD
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_RECORDINGS
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_SOUNDS
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordingsFragment : BaseFragment<ItemFragAudioBinding>(ItemFragAudioBinding::inflate) {

    companion object {
        fun newInstance(): RecordingsFragment {
            val args = Bundle()

            val fragment = RecordingsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var recordingsAdapter: RecordingsAdapter
    private val viewModel: MainActivityViewModel by activityViewModels()
    lateinit var callback: ICallBackItem
    lateinit var callbackLong: ICallBackItem

    override fun setUp() {
        Utils.makeFolder(requireContext(), FOLDER_RECORD)
        setUpView()
        binding.ivLoading.apply {
            setAnimation(R.raw.iv_loading)
            repeatCount = LottieDrawable.INFINITE
            playAnimation()
        }
        lifecycleScope.launch {

                viewModel.getAllRecord()
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiStateRecord.collect {
                        when (it) {
                            is UiState.Success -> {
                                binding.ivLoading.visibility = View.GONE
                                recordingsAdapter.setData(it.data)
                            }

                            is UiState.Error -> {}
                            is UiState.Loading -> {}
                        }
                    }
                }
        }
    }

    private fun setUpView() {
        binding.tvDel.createBackground(
            intArrayOf(Color.parseColor("#FF9950"), Color.parseColor("#FD6740")),
            (2.5f * w).toInt(), -1, -1
        )
        binding.tvCancel.setOnClickListener { callbackLong.callBack("", CANCEL_DEL) }
        binding.tvDel.setOnClickListener { callbackLong.callBack("", AGREE_DEL) }


        recordingsAdapter.newInstance(requireContext(), callback, callbackLong)

        binding.rcvItemFragAudio.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvItemFragAudio.adapter = recordingsAdapter
    }
}