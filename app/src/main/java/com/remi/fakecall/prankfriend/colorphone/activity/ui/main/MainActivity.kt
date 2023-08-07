package com.remi.fakecall.prankfriend.colorphone.activity.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.NumberPicker
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseActivity
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.frag.CallFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.frag.ContactsFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.frag.HistoryFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.frag.SettingsFragment
import com.remi.fakecall.prankfriend.colorphone.adapter.ViewPagerAddFragmentsAdapter
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackCheck
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ActivityMainBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.DialogDelAllHistoryBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.setGradient
import com.remi.fakecall.prankfriend.colorphone.helpers.CALL
import com.remi.fakecall.prankfriend.colorphone.helpers.CONTACTS
import com.remi.fakecall.prankfriend.colorphone.helpers.HISTORY
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_SCHEDULE_HISTORY
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_RING_TIME
import com.remi.fakecall.prankfriend.colorphone.helpers.SETTINGS_TALK_TIME
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun getDispatchers(): CoroutineContext {
        return Dispatchers.IO + job
    }

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, ContextCompat.getColor(this, R.color.black_background))
    }

    private val fragCall: CallFragment by lazy { CallFragment.newInstance() }
    private val fragContacts: ContactsFragment by lazy { ContactsFragment.newInstance() }
    private val fragHistory: HistoryFragment by lazy { HistoryFragment.newInstance() }
    private val fragSettings: SettingsFragment by lazy { SettingsFragment.newInstance() }

    private val viewModel: MainActivityViewModel by viewModels()

    private var typeTime = ""
    private var isCheckPerContact = false
    private var isCheckPerAudio = false

    override fun setUp() {
        setUpView()
        evenClick()
        actionBottom(CALL)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.ctlBottomSheet.isVisible) swipeBottomSheet("hide")
                else if (fragCall.fragPickContact.isVisible)
                    popBackStack(supportFragmentManager, "PickContactFragment", 1)
                else if (fragCall.fragPickAudio.isVisible) {
                    if (fragCall.fragPickAudio.fragRecordings.recordingsAdapter.isDel)
                        fragCall.fragPickAudio.actionDel(false)
                    else if (fragCall.fragPickAudio.fragRecord.isVisible) {
                        if (fragCall.fragPickAudio.fragRecord.isRecording)
                            fragCall.fragPickAudio.fragRecord.stopRecorder()
                        else popBackStack(supportFragmentManager, "RecordFragment", 1)
                    } else fragCall.fragPickAudio.popBackStackAll(-1)
                } else if (fragCall.fragPickTheme.isVisible) fragCall.fragPickTheme.backStackAll(-1)
                else onBackPressed(true)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllHistory()
        if (fragCall.fragPickTheme.isVisible) viewModel.getAllMyTheme()
        fragCall.setImageAvatar()

        if (isCheckPerContact) {
            viewModel.getAllContact()
            isCheckPerContact = false
        }
        if (isCheckPerAudio) {
            viewModel.getAllMusic()
            isCheckPerAudio = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun evenClick() {
        binding.ctlCall.setOnClickListener { actionBottom(CALL) }
        binding.ctlHistory.setOnClickListener { actionBottom(HISTORY) }
        binding.ctlContacts.setOnClickListener { actionBottom(CONTACTS) }
        binding.ctlSettings.setOnClickListener { actionBottom(SETTINGS) }

        binding.ivRight.setOnClickListener { showDialogDelAllHistory() }
        binding.vBtmSheet.setOnClickListener {
            swipeBottomSheet("hide")
            when(typeTime) {
                "ring_time" -> {
                    DataLocalManager.setLong(binding.pickNumber.value * 1000L, SETTINGS_RING_TIME)
                    fragSettings.binding.tvDesRingTimer.text = "End call after ${binding.pickNumber.value}s"
                }
                "talk_time" -> {
                    DataLocalManager.setLong(binding.pickNumber.value * 1000L, SETTINGS_TALK_TIME)
                    fragSettings.binding.tvDesTalkTime.text = "${binding.pickNumber.value}s"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpView() {
        val adapterPager = ViewPagerAddFragmentsAdapter(supportFragmentManager, lifecycle)
        adapterPager.addFrag(fragCall)
        adapterPager.addFrag(fragHistory)
        adapterPager.addFrag(fragContacts)
        adapterPager.addFrag(fragSettings)
        fragCall.isCheckPerContact = object : ICallBackCheck {
            override fun check(isCheck: Boolean) {
                isCheckPerContact = isCheck
            }
        }
        fragCall.isCheckPerAudio = object : ICallBackCheck {
            override fun check(isCheck: Boolean) {
                isCheckPerAudio = isCheck
            }
        }
        fragContacts.isCheckPer = object : ICallBackCheck {
            override fun check(isCheck: Boolean) {
                isCheckPerContact = isCheck
            }
        }
        fragSettings.isSwipePager = object : ICallBackCheck {
            override fun check(isCheck: Boolean) {
                binding.viewPager.isUserInputEnabled = !isCheck
            }
        }
        fragSettings.isPickTime = object : ICallBackItem{
            override fun callBack(ob: Any, position: Int) {
                typeTime = ob as String
                when(typeTime) {
                    "ring_time" -> {
                        binding.pickNumber.apply {
                            maxValue = 50
                            minValue = 0
                            value = (DataLocalManager.getLong(SETTINGS_RING_TIME) / 1000).toInt()
                        }
                        binding.tvTitleSheet.text = getString(R.string.phone_call_ring_time)
                        binding.tvDesSheet.text = "Call will be off after ringing ${binding.pickNumber.value}s"
                        swipeBottomSheet("show")
                    }
                    "talk_time" -> {
                        binding.pickNumber.apply {
                            maxValue = 180
                            minValue = 0
                            value = (DataLocalManager.getLong(SETTINGS_TALK_TIME) / 1000).toInt()
                        }
                        binding.tvTitleSheet.text = getString(R.string.talk_time)
                        binding.tvDesSheet.text =  "End call after ${binding.pickNumber.value}s"
                        swipeBottomSheet("show")
                    }
                }
            }
        }
        fragHistory.isVisibleDel = object : ICallBackCheck {
            override fun check(isCheck: Boolean) {
                if (binding.viewPager.currentItem == 1)
                    binding.ivRight.visibility = if (isCheck) View.VISIBLE else View.GONE
                else binding.ivRight.visibility = View.GONE
            }
        }

        binding.viewPager.adapter = adapterPager

        binding.viewPager.offscreenPageLimit = 4

        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> actionBottom(CALL)
                    1 -> actionBottom(HISTORY)
                    2 -> actionBottom(CONTACTS)
                    3 -> actionBottom(SETTINGS)
                }
            }
        })

        binding.pickNumber.apply {
            selectedTextSize = 3.889f * w
            textSize = 3.889f * w
            typeface = Utils.getTypeFace("roboto", "roboto_medium.ttf", context)
            dividerColor = Color.TRANSPARENT
            orientation = NumberPicker.VERTICAL
            textColor = Color.parseColor("#BCBCBC")
            selectedTextColor = ContextCompat.getColor(this@MainActivity, R.color.black_text)
            wheelItemCount = 5
        }
        binding.pickNumber.setOnValueChangedListener { picker, _, _ ->
            when(typeTime) {
                "ring_time" -> binding.tvDesSheet.text = "Call will be off after ringing ${picker.value}s"
                "talk_time" -> binding.tvDesSheet.text = "End call after ${picker.value}s"
            }
        }
    }

    private fun showDialogDelAllHistory() {
        val bindingDelAllHis = DialogDelAllHistoryBinding.inflate(LayoutInflater.from(this), null, false)
        bindingDelAllHis.tvCancel.text = getString(R.string.cancel).uppercase()
        val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
        dialog.apply {
            setCancelable(true)
            setView(bindingDelAllHis.root)
            show()
        }

        bindingDelAllHis.root.layoutParams.width = (83.33f * w).toInt()
        bindingDelAllHis.root.layoutParams.height = (41.11f * w).toInt()

        bindingDelAllHis.ivDel.setOnClickListener {
            DataLocalManager.setListSchedule(mutableListOf(), LIST_SCHEDULE_HISTORY)
            viewModel.getAllHistory()
            dialog.cancel()
        }
        bindingDelAllHis.tvCancel.setOnClickListener { dialog.cancel() }
    }

    private fun actionBottom(option: String) {
        when (option) {
            CALL -> {
                binding.tvTitle.text = getString(R.string.call_assistant)
                if (binding.ivRight.isVisible) binding.ivRight.visibility = View.GONE
                binding.viewPager.setCurrentItem(0, true)
                binding.tvCall.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.main_color),
                        ContextCompat.getColor(this, R.color.main_color2)
                    )
                )
                binding.ivCall.setImageResource(R.drawable.ic_btm_call_pick)

                binding.tvHistory.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivHistory.setImageResource(R.drawable.ic_btm_history_un_pick)
                binding.tvContacts.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivContacts.setImageResource(R.drawable.ic_btm_contact_un_pick)
                binding.tvSettings.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivSettings.setImageResource(R.drawable.ic_btm_settings_un_pick)
            }

            HISTORY -> {
                viewModel.getAllHistory()
                binding.tvTitle.text = getString(R.string.history)
                if (!binding.ivRight.isVisible) binding.ivRight.visibility = View.VISIBLE
                binding.viewPager.setCurrentItem(1, true)
                binding.tvHistory.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.main_color),
                        ContextCompat.getColor(this, R.color.main_color2)
                    )
                )
                binding.ivHistory.setImageResource(R.drawable.ic_btm_history_pick)

                binding.tvCall.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivCall.setImageResource(R.drawable.ic_btm_call_un_pick)
                binding.tvContacts.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivContacts.setImageResource(R.drawable.ic_btm_contact_un_pick)
                binding.tvSettings.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivSettings.setImageResource(R.drawable.ic_btm_settings_un_pick)
            }

            CONTACTS -> {
                viewModel.getAllContact()
                binding.tvTitle.text = getString(R.string.contacts)
                if (binding.ivRight.isVisible) binding.ivRight.visibility = View.GONE
                binding.viewPager.setCurrentItem(2, true)
                binding.tvContacts.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.main_color),
                        ContextCompat.getColor(this, R.color.main_color2)
                    )
                )
                binding.ivContacts.setImageResource(R.drawable.ic_btm_contact_pick)

                binding.tvCall.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivCall.setImageResource(R.drawable.ic_btm_call_un_pick)
                binding.tvHistory.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivHistory.setImageResource(R.drawable.ic_btm_history_un_pick)
                binding.tvSettings.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivSettings.setImageResource(R.drawable.ic_btm_settings_un_pick)
            }

            SETTINGS -> {
                binding.tvTitle.text = getString(R.string.settings)
                if (binding.ivRight.isVisible) binding.ivRight.visibility = View.GONE
                binding.viewPager.setCurrentItem(3, true)
                binding.tvSettings.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.main_color),
                        ContextCompat.getColor(this, R.color.main_color2)
                    )
                )
                binding.ivSettings.setImageResource(R.drawable.ic_btm_settings_pick)

                binding.tvCall.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivCall.setImageResource(R.drawable.ic_btm_call_un_pick)
                binding.tvHistory.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivHistory.setImageResource(R.drawable.ic_btm_history_un_pick)
                binding.tvContacts.setGradient(
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.gray_text),
                        ContextCompat.getColor(this, R.color.gray_text)
                    )
                )
                binding.ivContacts.setImageResource(R.drawable.ic_btm_contact_un_pick)
            }
        }
    }

    private fun swipeBottomSheet(option: String) {
        when(option) {
            "show" -> {
                binding.ctlBottomSheet.apply {
                    animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.slide_up_in)
                    visibility = View.VISIBLE
                    animation.setAnimationListener(object : AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            binding.vBtmSheet.visibility = View.VISIBLE
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                    })
                }
            }
            "hide" -> {
                binding.ctlBottomSheet.apply {
                    animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.slide_down_out)
                    visibility = View.GONE
                }
                binding.vBtmSheet.visibility = View.GONE
            }
        }
    }
}