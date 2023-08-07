package com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.picture.BucketPicModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemBucketBinding
import javax.inject.Inject

class BucketAdapter @Inject constructor(): RecyclerView.Adapter<BucketAdapter.BucketHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private var lstAlbums = mutableListOf<BucketPicModel>()
    private var w = 0f

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback
        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstAlbums: MutableList<BucketPicModel>) {
        this.lstAlbums = lstAlbums

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketHolder {
        return BucketHolder(ItemBucketBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstAlbums.isNotEmpty()) lstAlbums.size else 0
    }

    override fun onBindViewHolder(holder: BucketHolder, position: Int) {
        holder.onBind(position)
    }

    inner class BucketHolder(private val binding: ItemBucketBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {
            val bucket = lstAlbums[position]

            Glide.with(context)
                .load(Uri.parse(bucket.lstPic[0].uri))
                .placeholder(R.drawable.ic_placeholder_rec)
                .into(binding.ivBucket)

            binding.tvName.text = bucket.bucket
            binding.tvQuantity.text = bucket.lstPic.size.toString()

            binding.root.setOnClickListener { callback.callBack(bucket, position) }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}