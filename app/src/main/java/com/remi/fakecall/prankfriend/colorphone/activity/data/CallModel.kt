package com.remi.fakecall.prankfriend.colorphone.activity.data

import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.MusicModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.contact.ContactModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ThemeModel
import java.io.Serializable

data class CallModel(
    var idCall: Long = -1L,
    var timeSchedule: Long = 3000L,
    var uriAvatar: String = "",
    var contact: ContactModel = ContactModel(),
    var voice: MusicModel? = null,
    var theme: ThemeModel = ThemeModel(),
    var isAnswer: Boolean = false
) : Serializable