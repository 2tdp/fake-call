package com.remi.fakecall.prankfriend.colorphone.viewcustom

import android.os.Handler
import android.os.Looper

class CounterTimer(private var isIncrease: Boolean, listener: OnTimerTickListener) {

    interface OnTimerTickListener {
        fun onTimerTick(duration: Array<String>)
    }

    private var handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null


    var isRun = false
    var duration = 0L
    private var delay = 140L

    init {
        runnable = Runnable {
            if (isIncrease) duration += delay
            else duration -= delay
            handler.postDelayed(runnable!!, 134L)
            listener.onTimerTick(format())
        }
    }

    fun start() {
        isRun = true
        handler.postDelayed(runnable!!, delay)
    }

    fun startWithDuration(duration: Long) {
        this.duration = duration
        isRun = true
        handler.postDelayed(runnable!!, delay)
    }

    fun pause() {
        isRun = false
        handler.removeCallbacks(runnable!!)
    }

    fun stop() {
        isRun = false
        handler.removeCallbacks(runnable!!)
    }

    fun format(): Array<String> {
        val millis = duration % 1000
        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60
        val hours = (duration / (1000 * 60 * 60))

        if (duration < 0L) stop()
        return if (isIncrease) {
            if (hours > 0) arrayOf(
                "%02d".format(millis / 10),
                "%02d".format(seconds),
                "%02d".format(minutes),
                "%02d".format(hours)
            )
            else arrayOf("%02d".format(millis / 10), "%02d".format(seconds), "%02d".format(minutes))
        } else arrayOf("%02d".format(seconds), "%02d".format(minutes), "%02d".format(hours))
    }
}