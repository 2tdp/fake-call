package com.remi.fakecall.prankfriend.colorphone.activity.data.contact

data class ContactModel(
    var id: String = "-1",
    var name: String = "Private Number",
    var thumb: String = "",
    var contact: String = "+84 3457 80498",
    var ringtone: String = "",
    var lookupKey: String = "",
    var isTitle: Boolean = false,
    var isFake: Boolean = true
) {
}