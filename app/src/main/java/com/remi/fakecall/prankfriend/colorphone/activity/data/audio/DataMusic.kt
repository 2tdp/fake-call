package com.remi.fakecall.prankfriend.colorphone.activity.data.audio

import android.content.ContentUris
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import com.remi.fakecall.prankfriend.colorphone.MyApp
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_SOUNDS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object DataMusic {

     fun getSongList(): Flow<MutableList<MusicModel>> {

         val lstMusic = mutableListOf<MusicModel>()
        // To get all music files on the device.
        val selection = StringBuilder("is_music != 0 AND title != ''")

        // Display audios in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val cursor = MyApp.ctx.contentResolver.query(
            EXTERNAL_CONTENT_URI,
            null,
            selection.toString(),
            null,
            sortOrder
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val contentUri = ContentUris.withAppendedId(EXTERNAL_CONTENT_URI, id)

                lstMusic.add(MusicModel(
                    id, title.replace(title.substring(title.lastIndexOf("."), title.length), ""),
                    duration, data, album ?: "", contentUri.toString(),false, false
                ))
            } while (cursor.moveToNext())
        }

        cursor?.close()

         return flow { emit(lstMusic) }
    }

    private fun checkFavorite(id: Long, lstFavorite: MutableList<MusicModel>) : Boolean {
        val item = lstFavorite.filter { it.id == id }
        if (item.isNotEmpty()) return true

        return false
    }
}