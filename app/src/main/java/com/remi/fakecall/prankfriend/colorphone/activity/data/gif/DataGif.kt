package com.remi.fakecall.prankfriend.colorphone.activity.data.gif

import com.google.gson.Gson
import com.remi.fakecall.prankfriend.colorphone.MyApp
import com.remi.fakecall.prankfriend.colorphone.helpers.GIF_FOLDER
import com.remi.fakecall.prankfriend.colorphone.helpers.URL_GIF_ONL
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import java.io.File

object DataGif {

    fun getAllGif(): Flow<MutableList<GifModel>> {

        val lstGif = mutableListOf<GifModel>()

        if (Utils.getConnectionType(MyApp.ctx) == 1 || Utils.getConnectionType(MyApp.ctx) == 2) {
            try {
                val json = JsonReader.readJsonFromUrl(URL_GIF_ONL)
                for (i in 0 until json.length()) {
                    val gif = Gson().fromJson(json[i].toString(), GifModel::class.java).apply {
                        large = this.large.replace("www.dropbox", "dl.dropboxusercontent")
                        thumb = this.thumb.replace("www.dropbox", "dl.dropboxusercontent")
                        isChoose = false
                        isOnl = checkThemeApp(this.name)
                    }
                    lstGif.add(gif)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val filesApp = getTotalFile()
            filesApp?.let {
                if (it.isNotEmpty())
                    for (f in filesApp)
                        lstGif.add(GifModel(f.name, f.path + "/thumb.png", f.path + "/large.png", checkThemeApp(f.name), false))
            }
        }

        return flow { emit(lstGif) }
    }

    private fun getTotalFile(): Array<out File>? {
        return File(Utils.getStore(MyApp.ctx) + "/$GIF_FOLDER").listFiles()
    }

    private fun checkThemeApp(nameTheme: String): Boolean {
        val file = File(Utils.getStore(MyApp.ctx) + "/" + GIF_FOLDER + nameTheme)
        return file.exists()
    }
}