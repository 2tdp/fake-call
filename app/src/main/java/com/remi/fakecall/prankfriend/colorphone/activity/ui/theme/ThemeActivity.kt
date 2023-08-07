package com.remi.fakecall.prankfriend.colorphone.activity.ui.theme

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.flask.colorpicker.renderer.FlowerColorWheelRenderer
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.activity.data.color.ColorModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.gif.GifModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.picture.PicModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ButtonStyleModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ThemeModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.ButtonStyleAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.adapter.ColorAdapter
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.BaseActivity
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.activity.ui.theme.images.GifOnlineFragment
import com.remi.fakecall.prankfriend.colorphone.activity.ui.theme.images.PickPictureFragment
import com.remi.fakecall.prankfriend.colorphone.callback.ICallBackItem
import com.remi.fakecall.prankfriend.colorphone.databinding.ActivityThemeBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.DialogPickColorBinding
import com.remi.fakecall.prankfriend.colorphone.databinding.DialogSaveThemeBinding
import com.remi.fakecall.prankfriend.colorphone.extensions.createBackground
import com.remi.fakecall.prankfriend.colorphone.extensions.createColor
import com.remi.fakecall.prankfriend.colorphone.extensions.setIntent
import com.remi.fakecall.prankfriend.colorphone.helpers.FOLDER_PREVIEW_THEME
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_THEME
import com.remi.fakecall.prankfriend.colorphone.helpers.PREVIEW_THEME
import com.remi.fakecall.prankfriend.colorphone.helpers.TAG
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_BR_TL
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_B_T
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_IMAGE_APP
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_GIF_ONLINE
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_IMAGE_USER
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_R_L
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_TL_BR
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_TR_BL
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_T_B
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import com.remi.fakecall.prankfriend.colorphone.utils.Utils
import com.remi.fakecall.prankfriend.colorphone.utils.UtilsBitmap
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
class ThemeActivity : BaseActivity<ActivityThemeBinding>(ActivityThemeBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(ContextCompat.getColor(this, R.color.white_background), Color.parseColor("#CBEFFF"))
    }

    override fun isVisible(): Boolean {
        return true
    }

    @Inject
    lateinit var colorAdapter: ColorAdapter
    @Inject
    lateinit var gradientAdapter: ColorAdapter
    @Inject
    lateinit var btnStyleAdapter: ButtonStyleAdapter
    private var bindingColor: DialogPickColorBinding? = null
    private val viewModels: ThemeActivityViewModels by viewModels()

    var fragPickImage: PickPictureFragment? = null

    private lateinit var theme: ThemeModel
    private lateinit var colorDefault: ColorModel
    private lateinit var gradientDefault: ColorModel

    private var typeImage = false

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.vPreview.isVisible) binding.vPreview.visibility = View.GONE
                else if (fragPickImage != null)
                    fragPickImage?.let {
                        popBackStack(supportFragmentManager, "PickPictureFragment", 1)
                        fragPickImage = null
                    }
                else if (binding.cvButton.isVisible) swipeChooseButtonStyle(false)
                else onBackPressed(false)
            }
        })

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            Dexter.withContext(this@ThemeActivity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        Utils.makeFolder(this@ThemeActivity, FOLDER_PREVIEW_THEME)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {

                    }

                }).check()
        else Utils.makeFolder(this@ThemeActivity, FOLDER_PREVIEW_THEME)

        setUpView()
        evenClick()
    }

    override fun onResume() {
        super.onResume()
        if (fragPickImage != null)
            fragPickImage?.let {
                popBackStack(supportFragmentManager, "PickPictureFragment", 1)
                fragPickImage = null
            }
    }

    private fun setUpView() {
        theme = ThemeModel()

        colorAdapter.newInstance(this, object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                colorDefault = ob as ColorModel

                colorDefault.alpha = 100
                actionAlpha("")
                setColorAlpha(colorDefault)
            }
        })
        gradientAdapter.newInstance(this, object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                gradientDefault = ob as ColorModel
                bindingColor?.vPreview?.createColor(gradientDefault, (55f * w).toInt())
            }
        })
        btnStyleAdapter.newInstance(this, object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                binding.ivBgPreview.apply {
                    this.themeModel.buttonStyle = ob as ButtonStyleModel
                    invalidate()
                }
            }
        })
        binding.rcvButton.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcvButton.adapter = btnStyleAdapter

        lifecycleScope.launch {
            viewModels.getAllColor()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModels.uiStateAllColor.collect {
                    when (it) {
                        is UiState.Success -> {
                            colorDefault = it.data[0]
                            colorAdapter.setData(it.data)
                        }

                        is UiState.Error -> {}
                        is UiState.Loading -> {}
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModels.getAllGradient()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModels.uiStateAllGradient.collect {
                    when (it) {
                        is UiState.Success -> {
                            gradientDefault = it.data[0]
                            gradientAdapter.setData(it.data)
                        }

                        is UiState.Error -> {}
                        is UiState.Loading -> {}
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModels.getAllTheme()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModels.uiStateAllButton.collect {
                    when(it) {
                        is UiState.Success -> {
                            it.data[0].isCheck = true
                            btnStyleAdapter.setData(it.data)
                        }
                        is UiState.Error -> {}
                        is UiState.Loading -> {}
                    }
                }
            }
        }
    }

    private fun evenClick() {
        binding.ivBack.setOnClickListener { finish() }
        binding.ctlColor.setOnClickListener { showDialogPickColor() }
        binding.ctlBackground.setOnClickListener {
            fragPickImage = PickPictureFragment()
            fragPickImage?.callbackPic = object : ICallBackItem {
                override fun callBack(ob: Any, position: Int) {
                    val pic = ob as PicModel
                    theme.apply {
                        typeImages = TYPE_IMAGE_APP
                        uriImages = pic.uri
                    }
                    typeImage = true
                    binding.ivBgPreview.apply {
                        isImage = true
                        bmBg = Bitmap.createScaledBitmap(
                            UtilsBitmap.getBitmapFromAsset(this@ThemeActivity, "background", theme.uriImages)!!,
                            binding.ivBgPreview.width, binding.ivBgPreview.height, false
                        )
                        themeModel = theme
                        invalidate()
                    }
                    popBackStack(supportFragmentManager, "PickPictureFragment", 1)
                    fragPickImage = null
                }
            }
            fragPickImage?.callbackGif = object : ICallBackItem {
                override fun callBack(ob: Any, position: Int) {
                    val gif = ob as GifModel
                    theme.gif = gif
                    setBackgroundTheme("gif")

                    binding.ivBgPreview.apply {
                        themeModel = theme
                        invalidate()
                    }

                    Glide.with(this@ThemeActivity)
                        .asGif()
                        .load(gif.large)
                        .placeholder(R.drawable.ic_placeholder_rec)
                        .into(binding.ivGif)
                    popBackStack(supportFragmentManager, "PickPictureFragment", 1)
                    fragPickImage = null
                }
            }
            replaceFragment(supportFragmentManager, fragPickImage!!, true, true)
        }
        binding.ctlBtnStyle.setOnClickListener { swipeChooseButtonStyle(true) }
        binding.vButton.setOnClickListener { swipeChooseButtonStyle(false) }
        binding.ivExpand.setOnClickListener {
            DataLocalManager.setThemePreview(PREVIEW_THEME, theme)
            setIntent(PreviewActivity::class.java.name, false)
        }

        binding.ivCreate.setOnClickListener { showDialogSave() }
    }

    private fun setBackgroundTheme(option: String) {
        when (option) {
            "color" -> {
                typeImage = false
                binding.ivBgPreview.apply {
                    isImage = false
                    theme.typeImages = ""
                    this.themeModel = theme
                    invalidate()
                }
            }

            "image" -> {
                typeImage = true
                binding.ivBgPreview.apply {
                    isImage = true
                    UtilsBitmap.getBitmapFromUri(this@ThemeActivity, Uri.parse(theme.uriImages))?.let {bm ->
                        bmBg =
                            Bitmap.createScaledBitmap(bm, binding.ivBgPreview.width, binding.ivBgPreview.height, false)
                    }
                    themeModel = theme
                    invalidate()
                }
            }

            "gif" -> {
                typeImage = false
                binding.ivBgPreview.apply {
                    isImage = false
                    theme.typeImages = TYPE_GIF_ONLINE
                    this.themeModel = theme
                    invalidate()
                }
            }
        }
    }

    private fun showDialogPickColor() {
        bindingColor = DialogPickColorBinding.inflate(LayoutInflater.from(this))
        bindingColor?.let {
            it.rcvColor.layoutManager = GridLayoutManager(this@ThemeActivity, 5)
            it.rcvColor.adapter = colorAdapter

            it.rcvGradient.layoutManager =
                GridLayoutManager(this@ThemeActivity, 2, GridLayoutManager.HORIZONTAL, false)
            it.rcvGradient.adapter = gradientAdapter

            setColorAlpha(colorDefault)
            it.vPreview.createColor(gradientDefault, (55f * w).toInt())
            it.colorPicker.apply {
                setAlphaSlider(it.alphaSlider)
                setLightnessSlider(it.lightnessSlider)
                setDensity(15)
                setRenderer(FlowerColorWheelRenderer())


            }
        }

        val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
        dialog.apply {
            setView(bindingColor?.root)
            setCancelable(false)
            show()
        }

        bindingColor?.root?.layoutParams?.width = (83.33f * w).toInt()
        bindingColor?.root?.layoutParams?.height = (130.556f * w).toInt()

        bindingColor?.let {
            it.ivExitDialog.setOnClickListener { dialog.cancel() }
            it.tvCustomize.setOnClickListener { _ ->
                if (it.tvCustomize.text == getString(R.string.customize)) {
                    it.ctlColor.visibility = View.GONE
                    it.ctlCustomize.visibility = View.VISIBLE
                    it.tvCustomize.text = getString(R.string.back)
                } else if (it.tvCustomize.text == getString(R.string.back)) {
                    it.ctlColor.visibility = View.VISIBLE
                    it.ctlCustomize.visibility = View.GONE
                    it.tvCustomize.text = getString(R.string.customize)
                }
            }
            it.tvColor.setOnClickListener { swipePagerDialogColor("color") }
            it.tvGradient.setOnClickListener { swipePagerDialogColor("gradient") }

            it.vAlpha00.setOnClickListener {
                colorDefault.alpha = 0
                colorAdapter.setCurrent(-1)
                actionAlpha("00")
            }
            it.vAlpha20.setOnClickListener {
                colorDefault.alpha = 20
                colorAdapter.setCurrent(-1)
                actionAlpha("20")
            }
            it.vAlpha40.setOnClickListener {
                colorDefault.alpha = 40
                colorAdapter.setCurrent(-1)
                actionAlpha("40")
            }
            it.vAlpha60.setOnClickListener {
                colorDefault.alpha = 60
                colorAdapter.setCurrent(-1)
                actionAlpha("60")
            }
            it.vAlpha80.setOnClickListener {
                colorDefault.alpha = 80
                colorAdapter.setCurrent(-1)
                actionAlpha("80")
            }

            it.ivBRTL.setOnClickListener { _ ->
                actionDirect(TYPE_BR_TL)
                it.vPreview.createColor(gradientDefault, (55f * w).toInt())
            }
            it.ivBT.setOnClickListener { _ ->
                actionDirect(TYPE_B_T)
                it.vPreview.createColor(gradientDefault, (55f * w).toInt())
            }
            it.ivTLBR.setOnClickListener { _ ->
                actionDirect(TYPE_TL_BR)
                it.vPreview.createColor(gradientDefault, (55f * w).toInt())
            }
            it.ivRL.setOnClickListener { _ ->
                actionDirect(TYPE_R_L)
                it.vPreview.createColor(gradientDefault, (55f * w).toInt())
            }
            it.ivTRBL.setOnClickListener { _ ->
                actionDirect(TYPE_TR_BL)
                it.vPreview.createColor(gradientDefault, (55f * w).toInt())
            }
            it.ivTB.setOnClickListener { _ ->
                actionDirect(TYPE_T_B)
                it.vPreview.createColor(gradientDefault, (55f * w).toInt())
            }

            it.tvSelect.setOnClickListener { _ ->
                if (it.ctlColor.isVisible) theme.color = colorDefault
                else if (it.ctlCustomize.isVisible) {
                    colorDefault.apply {
                        colorStart = it.colorPicker.selectedColor
                        colorEnd = it.colorPicker.selectedColor
                        direc = TYPE_T_B
                        isCheck = false
                    }
                } else if (it.ctlGradient.isVisible) theme.color = gradientDefault
                setBackgroundTheme("color")
                dialog.cancel()
            }
        }
    }

    private fun setColorAlpha(color: ColorModel) {
        bindingColor?.let {
            it.v00.createBackground(
                intArrayOf(color.colorStart, color.colorEnd), (55f * w).toInt(), -1, -1
            )
            it.vAlpha00.createBackground(
                intArrayOf(Color.parseColor("#00FFFFFF")), (55f * w).toInt(), -1, -1
            )

            it.v20.createBackground(
                intArrayOf(color.colorStart, color.colorEnd), (55f * w).toInt(), -1, -1
            )
            it.vAlpha20.createBackground(
                intArrayOf(Color.parseColor("#33FFFFFF")), (55f * w).toInt(), -1, -1
            )

            it.v40.createBackground(
                intArrayOf(color.colorStart, color.colorEnd), (55f * w).toInt(), -1, -1
            )
            it.vAlpha40.createBackground(
                intArrayOf(Color.parseColor("#66FFFFFF")), (55f * w).toInt(), -1, -1
            )

            it.v60.createBackground(
                intArrayOf(color.colorStart, color.colorEnd), (55f * w).toInt(), -1, -1
            )
            it.vAlpha60.createBackground(
                intArrayOf(Color.parseColor("#99FFFFFF")), (55f * w).toInt(), -1, -1
            )

            it.v80.createBackground(
                intArrayOf(color.colorStart, color.colorEnd), (55f * w).toInt(), -1, -1
            )
            it.vAlpha80.createBackground(
                intArrayOf(Color.parseColor("#CCFFFFFF")), (55f * w).toInt(), -1, -1
            )
        }
    }

    private fun swipePagerDialogColor(option: String) {
        when (option) {
            "color" -> {
                bindingColor?.let {
                    it.root.layoutParams?.height = (130.556f * w).toInt()
                    it.tvCustomize.visibility = View.VISIBLE
                    it.ctlCustomize.visibility = View.GONE

                    it.tvGradient.setBackgroundResource(R.drawable.boder_tab_top_right_un_choose)
                    it.ctlGradient.apply {
                        animation = AnimationUtils.loadAnimation(this@ThemeActivity, R.anim.slide_out_right)
                        visibility = View.GONE
                    }
                    it.tvColor.setBackgroundResource(R.drawable.boder_tab_top_left_choose)
                    it.ctlColor.apply {
                        animation =
                            AnimationUtils.loadAnimation(this@ThemeActivity, R.anim.slide_in_left_small)
                        visibility = View.VISIBLE
                    }
                }
            }

            "gradient" -> {
                bindingColor?.let {
                    it.root.layoutParams?.height = (144.44f * w).toInt()
                    it.tvCustomize.visibility = View.GONE
                    it.ctlCustomize.visibility = View.GONE

                    it.tvColor.setBackgroundResource(R.drawable.boder_tab_top_left_un_choose)
                    it.ctlColor.apply {
                        animation = AnimationUtils.loadAnimation(this@ThemeActivity, R.anim.slide_out_left)
                        visibility = View.GONE
                    }
                    it.tvGradient.setBackgroundResource(R.drawable.boder_tab_top_right_choose)
                    it.ctlGradient.apply {
                        animation = AnimationUtils.loadAnimation(this@ThemeActivity, R.anim.slide_in_right)
                        visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun actionDirect(type: Int) {
        when (type) {
            TYPE_T_B -> {
                gradientDefault.direc = TYPE_T_B
                bindingColor?.let {
                    it.ivTB.setImageResource(R.drawable.ic_direc_top_down_choose)

                    it.ivBT.setImageResource(R.drawable.ic_direc_bot_top_un_choose)
                    it.ivBRTL.setImageResource(R.drawable.ic_direc_br_tl_un_choose)
                    it.ivRL.setImageResource(R.drawable.ic_direc_right_left_un_choose)
                    it.ivTLBR.setImageResource(R.drawable.ic_direc_tl_br_un_choose)
                    it.ivTRBL.setImageResource(R.drawable.ic_direc_tr_bl_un_choose)
                }
            }

            TYPE_BR_TL -> {
                gradientDefault.direc = TYPE_BR_TL
                bindingColor?.let {
                    it.ivBRTL.setImageResource(R.drawable.ic_direc_br_tl_choose)

                    it.ivTB.setImageResource(R.drawable.ic_direc_top_down_un_choose)
                    it.ivBT.setImageResource(R.drawable.ic_direc_bot_top_un_choose)
                    it.ivRL.setImageResource(R.drawable.ic_direc_right_left_un_choose)
                    it.ivTLBR.setImageResource(R.drawable.ic_direc_tl_br_un_choose)
                    it.ivTRBL.setImageResource(R.drawable.ic_direc_tr_bl_un_choose)
                }
            }

            TYPE_B_T -> {
                gradientDefault.direc = TYPE_B_T
                bindingColor?.let {
                    it.ivBT.setImageResource(R.drawable.ic_direc_bot_top_choose)

                    it.ivBRTL.setImageResource(R.drawable.ic_direc_br_tl_un_choose)
                    it.ivTB.setImageResource(R.drawable.ic_direc_top_down_un_choose)
                    it.ivRL.setImageResource(R.drawable.ic_direc_right_left_un_choose)
                    it.ivTLBR.setImageResource(R.drawable.ic_direc_tl_br_un_choose)
                    it.ivTRBL.setImageResource(R.drawable.ic_direc_tr_bl_un_choose)
                }
            }

            TYPE_R_L -> {
                gradientDefault.direc = TYPE_R_L
                bindingColor?.let {
                    it.ivRL.setImageResource(R.drawable.ic_direc_right_left_choose)

                    it.ivBT.setImageResource(R.drawable.ic_direc_bot_top_un_choose)
                    it.ivBRTL.setImageResource(R.drawable.ic_direc_br_tl_un_choose)
                    it.ivTB.setImageResource(R.drawable.ic_direc_top_down_un_choose)
                    it.ivTLBR.setImageResource(R.drawable.ic_direc_tl_br_un_choose)
                    it.ivTRBL.setImageResource(R.drawable.ic_direc_tr_bl_un_choose)
                }
            }

            TYPE_TL_BR -> {
                gradientDefault.direc = TYPE_TL_BR
                bindingColor?.let {
                    it.ivTLBR.setImageResource(R.drawable.ic_direc_tl_br_choose)

                    it.ivRL.setImageResource(R.drawable.ic_direc_right_left_un_choose)
                    it.ivBT.setImageResource(R.drawable.ic_direc_bot_top_un_choose)
                    it.ivBRTL.setImageResource(R.drawable.ic_direc_br_tl_un_choose)
                    it.ivTB.setImageResource(R.drawable.ic_direc_top_down_un_choose)
                    it.ivTRBL.setImageResource(R.drawable.ic_direc_tr_bl_un_choose)
                }
            }

            TYPE_TR_BL -> {
                gradientDefault.direc = TYPE_TR_BL
                bindingColor?.let {
                    it.ivTRBL.setImageResource(R.drawable.ic_direc_tr_bl_choose)

                    it.ivTLBR.setImageResource(R.drawable.ic_direc_tl_br_un_choose)
                    it.ivRL.setImageResource(R.drawable.ic_direc_right_left_un_choose)
                    it.ivBT.setImageResource(R.drawable.ic_direc_bot_top_un_choose)
                    it.ivBRTL.setImageResource(R.drawable.ic_direc_br_tl_un_choose)
                    it.ivTB.setImageResource(R.drawable.ic_direc_top_down_un_choose)
                }
            }
        }
    }

    private fun actionAlpha(type: String) {
        when (type) {
            "00" -> {
                bindingColor?.let {
                    it.vChoose00.visibility = View.VISIBLE

                    it.vChoose20.visibility = View.GONE
                    it.vChoose40.visibility = View.GONE
                    it.vChoose60.visibility = View.GONE
                    it.vChoose80.visibility = View.GONE
                }
            }

            "20" -> {
                bindingColor?.let {
                    it.vChoose20.visibility = View.VISIBLE

                    it.vChoose00.visibility = View.GONE
                    it.vChoose40.visibility = View.GONE
                    it.vChoose60.visibility = View.GONE
                    it.vChoose80.visibility = View.GONE
                }
            }

            "40" -> {
                bindingColor?.let {
                    it.vChoose40.visibility = View.VISIBLE

                    it.vChoose20.visibility = View.GONE
                    it.vChoose00.visibility = View.GONE
                    it.vChoose60.visibility = View.GONE
                    it.vChoose80.visibility = View.GONE
                }
            }

            "60" -> {
                bindingColor?.let {
                    it.vChoose60.visibility = View.VISIBLE

                    it.vChoose20.visibility = View.GONE
                    it.vChoose00.visibility = View.GONE
                    it.vChoose40.visibility = View.GONE
                    it.vChoose80.visibility = View.GONE
                }
            }

            "80" -> {
                bindingColor?.let {
                    it.vChoose80.visibility = View.VISIBLE

                    it.vChoose20.visibility = View.GONE
                    it.vChoose00.visibility = View.GONE
                    it.vChoose40.visibility = View.GONE
                    it.vChoose60.visibility = View.GONE
                }
            }

            else -> {
                bindingColor?.let {
                    it.vChoose80.visibility = View.GONE
                    it.vChoose20.visibility = View.GONE
                    it.vChoose00.visibility = View.GONE
                    it.vChoose40.visibility = View.GONE
                    it.vChoose60.visibility = View.GONE
                }
            }
        }
    }

    private fun swipeChooseButtonStyle(isShow: Boolean) {
        if (isShow) {
            binding.cvButton.animation = AnimationUtils.loadAnimation(this, R.anim.slide_up_in)
            binding.cvButton.visibility = View.VISIBLE

            binding.cvButton.animation.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding.vButton.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }

            })
        } else {
            binding.cvButton.animation = AnimationUtils.loadAnimation(this, R.anim.slide_down_out)
            binding.cvButton.visibility = View.GONE

            binding.vButton.visibility = View.GONE
        }
    }

    private fun showDialogSave() {
        val bindingSave =
            DialogSaveThemeBinding.inflate(LayoutInflater.from(this@ThemeActivity), null, false)

        val dialog = AlertDialog.Builder(this@ThemeActivity, R.style.SheetDialog).create()
        dialog.apply {
            setCancelable(true)
            setView(bindingSave.root)
            show()
        }

        bindingSave.root.layoutParams.width = (83.33f * w).toInt()
        bindingSave.root.layoutParams.height = (38.889f * w).toInt()

        bindingSave.tvCancel.setOnClickListener { dialog.cancel() }
        bindingSave.ivOceSave.setOnClickListener {
            val uriPreview = UtilsBitmap.saveBitmapToApp(
                this, UtilsBitmap.loadBitmapFromView(binding.ctlTheme)!!,
                FOLDER_PREVIEW_THEME, "remi-preview-${System.currentTimeMillis()}"
            )
            theme.apply {
                name = bindingSave.etdSave.text.toString()
                this.uriPreview = uriPreview
                isChoose = true
            }
            saveTheme()
            dialog.cancel()
            finish()
        }
    }

    private fun saveTheme() {
        lifecycleScope.launch {
            viewModels.getAllMyTheme()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModels.uiStateMyTheme.collect {
                    when(it) {
                        is UiState.Success -> {
                            for (t in it.data) t.isChoose = false
                            it.data.add(0, theme)
                            DataLocalManager.setListMyTheme(it.data, LIST_THEME)
                        }
                        is UiState.Error -> {}
                        is UiState.Loading -> {}
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP)
            data?.let {
                theme.apply {
                    typeImages = TYPE_IMAGE_USER
                    uriImages = UCrop.getOutput(it).toString()
                }
                setBackgroundTheme("image")
            }
    }
}