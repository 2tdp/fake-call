package com.remi.fakecall.prankfriend.colorphone.activity.ui.schedule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.CallModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseActivity
import com.remi.fakecall.prankfriend.colorphone.databinding.ActivityScheduleBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.DialogAutostartPerBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.DialogPerOverlayBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.DialogScheduleBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.helpers.AUTO_START_PER
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_SCHEDULE
import com.remi.fakecall.prankfriend.colorphone.helpers.SCHEDULE_CALL
import com.remi.fakecall.prankfriend.colorphone.service.FakeCallService
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import com.remi.fakecall.prankfriend.colorphone.viewcustom.CounterTimer
import java.text.SimpleDateFormat
import java.util.Random


class ScheduleActivity : BaseActivity<ActivityScheduleBinding>(ActivityScheduleBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(
            ContextCompat.getColor(this, R.color.white_background),
            ContextCompat.getColor(this, R.color.white_background)
        )
    }

    lateinit var simpleDate: SimpleDateFormat
    private var isCheck = false
    private var isCheckAutoStart = false
    private var isFinish = false

    private var bindingDialog: DialogScheduleBinding? = null
    private var callModel: CallModel? = null

    private var schedule = ""
    private var duration = 0L

    var autoStartIntent: Intent? = null

    override fun setUp() {
        autoStartIntent = checkAutoStartPermission()
        callModel = DataLocalManager.getScheduleCall(SCHEDULE_CALL)
        setUpView()
        evenClick()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun evenClick() {
        binding.tv10S.setOnClickListener { chooseTimer("10s") }
        binding.tv30S.setOnClickListener { chooseTimer("30s") }
        binding.tv1m.setOnClickListener { chooseTimer("1m") }
        binding.tv5m.setOnClickListener { chooseTimer("5m") }
        binding.tv15m.setOnClickListener { chooseTimer("15m") }
        binding.tv30m.setOnClickListener { chooseTimer("30m") }
        binding.tvNow.setOnClickListener { chooseTimer("now") }

        binding.ivPickTime.setOnClickListener { actionPickTime(if (!binding.ctlTimer.isVisible) "show" else "edit") }
        binding.tvCancel.setOnClickListener { actionPickTime("hide") }
        binding.ivOceTime.setOnClickListener {
            simpleDate = SimpleDateFormat("EEE d MMM")
            val date =
                if (simpleDate.format(binding.vPickTime.date) == simpleDate.format(System.currentTimeMillis()))
                    "Today"
                else simpleDate.format(binding.vPickTime.date)
            simpleDate = SimpleDateFormat("HH:mm")
            val time = simpleDate.format(binding.vPickTime.date)

            binding.tvTimer.text = "$date - $time"
            actionPickTime("pick")
        }
        binding.ivEditTimer.setOnClickListener { actionPickTime("edit") }

        binding.tvDone.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this@ScheduleActivity))
                checkPerOverlay()
            else if (!isCheckAutoStart && autoStartIntent != null && !DataLocalManager.getCheck(AUTO_START_PER))
                checkPerAutoStart()
            else startCall()
        }
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setUpView() {
        chooseTimer("10s")
        binding.vPickTime.setIsAmPm(false)
        binding.tvDone.createBackground(
            intArrayOf(Color.parseColor("#FF9950"), Color.parseColor("#FD6740")),
            (2.5f * w).toInt(), -1, -1
        )
        binding.tvCancel.text = getString(R.string.cancel).uppercase()
        binding.vPickTime.setCurved(true)
    }

    private fun chooseTimer(option: String) {
        when (option) {
            "10s" -> {
                schedule = "10s"
                duration = 10 * 1000L
                binding.tv10S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)),
                        (55f * w).toInt(),
                        -1,
                        -1
                    )
                    setTextColor(Color.WHITE)
                }

                binding.tv30S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv1m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv5m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv15m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tvNow.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
            }

            "30s" -> {
                schedule = "30s"
                duration = 30 * 1000L
                binding.tv30S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)),
                        (55f * w).toInt(),
                        -1,
                        -1
                    )
                    setTextColor(Color.WHITE)
                }

                binding.tv10S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv1m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv5m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv15m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tvNow.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
            }

            "1m" -> {
                schedule = "1m"
                duration = 60 * 1000L
                binding.tv1m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)),
                        (55f * w).toInt(),
                        -1,
                        -1
                    )
                    setTextColor(Color.WHITE)
                }

                binding.tv10S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv5m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv15m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tvNow.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
            }

            "5m" -> {
                schedule = "5m"
                duration = 5 * 60 * 1000L
                binding.tv5m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)),
                        (55f * w).toInt(),
                        -1,
                        -1
                    )
                    setTextColor(Color.WHITE)
                }

                binding.tv10S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv1m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv15m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tvNow.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
            }

            "15m" -> {
                schedule = "15m"
                duration = 15 * 60 * 1000L
                binding.tv15m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)),
                        (55f * w).toInt(),
                        -1,
                        -1
                    )
                    setTextColor(Color.WHITE)
                }

                binding.tv10S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv1m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv5m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tvNow.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
            }

            "30m" -> {
                schedule = "30m"
                duration = 30 * 60 * 1000L
                binding.tv30m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)),
                        (55f * w).toInt(),
                        -1,
                        -1
                    )
                    setTextColor(Color.WHITE)
                }

                binding.tv10S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv1m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv5m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv15m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tvNow.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
            }

            "now" -> {
                schedule = "now"
                duration = 3 * 1000L
                binding.tvNow.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)),
                        (55f * w).toInt(),
                        -1,
                        -1
                    )
                    setTextColor(Color.WHITE)
                }

                binding.tv10S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv1m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv5m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv15m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
            }

            else -> {
                schedule = "pickTime"
                binding.tv10S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30S.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv1m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv5m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv15m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tv30m.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
                binding.tvNow.apply {
                    createBackground(
                        intArrayOf(ContextCompat.getColor(this@ScheduleActivity, R.color.white_background)),
                        (55f * w).toInt(),
                        (0.556f * w).toInt(),
                        ContextCompat.getColor(this@ScheduleActivity, R.color.main_text)
                    )
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, R.color.main_text))
                }
            }
        }
    }

    private fun actionPickTime(option: String) {
        chooseTimer("")
        when (option) {
            "show" -> {
                binding.ctlPickTime.animation =
                    AnimationUtils.loadAnimation(this@ScheduleActivity, R.anim.slide_down_in)
                binding.ctlPickTime.visibility = View.VISIBLE
            }

            "hide" -> {
                binding.ctlPickTime.animation =
                    AnimationUtils.loadAnimation(this@ScheduleActivity, R.anim.slide_up)
                binding.ctlPickTime.visibility = View.GONE
            }

            "pick" -> {
                binding.ctlPickTime.animation =
                    AnimationUtils.loadAnimation(this@ScheduleActivity, R.anim.slide_up)
                binding.ctlPickTime.visibility = View.GONE

                binding.ctlTimer.animation =
                    AnimationUtils.loadAnimation(this@ScheduleActivity, R.anim.slide_down_in)
                binding.ctlTimer.visibility = View.VISIBLE
            }

            "edit" -> {
                binding.ctlTimer.animation =
                    AnimationUtils.loadAnimation(this@ScheduleActivity, R.anim.slide_up)
                binding.ctlTimer.visibility = View.GONE

                binding.ctlPickTime.animation =
                    AnimationUtils.loadAnimation(this@ScheduleActivity, R.anim.slide_down_in)
                binding.ctlPickTime.visibility = View.VISIBLE
            }
        }
    }

    private fun checkPerOverlay() {
        isCheck = true
        val bindingDialog =
            DialogPerOverlayBinding.inflate(LayoutInflater.from(this@ScheduleActivity), null, false)
        val dialog = AlertDialog.Builder(this@ScheduleActivity, R.style.SheetDialog).create()
        dialog.apply {
            setView(bindingDialog.root)
            setCancelable(true)
            show()
        }

        bindingDialog.root.layoutParams.width = (83.33f * w).toInt()
        bindingDialog.root.layoutParams.height = (38.889f * w).toInt()


        bindingDialog.tvAllow.setOnClickListener {
            dialog.cancel()
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this@ScheduleActivity)) {
                requestPermission.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                })
            }
        }
        bindingDialog.tvDeny.setOnClickListener {
            dialog.cancel()
            showMessage(resources.getString(R.string.per_overlay))
        }
    }

    private var requestPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this@ScheduleActivity))
                showMessage(resources.getString(R.string.per_overlay))
        }

    private fun checkPerAutoStart() {
        val bindingAutoStart = DialogAutostartPerBinding.inflate(LayoutInflater.from(this), null, false)
        val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
        dialog.apply {
            setCancelable(true)
            setView(bindingAutoStart.root)
            show()
        }

        bindingAutoStart.root.layoutParams.width = (83.33f * w).toInt()
        bindingAutoStart.root.layoutParams.height = (40.46f * w).toInt()

        bindingAutoStart.tvAllow.setOnClickListener {
            dialog.cancel()
            isCheckAutoStart = true
            launcherAutoStart.launch(autoStartIntent)
        }

        bindingAutoStart.tvDeny.setOnClickListener { dialog.cancel() }
        bindingAutoStart.cbPer.setOnClickListener { DataLocalManager.setCheck(AUTO_START_PER, true) }
    }

    var launcherAutoStart = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        /*if xiaomi: check to start in background*/
        try {
            if (isXiaomi()) {
                val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity")
                intent.putExtra("extra_pkgname", packageName)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startCall() {
        isCheck = false
        addCallToList()
        bindingDialog = DialogScheduleBinding.inflate(LayoutInflater.from(this), null, false)
        bindingDialog?.let { it.tvTitle.text = "Call Scheduled. Starts in $schedule seconds" }

        val dialog = AlertDialog.Builder(this@ScheduleActivity, R.style.SheetDialog).create()
        dialog.apply {
            setView(bindingDialog?.root)
            setCancelable(true)
            show()
        }

        bindingDialog?.let {
            it.root.layoutParams.width = (83.33f * w).toInt()
            it.root.layoutParams.height = (27.778f * w).toInt()

            it.tvOce.setOnClickListener {
                dialog.cancel()
                finish()
            }
        }

        if (!Utils.isMyServiceRunning(this@ScheduleActivity, FakeCallService::class.java))
            startService(Intent(this@ScheduleActivity, FakeCallService::class.java))

        CounterTimer(false, object : CounterTimer.OnTimerTickListener {
            override fun onTimerTick(duration: Array<String>) {
                isFinish = true
                bindingDialog?.let {
                    it.tvTitle.text = "Call Scheduled. Starts in ${duration[1]}:${duration[0]} seconds"
                }
            }
        }).startWithDuration(duration)
    }

    private fun addCallToList() {
        val lstCall = DataLocalManager.getListSchedule(LIST_SCHEDULE)

        callModel?.let {
            if (it.idCall == -1L) it.idCall = getId(lstCall)
            it.timeSchedule = when (schedule) {
                "10s" -> System.currentTimeMillis() + 10 * 1000L
                "30s" -> System.currentTimeMillis() + 30 * 1000L
                "1m" -> System.currentTimeMillis() + 60 * 1000L
                "5m" -> System.currentTimeMillis() + 5 * 60 * 1000L
                "15m" -> System.currentTimeMillis() + 15 * 60 * 1000L
                "30m" -> System.currentTimeMillis() + 30 * 60 * 1000L
                "now" -> System.currentTimeMillis() + 3 * 1000L
                "pickTime" -> binding.vPickTime.date.time
                else -> System.currentTimeMillis() + 3 * 1000L
            }
            duration = binding.vPickTime.date.time
        }
        lstCall.add(callModel!!)
        DataLocalManager.setListSchedule(lstCall, LIST_SCHEDULE)
    }

    private fun getId(lstCall: MutableList<CallModel>): Long {
        val id = Random().nextInt()
        val lstTemp = lstCall.filter { it.idCall == id.toLong() }
        if (lstTemp.isNotEmpty()) getId(lstCall) else return id.toLong()

        return -1
    }

    override fun onResume() {
        super.onResume()
        if (isCheck)
            if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this@ScheduleActivity)) {
                if (!isCheckAutoStart && autoStartIntent != null && !DataLocalManager.getCheck(AUTO_START_PER))
                    checkPerAutoStart()
                else startCall()
            }
            else showMessage(resources.getString(R.string.per_overlay))

        if (isFinish) finish()
    }

    private fun checkAutoStartPermission(): Intent? {
        val POWERMANAGER_INTENTS = arrayOf(
            Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            Intent().setComponent(ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")),
            Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            Intent().setComponent(ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity")),
            Intent().setComponent(ComponentName("com.transsion.phonemanager", "com.itel.autobootmanager.activity.AutoBootMgrActivity"))
        )

        for (intent in POWERMANAGER_INTENTS)
            if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) return intent
        return null
    }

    private fun isXiaomi(): Boolean {
        return "xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
    }
}