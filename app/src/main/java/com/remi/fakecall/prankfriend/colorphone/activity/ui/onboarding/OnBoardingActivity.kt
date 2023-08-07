package com.remi.fakecall.prankfriend.colorphone.activity.ui.onboarding

import android.graphics.Color
import android.media.RingtoneManager
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseActivity
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivity
import com.remi.fakecall.prankfriend.colorphone.adapter.DepthPageTransformer
import com.remi.fakecall.prankfriend.colorphone.databinding.ActivityOnBoardingBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.helpers.FIRST_INSTALL
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_GO_HOME_END_CALL
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_RING_TIME
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_SENSOR_PROXIMITY
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_SOUND
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_TALK_TIME
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_URI_RINGTONE
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_VIBRATE
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_VOLUME
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, Color.WHITE)
    }

    override fun setUp() {
        DataLocalManager.setCheck(SETTINGS_GO_HOME_END_CALL, false)
        DataLocalManager.setCheck(SETTINGS_SENSOR_PROXIMITY, true)
        DataLocalManager.setLong(34 * 1000L, SETTINGS_RING_TIME)
        DataLocalManager.setLong(84 * 1000L, SETTINGS_TALK_TIME)
        DataLocalManager.setCheck(SETTINGS_SOUND, true)
        DataLocalManager.setOption(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE).toString(), SETTINGS_URI_RINGTONE)
        DataLocalManager.setInt(84, SETTINGS_VOLUME)
        DataLocalManager.setCheck(SETTINGS_VIBRATE, true)

        binding.tvContinue.createBackground(
            intArrayOf(
                ContextCompat.getColor(this, R.color.main_color),
                ContextCompat.getColor(this, R.color.main_color2)
            ), (2.5f * w).toInt(), -1, -1
        )

        binding.tvContinue.setOnClickListener {
            if (binding.viewPager.currentItem == 0)
                binding.viewPager.setCurrentItem(1, true)
            else {
                startIntent(MainActivity::class.java.name, false)
                DataLocalManager.setFirstInstall(FIRST_INSTALL, true)
            }
        }

        val pageAdapter = PagerOnBoardingAdapter(this).apply {
            setData(ArrayList<Int>().apply {
                add(R.drawable.img_boarding)
                add(R.drawable.img_boarding_2)
            })
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.tvBoarding.text = getString(R.string.your_security_is_our_top_priority)
                    binding.tvDesBoarding.text = getString(R.string.des_boarding)
                    binding.tvContinue.text = getString(R.string.str_continue)
                } else {
                    binding.tvBoarding.text = getString(R.string.this_app_is_just_a_fake_call)
                    binding.tvDesBoarding.text = getString(R.string.des_boarding_2)
                    binding.tvContinue.text = getString(R.string.get_start)
                }
            }
        })

        binding.viewPager.apply {
            setPageTransformer(DepthPageTransformer())
            adapter = pageAdapter
        }

        binding.indicator.attachTo(binding.viewPager)
    }
}