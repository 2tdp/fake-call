package com.remi.fakecall.prankfriend.colorphone.activity.ui.onboarding

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemOnBoardingBinding

class PagerOnBoardingAdapter(context: Context): RecyclerView.Adapter<PagerOnBoardingAdapter.PagerHolder>() {

    private val context: Context
    private var lstPage: ArrayList<Int>
    private var w = 0f

    init {
        this.context = context
        this.lstPage = ArrayList()
        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstItem: ArrayList<Int>) {
        this.lstPage = lstItem

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerHolder {
        return PagerHolder(ItemOnBoardingBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        if (lstPage.isNotEmpty()) return lstPage.size
        return 0
    }

    override fun onBindViewHolder(holder: PagerHolder, position: Int) {
        holder.onBind(position)
    }

    inner class PagerHolder(private val binding: ItemOnBoardingBinding): ViewHolder(binding.root) {

        fun onBind(position: Int) {
            val item = lstPage[position]

            if (position == 0) {
                binding.itemBoarding.layoutParams.width = (78.889f * w).toInt()
                binding.itemBoarding.layoutParams.height = (81.11f * w).toInt()
            } else {
                binding.itemBoarding.layoutParams.width = (72.22f * w).toInt()
                binding.itemBoarding.layoutParams.height = (70.83f * w).toInt()
            }

            Glide.with(context)
                .asBitmap()
                .load(item)
                .into(binding.itemBoarding)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}