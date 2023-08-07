package com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.gif.GifModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemGifBinding
import javax.inject.Inject

class GifAdapter @Inject constructor(): RecyclerView.Adapter<GifAdapter.GifHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private var lstGif = mutableListOf<GifModel>()
    private var w = 0f

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback

        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstGif: MutableList<GifModel>) {
        this.lstGif = lstGif

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifHolder {
        return GifHolder(ItemGifBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstGif.isNotEmpty()) lstGif.size else 0
    }

    override fun onBindViewHolder(holder: GifHolder, position: Int) {
        holder.onBind(position)
    }

    inner class GifHolder(private val binding: ItemGifBinding): RecyclerView.ViewHolder(binding.root) {

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
            val gif = lstGif[position]

            Glide.with(context)
                .asGif()
                .load(gif.large)
                .placeholder(R.drawable.ic_placeholder_rec)
                .into(binding.ivGif)

            binding.root.setOnClickListener { callback.callBack(gif, position) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}