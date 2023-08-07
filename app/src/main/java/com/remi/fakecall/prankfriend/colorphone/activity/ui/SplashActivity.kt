package com.remi.fakecall.prankfriend.colorphone.activity.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseActivity
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivity
import com.remi.fakecall.prankfriend.colorphone.activity.ui.onboarding.OnBoardingActivity
import com.remi.fakecall.prankfriend.colorphone.databinding.ActivitySplashBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.FIRST_INSTALL
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager

@SuppressLint("CustomSplashScreen")
class SplashActivity: BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, Color.WHITE)
    }

    private var progressStatus = 0

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })

        binding.sbLoading.setMax(100)

        Thread {
            while (progressStatus < 99) {
                progressStatus += 1
                if (progressStatus > 55)
                    Handler(Looper.getMainLooper()).post { binding.beat.valueAnimator?.start() }
                Handler(Looper.getMainLooper()).post { binding.sbLoading.setProgress(progressStatus) }
                try {
                    Thread.sleep(34)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            Handler(Looper.getMainLooper()).post {
                if (!DataLocalManager.getFirstInstall(FIRST_INSTALL))
                    startIntent(OnBoardingActivity::class.java.name, true)
                else startIntent(MainActivity::class.java.name, true)
                finish()
            }
        }.start()
    }
}