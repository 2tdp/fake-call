package com.remi.fakecall.prankfriend.colorphone.activity.ui.theme

import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseActivity
import com.remi.fakecall.prankfriend.colorphone.databinding.ActivityPreviewBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.PREVIEW_THEME
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_IMAGE_APP
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_GIF_ONLINE
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_IMAGE_USER
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.UtilsBitmap

class PreviewActivity: BaseActivity<ActivityPreviewBinding>(ActivityPreviewBinding::inflate) {

    override fun isVisible(): Boolean {
        return false
    }

    override fun setUp() {
        val theme = DataLocalManager.getThemePreview(PREVIEW_THEME)
        binding.ivBack.setOnClickListener { finish() }
        binding.vTheme.apply {
            when (theme.typeImages) {
                TYPE_IMAGE_APP -> {
                    isImage = true
                    bmBg = Bitmap.createScaledBitmap(
                        UtilsBitmap.getBitmapFromAsset(context, "background", theme.uriImages)!!,
                        (w * 100).toInt(), resources.displayMetrics.heightPixels, false
                    )
                }

                TYPE_IMAGE_USER -> {
                    isImage = true
                    bmBg = Bitmap.createScaledBitmap(
                        UtilsBitmap.getBitmapFromUri(context, Uri.parse(theme.uriImages))!!,
                        (w * 100).toInt(), resources.displayMetrics.heightPixels, false
                    )
                }

                TYPE_GIF_ONLINE -> {
                    isImage = false

                    Glide.with(this@PreviewActivity)
                        .asGif()
                        .load(theme.gif.large)
                        .placeholder(R.drawable.ic_placeholder_rec)
                        .into(binding.ivGif)
                }
                else -> {
                    isImage = false
                }
            }
            this.themeModel = theme
        }
    }
}