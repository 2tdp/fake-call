package com.remi.fakecall.prankfriend.colorphone.viewcustom

import android.view.View

interface OnCalledResult {
    fun onDown(v: View)
    fun onMove(isAnswer: Boolean, isDeny: Boolean)
    fun onUp(v: View, value: Int)
}