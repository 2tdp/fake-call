package com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.picture.PicModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemPicBinding
import javax.inject.Inject

class PictureAdapter @Inject constructor(): RecyclerView.Adapter<PictureAdapter.PicHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private var lstPic = mutableListOf<PicModel>()
    private var w = 0f

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback
        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstPic: MutableList<PicModel>) {
        this.lstPic = lstPic

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicHolder {
        return PicHolder(ItemPicBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstPic.isNotEmpty()) lstPic.size else 0
    }

    override fun onBindViewHolder(holder: PicHolder, position: Int) {
        holder.onBind(position)
    }

    inner class PicHolder(private val binding: ItemPicBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.layoutParams =
                RelativeLayout.LayoutParams((27.78f * w).toInt(), (41.667f * w).toInt()).apply {
                    leftMargin = (3.33f * w).toInt()
                    rightMargin = (3.33f * w).toInt()
                    topMargin = (3.33f * w).toInt()
                    bottomMargin = (3.33f * w).toInt()
                }
        }

        fun onBind(position: Int) {
            val pic = lstPic[position]

            Glide.with(context)
                .asBitmap()
                .load("file:///android_asset/background/${pic.uri}")
                .placeholder(R.drawable.ic_placeholder_rec)
                .into(binding.root)

            binding.root.setOnClickListener { callback.callBack(pic, position) }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}