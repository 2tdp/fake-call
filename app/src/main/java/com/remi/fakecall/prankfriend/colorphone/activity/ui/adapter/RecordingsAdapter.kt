package com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.MusicModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemAudioBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.ALL_DEL
import com.remi.fakecall.prankfriend.colorphone.helpers.COUNT_DEL
import com.remi.fakecall.prankfriend.colorphone.helpers.START_DEL
import javax.inject.Inject

class RecordingsAdapter @Inject constructor() : RecyclerView.Adapter<RecordingsAdapter.AudioHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private lateinit var callbackLong: ICallBackItem
    private var lstAudio = mutableListOf<MusicModel>()
    private var w = 0f
    private var countDel = 0
    var isDel = false

    fun newInstance(context: Context, callback: ICallBackItem, callbackLong: ICallBackItem) {
        this.context = context
        this.callback = callback
        this.callbackLong = callbackLong

        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstAudio: MutableList<MusicModel>) {
        this.lstAudio = lstAudio

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioHolder {
        return AudioHolder(ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstAudio.isNotEmpty()) lstAudio.size else 0
    }

    override fun onBindViewHolder(holder: AudioHolder, position: Int) {
        holder.onBind(position)
    }

    inner class AudioHolder(private val binding: ItemAudioBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tvName.apply {
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.MIDDLE
            }
        }

        fun onBind(position: Int) {
            val audio = lstAudio[position]

            binding.tvName.text = audio.songName
            if (audio.isChoose) {
                if (!isDel) binding.root.setBackgroundColor(Color.parseColor("#CBEFFF"))
                else binding.root.setBackgroundColor(Color.WHITE)
                binding.ivChoose.setImageResource(if (!isDel) R.drawable.ic_choose else R.drawable.ic_del_tick)
                if (!isDel) {
                    if (audio.isPlay) binding.iv.setImageResource(R.drawable.ic_pause_audio)
                    else binding.iv.setImageResource(R.drawable.ic_play_audio)
                }
            } else {
                if (!isDel) binding.root.setBackgroundColor(Color.WHITE)
                binding.ivChoose.setImageResource(if (!isDel) R.drawable.ic_un_choose else R.drawable.ic_del_un_tick)
            }

            binding.root.setOnClickListener {
                if (!isDel) callback.callBack(audio, position)
                else {
                    setItemDel(position)
                    if (lstAudio[position].isChoose) countDel++ else countDel--

                    if (countDel == lstAudio.size) callbackLong.callBack("", ALL_DEL)
                    else callbackLong.callBack("", COUNT_DEL)

                }
            }

            binding.root.setOnLongClickListener {
                isDel = true
                audio.isChoose = true
                countDel++
                if (countDel == lstAudio.size) callbackLong.callBack("", ALL_DEL)
                else callbackLong.callBack("", COUNT_DEL)

                notifyChange()
                callbackLong.callBack(audio, START_DEL)
                true
            }
        }
    }

    fun setCurrent(position: Int) {
        for (pos in lstAudio.indices) {
            if (pos == position) lstAudio[pos].isPlay = !lstAudio[pos].isPlay
            else lstAudio[pos].isPlay = false

            lstAudio[pos].isChoose = pos == position
        }

        notifyChange()
    }

    fun setItemDel(position: Int) {
        lstAudio[position].isChoose = !lstAudio[position].isChoose
        notifyItemRangeChanged(position, 1,false)
    }

    fun setAllDel() {
        if (countDel == lstAudio.size) {
            for (music in lstAudio) music.isChoose = false
            countDel = 0
            callbackLong.callBack("", COUNT_DEL)
        } else {
            for (music in lstAudio) music.isChoose = true
            countDel = lstAudio.size
            callbackLong.callBack("", ALL_DEL)
        }
        notifyChange()
    }

    fun getListDel(): MutableList<MusicModel> {
        if (!isDel) return mutableListOf()
        return lstAudio.filter { it.isChoose } as MutableList<MusicModel>
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}