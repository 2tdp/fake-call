package com.remi.fakecall.prankfriend.colorphone.activity.data.audio

import com.remi.fakecall.prankfriend.colorphone.MyApp
import com.remi.fakecall.prankfriend.colorphone.helpers.FOLDER_RECORD
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_RECORDINGS
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.*

object DataRecord {

    fun getRecordSave(): Flow<MutableList<MusicModel>> {
        val lstRecord = ArrayList<MusicModel>()

        val filesApp = getTotalFile()
        if (!filesApp.isNullOrEmpty()) {
            for (f in filesApp) {
                if (!f.name.contains(".mp3")) continue

                lstRecord.add(MusicModel(-1, f.name, 0L, f.path, "record", "", false, false))
            }
        }

        return flow { emit(lstRecord) }
    }

    private fun getTotalFile(): Array<File>? {
        val directory = File(Utils.getStore(MyApp.ctx) + "/" + FOLDER_RECORD)
        return directory.listFiles()
    }
}