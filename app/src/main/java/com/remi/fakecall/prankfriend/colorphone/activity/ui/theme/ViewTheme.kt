package com.remi.fakecall.prankfriend.colorphone.activity.ui.theme

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.graphics.PathParser
import androidx.core.view.isVisible
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ThemeModel
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_BR_TL
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_B_T
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_GIF_ONLINE
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_R_L
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_TL_BR
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_TR_BL
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_T_B
import com.remi.fakecall.prankfriend.colorphone.utils.UtilsBitmap
import com.remi.fakecall.prankfriend.colorphone.viewcustom.OnCalledResult

@SuppressLint("AppCompatCustomView")
class ViewTheme : ImageView {

    companion object {
        var w = 0f
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint: Paint
    private val paintAlpha: Paint
    private val paintControl: Paint
    private val paintText: Paint
    private var rectBg: RectF
    private var rectBtn: RectF
    private var rectText: Rect
    private var pathClip: Path
    private var pathControl: Path

    private var animatorBg: ValueAnimator
    private var animatorControl: ValueAnimator
    private var isCreate = true
    private var isUp = true
    private var startX = 0f
    private var startY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var size = 0f
    private var sizeBm = 0f
    private var alphaControl = 0
    private var step = 0
    private var tranX = 0f
    private var or1 = 1
    private var topBtn = 0f
    private var distanceLeft = 0f
    private var distanceRight = 0f
    private var isLeft = false
    private var isRight = false

    var isImage = false
    var themeModel: ThemeModel
    var bmBg: Bitmap? = null
    var bmButton: Bitmap? = null

    var onCalledResult: OnCalledResult? = null

    init {
        size = dpToPx(13)
        themeModel = ThemeModel()

        paint = Paint(ANTI_ALIAS_FLAG)
        paintAlpha = Paint(ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        paintControl = Paint(ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        paintText = Paint(ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        rectBg = RectF()
        rectBtn = RectF()
        rectText = Rect()
        pathClip = Path()
        pathControl = Path()
        animatorBg = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                createShader()
                invalidate()
            }
            duration = 500
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
        animatorControl = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val p = it.animatedValue as Float
                tranX = size * p
                step++
                if (step % 3 == 0) {
                    alphaControl += or1 * 10
                    if (alphaControl > 255) {
                        alphaControl = 255
                        or1 = -1
                    } else if (alphaControl < 20) {
                        alphaControl = 20
                        or1 = 1
                    }
                }
                invalidate()
            }
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        w = width / 100f
        sizeBm = 22.14f * w
        distanceLeft = 9.225f * w
        distanceRight = 9.225f * w
        topBtn = 36.9f * w
        if (isCreate) {
            rectBg.set(0f, 0f, width.toFloat(), height.toFloat())
            pathClip.addRoundRect(rectBg, 4.44f * w, 4.44f * w, Path.Direction.CW)
            startX = 0f
            currentX = width / 2f
            startY = 0f
            currentY = height / 2f
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            if (!animatorBg.isRunning) animatorBg.start()
            if (!animatorControl.isRunning) animatorControl.start()
        } else {
            if (animatorBg.isRunning) animatorBg.cancel()
            if (animatorControl.isRunning) animatorControl.cancel()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (isVisible) {
            if (themeModel.typeImages == TYPE_GIF_ONLINE) setBackgroundColor(Color.TRANSPARENT)
            else drawBackground(canvas)

            drawButton(canvas)

            drawText(canvas)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        if (!isImage) {
            paint.apply {
                isAntiAlias = true
                shader = LinearGradient(
                    startX, startY, currentX, currentY,
                    intArrayOf(themeModel.color.colorStart, themeModel.color.colorEnd),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRoundRect(rectBg, 4.44f * w, 4.44f * w, paint)

            when (themeModel.color.alpha) {
                100 -> paintAlpha.color = Color.parseColor("#00FFFFFF")
                80 -> paintAlpha.color = Color.parseColor("#CCFFFFFF")
                60 -> paintAlpha.color = Color.parseColor("#99FFFFFF")
                40 -> paintAlpha.color = Color.parseColor("#66FFFFFF")
                20 -> paintAlpha.color = Color.parseColor("#33FFFFFF")
            }
            canvas.drawRoundRect(rectBg, 4.44f * w, 4.44f * w, paintAlpha)
        } else {
            canvas.clipPath(pathClip)
            paint.isFilterBitmap = true

            bmBg?.let { canvas.drawBitmap(it, null, rectBg, paint) }
        }
    }

    private fun drawButton(canvas: Canvas) {
        paint.isFilterBitmap = true

        bmButton =
            UtilsBitmap.getBitmapFromAsset(context, "theme/${themeModel.buttonStyle.name}", "answer.png")
        bmButton?.let {
            rectBtn.set(
                distanceLeft,
                height - topBtn,
                distanceLeft + sizeBm,
                height - topBtn + sizeBm
            )
            canvas.drawBitmap(it, null, rectBtn, paint)
        }

        bmButton = UtilsBitmap.getBitmapFromAsset(context, "theme/${themeModel.buttonStyle.name}", "deny.png")
        bmButton?.let {
            rectBtn.set(
                width - distanceRight - sizeBm,
                height - topBtn,
                width - distanceRight,
                height - topBtn + sizeBm
            )
            canvas.drawBitmap(it, null, rectBtn, paint)
        }

        paintControl.apply {
            color = Color.WHITE
            alpha = alphaControl
        }
        if (!isLeft) {
            canvas.save()
            pathControl.apply {
                reset()
                addPath(PathParser.createPathFromPathData("M0.22 0.22c0.3-0.3 0.77-0.3 1.06 0l4.95 4.95c0.3 0.3 0.3 0.77 0 1.06l-4.95 4.95c-0.3 0.3-0.77 0.3-1.06 0-0.3-0.29-0.3-0.76 0-1.06L4.64 5.7 0.22 1.28c-0.3-0.3-0.3-0.77 0-1.06Z"))
                fillType = Path.FillType.EVEN_ODD
            }
            canvas.translate(33.21f * w + tranX, height - 26.56f * w)
            canvas.scale(dpToPx(1), dpToPx(1))
            canvas.drawPath(pathControl, paintControl)
            canvas.restore()
        }

        if (!isRight) {
            canvas.save()
            pathControl.apply {
                reset()
                addPath(PathParser.createPathFromPathData("M6.23 11.18c-0.29 0.3-0.77 0.3-1.06 0L0.22 6.23c-0.3-0.29-0.3-0.77 0-1.06l4.95-4.95c0.3-0.3 0.77-0.3 1.06 0 0.3 0.3 0.3 0.77 0 1.06L1.81 5.7l4.42 4.42c0.3 0.3 0.3 0.77 0 1.06Z"))
                fillType = Path.FillType.EVEN_ODD
            }
            canvas.translate(width - 34f * w - tranX, height - 26.56f * w)
            canvas.scale(dpToPx(1), dpToPx(1))
            canvas.drawPath(pathControl, paintControl)
            canvas.restore()
        }
    }

    private fun drawText(canvas: Canvas) {
        val txt = "Incoming call"
        paintText.apply {
            textSize = 3.69f * w
            getTextBounds(txt, 0, txt.length, rectText)
        }
        canvas.drawText(txt, width / 2f - rectText.width() / 2f, 17.22f * w, paintText)

        paintText.apply {
            textSize = 6.64f * w
            getTextBounds(themeModel.nameNumb, 0, themeModel.nameNumb.length, rectText)
        }
        canvas.drawText(themeModel.nameNumb, width / 2f - rectText.width() / 2f, 25.556f * w, paintText)

        paintText.apply {
            textSize = 3.69f * w
            getTextBounds(themeModel.numb, 0, themeModel.numb.length, rectText)
        }
        canvas.drawText(themeModel.numb, width / 2f - rectText.width() / 2f, 32.223f * w, paintText)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onCalledResult?.onDown(this)
                if (x in distanceLeft..distanceLeft + sizeBm && y > height - topBtn) isLeft = true
                if (x in width - distanceRight - sizeBm..width - distanceRight && y > height - topBtn)
                    isRight = true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isLeft) {
                    if (x < 9.225f * w) distanceLeft = 9.225f * w
                    else if (x >= width / 2f - sizeBm / 2) {
                        distanceLeft = width / 2f - sizeBm / 2
                        onCalledResult?.onMove(true, false)
                        isLeft = false
                    } else distanceLeft = x
                }
                if (isRight) {
                    if (x > width - 9.225f * w) distanceRight = 9.225f * w
                    else if (x <= width / 2f + sizeBm / 2) {
                        distanceRight = width / 2f - sizeBm / 2
                        onCalledResult?.onMove(false, true)
                        isRight = false
                    } else distanceRight = width - x
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                isLeft = false
                isRight = false
                distanceLeft = 9.225f * w
                distanceRight = 9.225f * w
            }
        }

        return true
    }

    private fun createShader() {
        when (themeModel.color.direc) {
            TYPE_T_B -> {
                startX = width / 2f
                currentX = width / 2f
                startY = 0f
                if (isUp) currentY += 0.5f * w else currentY -= 0.5f * w

                if (currentY > height * 6 / 7f) isUp = false
                else if (currentY < height / 4f) isUp = true
            }

            TYPE_R_L -> {
                startY = height / 2f
                currentY = height / 2f
                startX = width * 5 / 6f
                if (isUp) currentX += 0.25f * w else currentX -= 0.25f * w

                if (currentX < width / 6f) isUp = true
                else if (currentX > width * 3 / 4f) isUp = false
            }

            TYPE_B_T -> {
                startX = width / 2f
                currentX = width / 2f
                startY = height * 6 / 7f
                if (isUp) currentY += 0.5f * w else currentY -= 0.5f * w

                if (currentY > height * 3 / 4f) isUp = false
                else if (currentY < height / 4f) isUp = true
            }

            TYPE_TR_BL -> {
                startX = 3 * width / 4f
                startY = height / 4f
                if (isUp) {
                    currentX += 0.2f * width / 100
                    currentY -= 0.3f * width / 100
                } else {
                    currentX -= 0.2f * width / 100
                    currentY += 0.3f * width / 100
                }
                if (currentX > startX / 2f) isUp = false
                else if (currentX < 0) isUp = true
            }

            TYPE_TL_BR -> {
                startX = width / 6f
                startY = height / 7f
                if (isUp) {
                    currentY += (0.3f * height / width) * w
                    currentX += (0.3f * width / height) * w
                } else {
                    currentY -= (0.3f * height / width) * w
                    currentX -= (0.3f * width / height) * w
                }

                if (currentX > width * 5 / 6f) isUp = false
                else if (currentX < width / 4f) isUp = true
            }

            TYPE_BR_TL -> {
                startX = width * 5 / 6f
                startY = height * 5 / 6f

                if (isUp) {
                    currentY += 0.3f * w
                    currentX += 0.3f * w
                } else {
                    currentY -= 0.3f * w
                    currentX -= 0.3f * w
                }

                if (currentX > width * 3 / 4f) isUp = false
                else if (currentX < width / 5f) isUp = true
            }
        }
    }

    private fun dpToPx(dp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
    }
}