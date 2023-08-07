package com.remi.fakecall.prankfriend.colorphone.activity.data.theme

import android.graphics.Color
import com.remi.fakecall.prankfriend.colorphone.activity.data.color.ColorModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.gif.GifModel
import com.remi.fakecall.prankfriend.colorphone.helpers.MY_THEME
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_T_B

data class ThemeModel (
    var name: String = "",
    var nameNumb: String = "Private Number",
    var numb: String = "Mobile +12 091 1222 123",
    var style: String = MY_THEME,
    var color: ColorModel =
        ColorModel(Color.parseColor("#1F94C6"), Color.parseColor("#1F94C6"), 100, TYPE_T_B, false),
    var gif: GifModel = GifModel(),
    var uriPreview: String = "",
    var uriImages: String = "",
    var typeImages: String = "",
    var buttonStyle: ButtonStyleModel = ButtonStyleModel("theme1", true),
    var isChoose: Boolean = false
)