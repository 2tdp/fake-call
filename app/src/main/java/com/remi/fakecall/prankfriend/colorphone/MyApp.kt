package com.remi.fakecall.prankfriend.colorphone

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import dagger.hilt.android.HiltAndroidApp
import java.security.AccessController.getContext
import kotlin.reflect.KParameter

@HiltAndroidApp
class MyApp : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
    }

    override fun onCreate() {
        DataLocalManager.init(applicationContext)
        ctx = applicationContext
        super.onCreate()
    }
}