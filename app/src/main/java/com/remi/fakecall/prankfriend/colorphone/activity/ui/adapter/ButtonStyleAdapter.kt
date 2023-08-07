package com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ButtonStyleModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemButtonStyleBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.utils.UtilsBitmap
import javax.inject.Inject

class ButtonStyleAdapter @Inject constructor() :
    RecyclerView.Adapter<ButtonStyleAdapter.ButtonStyleHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private lateinit var lstTheme: MutableList<ButtonStyleModel>
    private var w = 0f

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback

        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstTheme: MutableList<ButtonStyleModel>) {
        this.lstTheme = lstTheme

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonStyleHolder {
        return ButtonStyleHolder(
            ItemButtonStyleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (lstTheme.isNotEmpty()) lstTheme.size else 0
    }

    override fun onBindViewHolder(holder: ButtonStyleHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ButtonStyleHolder(private val binding: ItemButtonStyleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivBg.createBackground(
                intArrayOf(ContextCompat.getColor(context, R.color.black_text)), (2.5f * w).toInt(), -1, -1
            )
        }

        fun onBind(position: Int) {
            val theme = lstTheme[position]

            when (position) {
                0 -> binding.root.layoutParams =
                    RelativeLayout.LayoutParams((25f * w).toInt(), (25f * w).toInt()).apply {
                        leftMargin = (4.44f * w).toInt()
                        rightMargin = (2.22f * w).toInt()
                        topMargin = (6.67f * w).toInt()
                        addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                    }

                lstTheme.size - 1 -> binding.root.layoutParams =
                    RelativeLayout.LayoutParams((25f * w).toInt(), (25f * w).toInt()).apply {
                        rightMargin = (4.44f * w).toInt()
                        leftMargin = (2.22f * w).toInt()
                        topMargin = (6.67f * w).toInt()
                        addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                    }

                else -> binding.root.layoutParams =
                    RelativeLayout.LayoutParams((25f * w).toInt(), (25f * w).toInt()).apply {
                        leftMargin = (2.22f * w).toInt()
                        rightMargin = (2.22f * w).toInt()
                        topMargin = (6.67f * w).toInt()
                        addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                    }
            }

            if (theme.isCheck) binding.ivChoose.visibility = View.VISIBLE
            else binding.ivChoose.visibility = View.GONE
            Glide.with(context)
                .asBitmap()
                .load("file:///android_asset/theme/${theme.name}/answer.png")
                .into(binding.ivAnswer)
            Glide.with(context)
                .asBitmap()
                .load("file:///android_asset/theme/${theme.name}/deny.png")
                .into(binding.ivDeny)

            binding.root.setOnClickListener {
                setCurrent(position)
                callback.callBack(theme, position)
            }
        }
    }

    fun setCurrent(position: Int) {
        for (pos in lstTheme.indices) lstTheme[pos].isCheck = pos == position

        notifyChange()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}