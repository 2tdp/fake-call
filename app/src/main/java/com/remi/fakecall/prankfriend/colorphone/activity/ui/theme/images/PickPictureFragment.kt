package com.remi.fakecall.prankfriend.colorphone.activity.ui.theme.images

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseFragment
import com.remi.fakecall.prankfriend.colorphone.adapter.ViewPagerAddFragmentsAdapter
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.FragPickPictureBinding
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_GIF
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_PHOTO
import com.yalantis.ucrop.UCrop
import java.io.File

class PickPictureFragment : BaseFragment<FragPickPictureBinding>(FragPickPictureBinding::inflate) {

    companion object {
        fun newInstance(): PickPictureFragment {
            val args = Bundle()

            val fragment = PickPictureFragment()
            fragment.arguments = args
            return fragment
        }
    }

    val fragPhoto: ImageAppFragment by lazy { ImageAppFragment.newInstance() }
    val fragGif: GifOnlineFragment by lazy { GifOnlineFragment.newInstance() }

    lateinit var callbackPic: ICallBackItem
    lateinit var callbackGif: ICallBackItem

    var intent: Intent? = null

    override fun setUp() {
        setUpView()
        evenClick()
    }

    private fun evenClick() {
        binding.ctlPhoto.setOnClickListener {
            swipePagerAudio(TYPE_PHOTO)
            binding.viewPager.setCurrentItem(0, true)
        }
        binding.ctlGif.setOnClickListener {
            swipePagerAudio(TYPE_GIF)
            binding.viewPager.setCurrentItem(1, true)
        }
        binding.ivPickImage.setOnClickListener { picImage.launch("image/*") }
    }

    private fun setUpView() {
        val adapterViewPager = ViewPagerAddFragmentsAdapter(parentFragmentManager, lifecycle)

        adapterViewPager.addFrag(fragPhoto)
        fragPhoto.callback = callbackPic
        adapterViewPager.addFrag(fragGif)
        fragGif.callback = callbackGif

        binding.viewPager.apply {
            offscreenPageLimit = 2
            this.adapter = adapterViewPager
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    swipePagerAudio(if (position == 0) TYPE_PHOTO else TYPE_GIF)
                }
            })
        }
    }

    private val picImage = registerForActivityResult(ActivityResultContracts.GetContent()) {uri ->
        uri?.let {
            val uCrop =
                UCrop.of(it, Uri.fromFile(File(requireContext().cacheDir, "remi-fake-call-bg-${System.currentTimeMillis()}.png")))
            uCrop.apply {
                withAspectRatio(9f, 16f)
                withOptions(UCrop.Options().apply {
                    setCompressionFormat(Bitmap.CompressFormat.PNG)
                    setCompressionQuality(100)
                })
            }
            uCrop.start(requireActivity())
            intent = uCrop.getIntent(requireContext())
        }
    }

    private fun swipePagerAudio(option: String) {
        when (option) {
            TYPE_PHOTO -> {
                binding.tvPhoto.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
                binding.vLine.visibility = View.VISIBLE

                binding.tvGif.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_text))
                binding.vLine1.visibility = View.GONE
            }

            TYPE_GIF -> {
                binding.tvGif.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
                binding.vLine1.visibility = View.VISIBLE

                binding.tvPhoto.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_text))
                binding.vLine.visibility = View.GONE
            }
        }
    }

}