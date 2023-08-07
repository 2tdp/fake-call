package com.remi.fakecall.prankfriend.colorphone.activity.ui.theme.images

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.GifAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.activity.ui.theme.ThemeActivityViewModels
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.FragImageOnlineBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.GIF_FOLDER
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GifOnlineFragment : BaseFragment<FragImageOnlineBinding>(FragImageOnlineBinding::inflate) {

    companion object {
        fun newInstance(): GifOnlineFragment {
            val args = Bundle()

            val fragment = GifOnlineFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: ThemeActivityViewModels by activityViewModels()

    @Inject
    lateinit var gifAdapter: GifAdapter
    lateinit var callback: ICallBackItem

    override fun setUp() {
        Utils.makeFolder(requireContext(), GIF_FOLDER)

        gifAdapter.newInstance(requireContext(), callback)

        binding.rcvPic.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rcvPic.adapter = gifAdapter

        lifecycleScope.launch {
            viewModel.getAllGif()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateGif.collect {
                    when(it) {
                        is UiState.Success -> {
                            binding.ivLoading.visibility = View.GONE
                            gifAdapter.setData(it.data)
                        }
                        is UiState.Error -> {}
                        is UiState.Loading -> {}
                    }
                }
            }
        }
    }

}