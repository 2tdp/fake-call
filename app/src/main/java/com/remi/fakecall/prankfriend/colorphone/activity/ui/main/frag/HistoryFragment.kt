package com.remi.fakecall.prankfriend.colorphone.activity.ui.main.frag

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.CallModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.HistoryAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.activity.ui.main.MainActivityViewModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.schedule.ScheduleActivity
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackCheck
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.DialogDelAllHistoryBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.FragCallBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.FragHistoryBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_SCHEDULE_HISTORY
import com.remi.fakecall.prankfriend.colorphone.helpers.SCHEDULE_CALL
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment: BaseFragment<FragHistoryBinding>(FragHistoryBinding::inflate) {

    companion object {
        fun newInstance(): HistoryFragment {
            val args = Bundle()

            val fragment = HistoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var historyAdapter: HistoryAdapter
    private val viewModel: MainActivityViewModel by activityViewModels()

    var isVisibleDel: ICallBackCheck? = null

    override fun setUp() {
        setUpView()


        lifecycleScope.launch {
            viewModel.getAllHistory()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateHistory.collect {
                    when(it) {
                        is UiState.Success -> {
                            if (it.data.isNotEmpty()) {
                                historyAdapter.setData(it.data)
                                binding.rcvHistory.visibility = View.VISIBLE
                                binding.tvNoData.visibility = View.GONE
                                isVisibleDel?.check(true)
                            } else {
                                binding.rcvHistory.visibility = View.GONE
                                binding.tvNoData.visibility = View.VISIBLE
                                isVisibleDel?.check(false)
                            }
                        }
                        is UiState.Error -> {}
                        is UiState.Loading -> {}
                    }
                }
            }
        }
    }

    private fun setUpView() {
        historyAdapter.newInstance(requireContext(), object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                val schedule = ob as CallModel
                if (position == -1) delItemHistory(schedule)
                else {
                    DataLocalManager.setScheduleCall(SCHEDULE_CALL, schedule)
                    startIntent(ScheduleActivity::class.java.name, false)
                }
            }
        })

        binding.rcvHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvHistory.adapter = historyAdapter
    }

    private fun delItemHistory(schedule: CallModel) {
        val bindingDel = DialogDelAllHistoryBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        bindingDel.tvDes.text = getString(R.string.des_del_2)

        val dialog = AlertDialog.Builder(requireContext(), R.style.SheetDialog).create()
        dialog.apply {
            setCancelable(true)
            setView(bindingDel.root)
            show()
        }

        bindingDel.root.layoutParams.width = (83.33f * w).toInt()
        bindingDel.root.layoutParams.height = (36.556f * w).toInt()

        bindingDel.ivDel.setOnClickListener {
            dialog.cancel()
            val lstHis = DataLocalManager.getListSchedule(LIST_SCHEDULE_HISTORY)
            val lstTmp = lstHis.filter { it.idCall == schedule.idCall }
            if (lstHis.isNotEmpty()) lstHis.remove(lstTmp[0])

            DataLocalManager.setListSchedule(lstHis, LIST_SCHEDULE_HISTORY)
            viewModel.getAllHistory()
        }
        bindingDel.tvCancel.setOnClickListener { dialog.cancel() }
    }
}