package com.remi.fakecall.prankfriend.colorphone.activity.data.theme

import com.remi.fakecall.prankfriend.colorphone.MyApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.util.ArrayList

object DataTheme {

    fun getAllStyleButton(): Flow<MutableList<ButtonStyleModel>> {
        val lstTheme = mutableListOf<ButtonStyleModel>()
        try {
            val f = MyApp.ctx.assets.list("theme")
            for (s in f!!) lstTheme.add(ButtonStyleModel(s, false))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return flow { emit(lstTheme) }
    }
}