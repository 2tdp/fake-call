package com.remi.fakecall.prankfriend.colorphone.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.remi.fakecall.prankfriend.colorphone.activity.data.color.ColorModel
import com.remi.fakecall.prankfriend.colorphone.helpers.*
import com.remi.fakecall.prankfriend.colorphone.utils.Utils

fun View.loadBitmapFromView(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    layout(left, top, right, bottom)
    draw(canvas)
    return bitmap
}

fun View.createBackground(colorArr: IntArray, border: Int, stroke: Int, colorStroke: Int) {
    background = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = border.toFloat()
        if (stroke != -1) setStroke(stroke, colorStroke)

        if (colorArr.size >= 2) {
            colors = colorArr
            gradientType = GradientDrawable.LINEAR_GRADIENT
        } else setColor(colorArr[0])
    }
}

fun View.createColor(color: ColorModel, border: Int) {
    background =
        Utils.createBackground(intArrayOf(color.colorStart, color.colorEnd), border, -1, -1).apply {
            orientation = setDirection(color.direc)
        }
}

fun View.setDirection(pos: Int): GradientDrawable.Orientation {
    when (pos) {
        TYPE_T_B -> return GradientDrawable.Orientation.TOP_BOTTOM
        TYPE_BR_TL -> return GradientDrawable.Orientation.BR_TL
        TYPE_B_T -> return GradientDrawable.Orientation.BOTTOM_TOP
        TYPE_R_L -> return GradientDrawable.Orientation.RIGHT_LEFT
        TYPE_TL_BR -> return GradientDrawable.Orientation.TL_BR
        TYPE_TR_BL -> return GradientDrawable.Orientation.TR_BL
    }
    return GradientDrawable.Orientation.TOP_BOTTOM
}