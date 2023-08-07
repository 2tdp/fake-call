package com.remi.fakecall.prankfriend.colorphone.activity.ui

import android.graphics.Color
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseActivity
import com.remi.fakecall.prankfriend.colorphone.databinding.ActivityPremiumBinding

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(ActivityPremiumBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, Color.parseColor("#FF49CC"))
    }

    override fun setUp() {

    }
}