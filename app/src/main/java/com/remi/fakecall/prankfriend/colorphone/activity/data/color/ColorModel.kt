package com.remi.fakecall.prankfriend.colorphone.activity.data.color

import java.io.Serializable

data class ColorModel(
    var colorStart: Int,
    var colorEnd: Int,
    var alpha: Int,
    var direc: Int,
    var isCheck: Boolean
) : Serializable