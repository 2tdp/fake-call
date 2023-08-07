package com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.contact.ContactModel
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemContactBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.ItemContactTitleBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.PopUpHistoryBinding
import javax.inject.Inject

class ContactAdapter @Inject constructor() :RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private var lstContact = mutableListOf<ContactModel>()
    private var lstContactOld = mutableListOf<ContactModel>()
    private var w = 0f

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback
        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstContact: MutableList<ContactModel>) {
        this.lstContact = lstContact
        this.lstContactOld = ArrayList(lstContact)

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        return ContactHolder(ItemContactTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstContact.isNotEmpty()) lstContact.size else 0
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ContactHolder(private val binding: ItemContactTitleBinding): ViewHolder(binding.root) {

        fun onBind(position: Int) {
            val contact = lstContact[position]

            binding.ivMore.visibility = if (contact.isFake)  View.VISIBLE else View.GONE
            if (!contact.isTitle) binding.tvTitle.visibility = View.GONE
            if (contact.isTitle && contact.isFake)
                binding.tvTitle.apply {
                    visibility = View.VISIBLE
                    text = context.getString(R.string.fake_contact)
                }

            if (contact.isTitle && !contact.isFake)
                binding.tvTitle.apply {
                    visibility = View.VISIBLE
                    text = context.getString(R.string.contacts)
                }

            binding.tvName.text = contact.name
            binding.tvContact.text = contact.contact.replace("Mobile ", "")
            if (contact.thumb == "") binding.ivThumb.setImageResource(R.drawable.ic_default_avartar)
            else
                Glide.with(context)
                    .asBitmap()
                    .load(contact.thumb)
                    .into(binding.ivThumb)

            binding.root.setOnClickListener { callback.callBack(contact, position) }

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
                    callback.callBack(contact, position)
                    popUp.dismiss()
                }
                bindingMore.rlDel.setOnClickListener {
                    callback.callBack(contact, -1)
                    popUp.dismiss()
                }
            }
        }
    }

    fun filter(text: String) {
        Thread {
            lstContact.clear()
            if (text.isEmpty()) lstContact.addAll(lstContactOld)
            else
                for (contact in lstContactOld)
                    if (contact.name.lowercase().contains(text.lowercase())) lstContact.add(contact)

            Handler(Looper.getMainLooper()).post { notifyChange() }
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}