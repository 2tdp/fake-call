package com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.CallModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.schedule.ScheduleActivity
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemHistoryBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.PopUpHistoryBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.SCHEDULE_CALL
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import java.text.ParseException
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class HistoryAdapter @Inject constructor() : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private lateinit var lstCall: MutableList<CallModel>
    private var w = 0F

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback

        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstCall: MutableList<CallModel>) {
        this.lstCall = lstCall
        this.lstCall.reverse()

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        return HistoryHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstCall.isNotEmpty()) lstCall.size else 0
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.onBind(position)
    }

    inner class HistoryHolder(private val binding: ItemHistoryBinding) : ViewHolder(binding.root) {


        fun onBind(position: Int) {
            val call = lstCall[position]

            if (call.uriAvatar == "") binding.ivAvatar.setImageResource(R.drawable.ic_default_avartar)
            else
                Glide.with(context)
                    .asBitmap()
                    .load(call.uriAvatar)
                    .into(binding.ivAvatar)
            binding.ivState.setImageResource(if (!call.isAnswer) R.drawable.ic_history_deny else R.drawable.ic_history_answer)
            binding.tvName.apply {
                text = call.contact.name
                setTextColor(
                    if (call.isAnswer) ContextCompat.getColor(context, R.color.black_text)
                    else ContextCompat.getColor(context, R.color.red)
                )
            }
            binding.tvTime.text = covertTimeToText(call.timeSchedule)

            binding.ivMore.setOnClickListener {
                val bindingMore = PopUpHistoryBinding.inflate(LayoutInflater.from(context), null, false)
                bindingMore.tvDel.text = "Delete"
                val popUp =
                    PopupWindow(
                        bindingMore.root, (41.667f * w).toInt(),
                        RelativeLayout.LayoutParams.WRAP_CONTENT, true
                    )
                popUp.showAsDropDown(it, (-34f * w).toInt(), 0)

                bindingMore.rlSchedule.setOnClickListener {
                    callback.callBack(call, position)
                    popUp.dismiss()
                }
                bindingMore.rlDel.setOnClickListener {
                    callback.callBack(call, -1)
                    popUp.dismiss()
                }
            }
        }
    }

    fun covertTimeToText(pasTime: Long): String {
        var convTime = ""
        val prefix = ""
        val suffix = "ago"
        try {
            val dateDiff = Date().time - pasTime
            val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day = TimeUnit.MILLISECONDS.toDays(dateDiff)
            convTime = if (second < 60) "$second seconds $suffix"
            else if (minute < 60) "$minute minutes $suffix"
            else if (hour < 24) "$hour hours $suffix"
            else if (day >= 7) {
                if (day > 360) (day / 360).toString() + " years " + suffix
                else if (day > 30) (day / 30).toString() + " months " + suffix
                else (day / 7).toString() + " week " + suffix
            } else "$day days $suffix"
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convTime
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}