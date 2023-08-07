package com.remi.fakecall.prankfriend.colorphone.activity.data.audio

data class MusicModel(
    val id: Long = 0L,
    val songName: String = "",
    val duration: Long = 0L,
    val path: String = "",
    val album: String = "",
    val uri: String = "",
    var isPlay: Boolean = false,
    var isChoose: Boolean = false
)