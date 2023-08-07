package com.remi.fakecall.prankfriend.colorphone.sharepref

import android.content.Context
import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.MusicModel
import com.google.gson.Gson
import com.remi.fakecall.prankfriend.colorphone.activity.data.CallModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.contact.ContactModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ThemeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class DataLocalManager {
    private var mySharedPreferences: MySharePreferences? = null

    companion object {
        private var instance: DataLocalManager? = null
        fun init(context: Context) {
            instance = DataLocalManager()
            instance!!.mySharedPreferences = MySharePreferences(context)
        }

        private fun getInstance(): DataLocalManager? {
            if (instance == null) instance = DataLocalManager()
            return instance
        }

        fun setFirstInstall(key: String?, isFirst: Boolean) {
            getInstance()!!.mySharedPreferences!!.putBooleanValue(key, isFirst)
        }

        fun getFirstInstall(key: String?): Boolean {
            return getInstance()!!.mySharedPreferences!!.getBooleanValue(key)
        }

        fun setCheck(key: String?, volumeOn: Boolean) {
            getInstance()!!.mySharedPreferences!!.putBooleanValue(key, volumeOn)
        }

        fun getCheck(key: String?): Boolean {
            return getInstance()!!.mySharedPreferences!!.getBooleanValue(key)
        }

        fun setOption(option: String?, key: String?) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, option)
        }

        fun getOption(key: String?): String? {
            return getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
        }

        fun setInt(count: Int, key: String?) {
            getInstance()!!.mySharedPreferences!!.putIntWithKey(key, count)
        }

        fun getInt(key: String?): Int {
            return getInstance()!!.mySharedPreferences!!.getIntWithKey(key, -1)
        }

        fun setLong(count: Long, key: String?) {
            getInstance()!!.mySharedPreferences!!.putLongWithKey(key, count)
        }

        fun getLong(key: String?): Long {
            return getInstance()!!.mySharedPreferences!!.getLongWithKey(key, -1L)
        }

        fun setListTimeStamp(key: String?, lstTimeStamp: ArrayList<String?>?) {
            val gson = Gson()
            val jsonArray = gson.toJsonTree(lstTimeStamp).asJsonArray
            val json = jsonArray.toString()
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, json)
        }

        fun getListTimeStamp(key: String?): ArrayList<String> {
            val lstTimeStamp = ArrayList<String>()
            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
            try {
                val jsonArray = JSONArray(strJson)
                for (i in 0 until jsonArray.length()) {
                    lstTimeStamp.add(jsonArray[i].toString())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstTimeStamp
        }

        fun setListFavorite(lstBorder: MutableList<MusicModel>, key: String) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, Gson().toJsonTree(lstBorder).asJsonArray.toString())
        }

        fun getListFavorite(key: String): MutableList<MusicModel> {
            val lstBorder = mutableListOf<MusicModel>()
            try {
                val jsonArray = JSONArray(getInstance()!!.mySharedPreferences!!.getStringWithKey(key, ""))
                for (i in 0 until jsonArray.length()) {
                    lstBorder.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(), MusicModel::class.java))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstBorder
        }

        fun setListFakeContact(lstFakeContact: MutableList<ContactModel>, key: String) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, Gson().toJsonTree(lstFakeContact).asJsonArray.toString())
        }

        fun getListFakeContact(key: String): MutableList<ContactModel> {
            val lstFakeContact = mutableListOf<ContactModel>()
            try {
                val jsonArray = JSONArray(getInstance()!!.mySharedPreferences!!.getStringWithKey(key, ""))
                for (i in 0 until jsonArray.length())
                    lstFakeContact.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(), ContactModel::class.java))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstFakeContact
        }

        fun setThemePreview(key: String, themeModel: ThemeModel) {
            getInstance()!!.mySharedPreferences?.putStringWithKey(key, Gson().toJsonTree(themeModel).asJsonObject.toString())
        }

        fun getThemePreview(key: String): ThemeModel {
            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")!!
            var themeModel = ThemeModel()
            try {
                themeModel = Gson().fromJson(JSONObject(strJson).toString(), ThemeModel::class.java)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return themeModel
        }

        fun setListMyTheme(lstMyTheme: MutableList<ThemeModel>, key: String) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, Gson().toJsonTree(lstMyTheme).asJsonArray.toString())
        }

        fun getListMyTheme(key: String): MutableList<ThemeModel> {
            val lstMyTheme = mutableListOf<ThemeModel>()
            try {
                val jsonArray = JSONArray(getInstance()!!.mySharedPreferences!!.getStringWithKey(key, ""))
                for (i in 0 until jsonArray.length())
                    lstMyTheme.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(), ThemeModel::class.java))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstMyTheme
        }

        fun getListMyThemeFlow(key: String): Flow<MutableList<ThemeModel>> {
            val lstMyTheme = mutableListOf<ThemeModel>()
            try {
                val jsonArray = JSONArray(getInstance()!!.mySharedPreferences!!.getStringWithKey(key, ""))
                for (i in 0 until jsonArray.length())
                    lstMyTheme.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(), ThemeModel::class.java))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return flow { emit(lstMyTheme) }
        }

        fun setScheduleCall(key: String, callModel: CallModel) {
            getInstance()!!.mySharedPreferences?.putStringWithKey(key, Gson().toJsonTree(callModel).asJsonObject.toString())
        }

        fun getScheduleCall(key: String): CallModel {
            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")!!
            var callModel = CallModel()
            try {
                callModel = Gson().fromJson(JSONObject(strJson).toString(), CallModel::class.java)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return callModel
        }

        fun setListSchedule(lstSchedule: MutableList<CallModel>, key: String) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, Gson().toJsonTree(lstSchedule).asJsonArray.toString())
        }

        fun getListSchedule(key: String): MutableList<CallModel> {
            val lstSchedule = mutableListOf<CallModel>()
            try {
                val jsonArray = JSONArray(getInstance()!!.mySharedPreferences!!.getStringWithKey(key, ""))
                for (i in 0 until jsonArray.length())
                    lstSchedule.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(), CallModel::class.java))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstSchedule
        }

        fun getListHistorySchedule(key: String): Flow<MutableList<CallModel>> {
            val lstSchedule = mutableListOf<CallModel>()
            try {
                val jsonArray = JSONArray(getInstance()!!.mySharedPreferences!!.getStringWithKey(key, ""))
                for (i in 0 until jsonArray.length())
                    lstSchedule.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(), CallModel::class.java))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return flow { emit(lstSchedule) }
        }

//        fun setListBucket(lstBucket: ArrayList<BucketPicModel>, key: String) {
//            val gson = Gson()
//            val jsonArray = gson.toJsonTree(lstBucket).asJsonArray
//            val json = jsonArray.toString()
//            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, json)
//        }
//
//        fun getListBucket(key: String): ArrayList<BucketPicModel> {
//            val gson = Gson()
//            var jsonObject: JSONObject
//            val lstBucket: ArrayList<BucketPicModel> = ArrayList()
//            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
//            try {
//                val jsonArray = JSONArray(strJson)
//                for (i in 0 until jsonArray.length()) {
//                    jsonObject = jsonArray.getJSONObject(i)
//                    lstBucket.add(gson.fromJson(jsonObject.toString(), BucketPicModel::class.java))
//                }
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//            return lstBucket
//        }
    }
}