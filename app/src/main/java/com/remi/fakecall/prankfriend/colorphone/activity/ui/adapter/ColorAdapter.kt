package com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.remi.fakecall.prankfriend.colorphone.activity.data.color.ColorModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemColorBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.extensions.createColor
import javax.inject.Inject

class ColorAdapter @Inject constructor() : RecyclerView.Adapter<ColorAdapter.ColorHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private lateinit var lstColor: MutableList<ColorModel>
    private var w = 0f

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback

        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstColor: MutableList<ColorModel>) {
        this.lstColor = lstColor

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
        return ColorHolder(ItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstColor.isNotEmpty()) lstColor.size else 0
    }

    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ColorHolder(private val binding: ItemColorBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.layoutParams =
                RelativeLayout.LayoutParams((13.889f * w).toInt(), (13.889f * w).toInt()).apply {
                    leftMargin = (1.667f * w).toInt()
                    rightMargin = (1.667f * w).toInt()
                    topMargin = (1.667f * w).toInt()
                    bottomMargin = (1.667f * w).toInt()
                }
            binding.ivColor.layoutParams =
                RelativeLayout.LayoutParams((11.667f * w).toInt(), (11.667f * w).toInt()).apply {
                    addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                }
        }

        fun onBind(position: Int) {
            val color = lstColor[position]

            binding.ivChoose.visibility = if (color.isCheck) View.VISIBLE else View.GONE
            binding.ivColor.createColor(color, (55f * w).toInt())

            binding.root.setOnClickListener {
                setCurrent(position)
                callback.callBack(color, position)
            }
        }
    }

    fun setCurrent(position: Int) {
        for (pos in lstColor.indices) lstColor[pos].isCheck = pos == position

        notifyChange()
    }

    fun setDirect(position: Int, type: Int) {
        lstColor[position].direc = type

        notifyItemRangeChanged(position, 1, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}