package com.remi.fakecall.prankfriend.colorphone.extensions

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.utils.Utils

fun TextView.textCustom(content: String, color: Int, textSize: Float, font: String, context: Context) {
    text = content
    setTextColor(color)
    setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    typeface = try {
        Utils.getTypeFace(font.substring(0, 6), "$font.ttf", context)
    } catch (e: Exception) {
        Utils.getTypeFace(font.substring(0, 6), "$font.otf", context)
    }
}

fun TextView.setGradient(colors: IntArray) {
    paint.shader = LinearGradient(
        0f,
        0f,
        paint.measureText(text.toString()),
        textSize,
        colors, null, Shader.TileMode.CLAMP)
    invalidate()
}