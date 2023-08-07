package com.remi.fakecall.prankfriend.colorphone.activity.data.gif

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.StandardCharsets

object JsonReader {

    @Throws(IOException::class)
    private fun readAll(rd: Reader): String {
        val sb = StringBuilder()
        var cp: Int
        while (rd.read().also { cp = it } != -1) {
            sb.append(cp.toChar())
        }
        return sb.toString()
    }

    @Throws(IOException::class, JSONException::class)
    fun readJsonFromUrl(url: String?): JSONArray {
        return JSONArray(readAll(BufferedReader(InputStreamReader(URL(url).openStream(), StandardCharsets.UTF_8))))
    }

    @Throws(IOException::class, JSONException::class)
    fun readJsonFromUrlObject(url: String?): JSONObject {
        return JSONObject(readAll(BufferedReader(InputStreamReader(URL(url).openStream(), StandardCharsets.UTF_8))))
    }

    fun readFromFile(url: String): String {
        var result = ""
        try {
            val bufferedReader = BufferedReader(FileReader(File(url)))
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()

            // This responce will have Json Format String
            result = stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }
}