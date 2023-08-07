package com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ThemeModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemThemeBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_THEME
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_IMAGE_APP
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_GIF_ONLINE
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_IMAGE_USER
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.UtilsBitmap
import javax.inject.Inject

class ThemeAdapter @Inject constructor() : RecyclerView.Adapter<ThemeAdapter.ThemeHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private var lstTheme = mutableListOf<ThemeModel>()
    private var w = 0f
    private var isFirst = true

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback

        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstTheme: MutableList<ThemeModel>) {
        this.lstTheme = lstTheme

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeHolder {
        return ThemeHolder(ItemThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstTheme.isNotEmpty()) lstTheme.size else 0
    }

    override fun onBindViewHolder(holder: ThemeHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ThemeHolder(private val binding: ItemThemeBinding) : ViewHolder(binding.root) {

        init {
            binding.root.layoutParams =
                RelativeLayout.LayoutParams((29.167f * w).toInt(), (55.8333f * w).toInt()).apply {
                    leftMargin = (1.32f * w).toInt()
                    rightMargin = (1.32f * w).toInt()
                    topMargin = (2.22f * w).toInt()
                    bottomMargin = (2.22f * w).toInt()
                }
            binding.tvName.apply {
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            }
        }

        fun onBind(position: Int) {
            val theme = lstTheme[position]

            if (theme.isChoose) binding.ivChoose.visibility = View.VISIBLE
            else binding.ivChoose.visibility = View.GONE

            binding.tvName.text = theme.name
            Glide.with(context)
                .asBitmap()
                .load(theme.uriPreview)
                .placeholder(R.drawable.ic_placeholder_rec)
                .into(binding.vTheme)

            binding.root.setOnClickListener {
                setCurrent(position)
                callback.callBack(theme, position)
            }
            binding.ivPreview.setOnClickListener {
                setCurrent(position)
                callback.callBack(theme, -1)
            }
        }
    }

    fun setCurrent(position: Int) {
        for (pos in lstTheme.indices) lstTheme[pos].isChoose = pos == position

        if (isFirst) {
            Thread {
                val lstTheme = DataLocalManager.getListMyTheme(LIST_THEME)
                for (theme in lstTheme) theme.isChoose = false
                DataLocalManager.setListMyTheme(lstTheme, LIST_THEME)
                isFirst = false
            }.start()
        }
        notifyChange()
    }

    fun getCurrent() {
        for (theme in lstTheme) if (theme.isChoose) callback.callBack(theme, lstTheme.indexOf(theme))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}