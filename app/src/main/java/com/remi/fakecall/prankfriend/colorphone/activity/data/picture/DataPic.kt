package com.remi.fakecall.prankfriend.colorphone.activity.data.picture

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.remi.fakecall.prankfriend.colorphone.MyApp
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.utils.UtilsBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.IOException

object DataPic {

    private val EXTERNAL_COLUMNS_PIC = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        "\"" + MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "\""
    )

    private val EXTERNAL_COLUMNS_PIC_API_Q = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.DATA
    )

    fun getBucketPictureList(): Flow<MutableList<BucketPicModel>> {

        val lstBucket = mutableListOf<BucketPicModel>()
        var lstPic = mutableListOf<PicModel>()
        val lstAll = mutableListOf<PicModel>()

        val CONTENT_URI: Uri
        val COLUMNS: Array<String>

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            CONTENT_URI = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            COLUMNS = EXTERNAL_COLUMNS_PIC_API_Q
        } else {
            CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            COLUMNS = EXTERNAL_COLUMNS_PIC
        }

        MyApp.ctx.contentResolver.query(
            CONTENT_URI,
            COLUMNS,
            null,
            null,
            MediaStore.Images.Media.DEFAULT_SORT_ORDER
        ).use { cursor ->

            val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {

                val id = cursor.getString(idColumn)
                val bucket = cursor.getString(bucketColumn)
                val data = cursor.getString(dataColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong())
                val size = UtilsBitmap.getImageSize(MyApp.ctx, Uri.parse(contentUri.toString()))
                val ratio = size[0] / size[1]

                if (File(data).canRead()) {
                    lstAll.add(PicModel(id, bucket, ratio, contentUri.toString(), false))
                    var check = false
                    if (lstBucket.isEmpty()) {
                        if (bucket != null) {
                            lstBucket.add(BucketPicModel(lstPic, bucket))
                            lstPic.add(PicModel(id, bucket, ratio, contentUri.toString(), false))
                        } else {
                            lstBucket.add(BucketPicModel(lstPic, ""))
                            lstPic.add(PicModel(id, "", ratio, contentUri.toString(), false))
                        }
                    } else {
                        for (i in lstBucket.indices) {
                            if (bucket == null) {
                                lstBucket[i].lstPic.add(PicModel(id, "", ratio, contentUri.toString(), false))
                                check = true
                                break
                            }
                            if (bucket == lstBucket[i].bucket) {
                                lstBucket[i].lstPic.add(PicModel(id, bucket, ratio, contentUri.toString(), false))
                                check = true
                                break
                            }
                        }
                        if (!check) {
                            lstPic = ArrayList()
                            lstPic.add(PicModel(id, bucket, ratio, contentUri.toString(), false))
                            lstBucket.add(BucketPicModel(lstPic, bucket!!))
                        }
                    }
                }
            }
            lstBucket.add(0, BucketPicModel(lstAll, MyApp.ctx.getString(R.string.all)))
        }

        return flow {
            emit(lstBucket)
        }
    }

    fun getPicAssets(name: String): Flow<MutableList<PicModel>> {
        val lstPicAsset = mutableListOf<PicModel>()
        try {
            val f = MyApp.ctx.assets.list(name)
            for (str in listOf(*f!!))
                lstPicAsset.add(PicModel(str.replace(".webp", ""), "Remi Images", -1F, str, false))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return flow { emit(lstPicAsset) }
    }
}