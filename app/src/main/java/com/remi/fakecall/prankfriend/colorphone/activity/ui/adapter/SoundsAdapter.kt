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
import javax.inject.Inject

class SoundsAdapter @Inject constructor() : RecyclerView.Adapter<SoundsAdapter.AudioHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private var lstAudio = mutableListOf<MusicModel>()
    private var w = 0f

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback

        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstAudio: MutableList<MusicModel>) {
        this.lstAudio = lstAudio

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioHolder {
        return AudioHolder(
            ItemAudioBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (lstAudio.isNotEmpty()) lstAudio.size else 0
    }

    override fun onBindViewHolder(holder: AudioHolder, position: Int) {
        holder.onBind(position)
    }

    inner class AudioHolder(private val binding: ItemAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
                binding.root.setBackgroundColor(Color.parseColor("#CBEFFF"))
                binding.ivChoose.setImageResource(R.drawable.ic_choose)
                if (audio.isPlay) binding.iv.setImageResource(R.drawable.ic_pause_audio)
                else binding.iv.setImageResource(R.drawable.ic_play_audio)
            } else binding.ivChoose.setImageResource(R.drawable.ic_un_choose)

            binding.root.setOnClickListener {
                callback.callBack(audio, position)
                setCurrent(position)
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

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}