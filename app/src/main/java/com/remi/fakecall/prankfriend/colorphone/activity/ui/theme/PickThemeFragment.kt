package com.remi.fakecall.prankfriend.colorphone.activity.ui.theme

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.adapter.ViewPagerAddFragmentsAdapter
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.FragPickThemeBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.textCustom
import com.remi.fakecall.prankfriend.colorphone.utils.Utils.getTypeFace

class PickThemeFragment : BaseFragment<FragPickThemeBinding>(FragPickThemeBinding::inflate) {

    companion object {
        fun newInstance(): PickThemeFragment {
            val args = Bundle()

            val fragment = PickThemeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var fragItemTheme: ItemThemeFragment
    lateinit var callback: ICallBackItem

    override fun setUp() {
        setUpView()
        binding.ivApply.setOnClickListener { backStackAll(55) }
        binding.ivBack.setOnClickListener { backStackAll(-1) }
    }

    private fun setUpView() {
        val arrTheme = arrayOf(
            "MY THEME", "SAMSUNG", "HUAWEI", "XIAOMI", "OPPO", "VIVO", "LG", "MEIZU", "ANDROID", "ONEPLUS",
            "IPHONE", "NOKIA", "MOTOROLA",
        )
        val viewPagerAdapter = ViewPagerAddFragmentsAdapter(parentFragmentManager, lifecycle)
        for (theme in arrTheme) {
            fragItemTheme = ItemThemeFragment.newInstance()
            fragItemTheme.apply {
                typeTheme = theme
                this.callBack = callback
            }
            viewPagerAdapter.addFrag(fragItemTheme)
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tvTab = tab.customView as TextView
                tvTab.apply {
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_background))
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
                    typeface = getTypeFace("roboto", "roboto_medium.ttf", requireContext())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tvTab = tab.customView as TextView
                tvTab.apply {
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_background))
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black_text))
                    typeface = getTypeFace("roboto", "roboto_medium.ttf", requireContext())
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        binding.viewPager.offscreenPageLimit = 5
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val text = arrTheme[position]
            val tvTab = TextView(requireContext()).apply {
                gravity = Gravity.CENTER
                setPadding(
                    (3.24f * w).toInt(), w.toInt(), (3.24f * w).toInt(), w.toInt()
                )
                textCustom(
                    text.uppercase(),
                    ContextCompat.getColor(requireContext(), R.color.black_text),
                    3.589f * w,
                    "roboto_medium",
                    requireContext()
                )
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_background))
            }
            tab.customView = tvTab
        }.attach()
    }

    fun backStackAll(pos: Int) {
        if (pos == -1) callback.callBack("", pos)
        popBackStack(parentFragmentManager, "PickThemeFragment")
        binding.viewPager.adapter = null
    }
}