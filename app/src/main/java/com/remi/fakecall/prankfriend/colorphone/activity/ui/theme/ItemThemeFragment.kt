package com.remi.fakecall.prankfriend.colorphone.activity.ui.theme

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.CallModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ThemeModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.ThemeAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivityViewModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.FragItemThemeBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemFragAudioBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.helpers.MY_THEME
import com.remi.fakecall.prankfriend.colorphone.helpers.PREVIEW_THEME
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ItemThemeFragment : BaseFragment<FragItemThemeBinding>(FragItemThemeBinding::inflate) {

    companion object {
        fun newInstance(): ItemThemeFragment {
            val args = Bundle()

            val fragment = ItemThemeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var themeAdapter: ThemeAdapter

    private val viewModel: MainActivityViewModel by activityViewModels()

    var typeTheme = ""
    lateinit var callBack: ICallBackItem

    override fun setUp() {
        setUpView()
        binding.tvCreate.setOnClickListener { startIntent(ThemeActivity::class.java.name, false) }
        binding.ivAddCustomTheme.setOnClickListener { startIntent(ThemeActivity::class.java.name, false) }
    }

    private fun setUpView() {
        binding.tvCreate.createBackground(
            intArrayOf(Color.WHITE), (2.5f * w).toInt(),
            (0.5f * w).toInt(), ContextCompat.getColor(requireContext(), R.color.main_text)
        )

        themeAdapter.newInstance(requireContext(), object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                if (position != -1) callBack.callBack(ob, position)
                else {
                    DataLocalManager.setThemePreview(PREVIEW_THEME, ob as ThemeModel)
                    Utils.setIntent(requireContext(), PreviewActivity::class.java.name)
                }
            }
        })
        binding.rcvItemTheme.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rcvItemTheme.adapter = themeAdapter

        loadData()
    }

    private fun loadData() {
        when (typeTheme) {
            "MY THEME" -> viewModel.getAllMyTheme()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (typeTheme) {
                    "MY THEME" -> {
                        viewModel.uiStateMyTheme.collect {
                            when (it) {
                                is UiState.Success -> {
                                    if (it.data.isEmpty()) {
                                        binding.tvCreate.visibility = View.VISIBLE
                                        binding.tvCustom.visibility = View.VISIBLE
                                        binding.ivAddCustomTheme.visibility = View.GONE
                                    } else {
                                        binding.tvCreate.visibility = View.GONE
                                        binding.tvCustom.visibility = View.GONE
                                        binding.ivAddCustomTheme.visibility = View.VISIBLE

                                        themeAdapter.setData(it.data)
                                    }
                                }

                                is UiState.Error -> {}
                                is UiState.Loading -> {}
                            }
                        }
                    }

                    else -> {
                        binding.tvCreate.visibility = View.GONE
                        binding.tvCustom.visibility = View.GONE
                    }
                }
            }
        }
    }
}