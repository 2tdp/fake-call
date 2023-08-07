package com.remi.fakecall.prankfriend.colorphone.activity.ui.theme.images

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.PictureAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivityViewModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.theme.ThemeActivityViewModels
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.FragImageOnlineBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImageAppFragment : BaseFragment<FragImageOnlineBinding>(FragImageOnlineBinding::inflate) {

    companion object {
        fun newInstance(): ImageAppFragment {
            val args = Bundle()

            val fragment = ImageAppFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var picAdapter: PictureAdapter
    lateinit var callback: ICallBackItem

    private val viewModel: ThemeActivityViewModels by activityViewModels()

    override fun setUp() {
        binding.ivLoading.apply {
            setAnimation(R.raw.iv_loading)
            repeatCount = LottieDrawable.INFINITE
            playAnimation()
        }

        picAdapter.newInstance(requireContext(), callback)

        binding.rcvPic.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rcvPic.adapter = picAdapter

        lifecycleScope.launch {
            viewModel.getAppImages("background")
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateAppImages.collect {
                    when(it) {
                        is UiState.Success -> {
                            binding.ivLoading.visibility = View.GONE
                            picAdapter.setData(it.data)
                        }
                        is UiState.Error -> {}
                        is UiState.Loading -> {}
                    }
                }
            }
        }
    }
}