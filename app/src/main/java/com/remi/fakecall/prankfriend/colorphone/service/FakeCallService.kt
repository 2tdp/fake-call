package com.remi.fakecall.prankfriend.colorphone.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telecom.Call
import android.util.Log
import com.remi.fakecall.prankfriend.colorphone.activity.data.CallModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.schedule.MyCallActivity
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_SCHEDULE
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_SCHEDULE_HISTORY
import com.remi.fakecall.prankfriend.colorphone.helpers.SCHEDULE_CALL
import com.remi.fakecall.prankfriend.colorphone.helpers.TAG
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class FakeCallService: Service() {

    companion object {
        var IS_SERVICE_RUNNING = false
    }

    private val timeFormat = "yyyy MMM dd hh:mm:s aaa"
    private lateinit var simpleFormat: SimpleDateFormat

    private lateinit var timer: Timer
    private lateinit var fullScreenIntent: Intent

    override fun onCreate() {
        super.onCreate()
        IS_SERVICE_RUNNING = true

        simpleFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        timer = Timer()
        fullScreenIntent = Intent(this, MyCallActivity::class.java)
        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return super.onStartCommand(intent, flags, startId)
        if (intent.action != null && intent.action.equals("stop_service", true)) {
            stopForeground(true)
            stopSelfResult(startId)
            return super.onStartCommand(intent, flags, startId)
        }

        timer.schedule(object : TimerTask() {
            override fun run() {
                checkSchedule()
            }
        }, 0, 1000)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun checkSchedule() {
        val lstSchedule = DataLocalManager.getListSchedule(LIST_SCHEDULE)
        for (schedule in lstSchedule) {
            if (simpleFormat.format(System.currentTimeMillis()) == simpleFormat.format(schedule.timeSchedule)) {
                DataLocalManager.setScheduleCall(SCHEDULE_CALL, schedule)
                startActivity(fullScreenIntent)
                removeSchedule(schedule)
            }
        }
    }

    private fun removeSchedule(schedule: CallModel) {
        val lstSchedule = DataLocalManager.getListSchedule(LIST_SCHEDULE)
        lstSchedule.remove(schedule)
        DataLocalManager.setListSchedule(lstSchedule, LIST_SCHEDULE)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        IS_SERVICE_RUNNING = false
    }
}