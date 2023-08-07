package com.remi.fakecall.prankfriend.colorphone.callback

import java.util.Objects

interface ICallBackDimensional {
    fun callBackItem(objects: Objects, callBackItem: ICallBackItem)

    fun callBackCheck(objects: Objects, check: ICallBackCheck)
}