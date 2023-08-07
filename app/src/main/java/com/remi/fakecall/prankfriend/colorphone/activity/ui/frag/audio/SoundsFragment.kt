package com.remi.fakecall.prankfriend.colorphone.activity.ui.frag.audio

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.SoundsAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivityViewModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemFragAudioBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.FOLDER_RECORD
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SoundsFragment : BaseFragment<ItemFragAudioBinding>(ItemFragAudioBinding::inflate) {

    companion object {
        fun newInstance(): SoundsFragment {
            val args = Bundle()

            val fragment = SoundsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var soundsAdapter: SoundsAdapter
    private val viewModel: MainActivityViewModel by activityViewModels()
    lateinit var callback: ICallBackItem

    override fun setUp() {
        Utils.makeFolder(requireContext(), FOLDER_RECORD)
        setUpView()
        binding.ivLoading.apply {
            setAnimation(R.raw.iv_loading)
            repeatCount = LottieDrawable.INFINITE
            playAnimation()
        }
        lifecycleScope.launch {
                viewModel.getAllMusic()
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiStateAudio.collect {
                        when (it) {
                            is UiState.Success -> {
                                binding.ivLoading.visibility = View.GONE
                                soundsAdapter.setData(it.data)
                            }

                            is UiState.Error -> {}
                            is UiState.Loading -> {}
                        }
                    }
                }
        }
    }

    private fun setUpView() {
        soundsAdapter.newInstance(requireContext(), callback)

        binding.rcvItemFragAudio.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvItemFragAudio.adapter = soundsAdapter
    }
}