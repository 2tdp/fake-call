package com.remi.fakecall.prankfriend.colorphone.viewcustom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

import com.remi.fakecall.prankfriend.colorphone.R
import kotlinx.coroutines.NonCancellable.start

class CustomBeatSplash : View {

    var valueAnimator: ValueAnimator? = null
    private var paint: Paint
    private var w = 0f
    private var isCreate = true
    private var count = 0f
    private var plus = 0f
    private var radius = 0f
    private var radiuss = 0f
    private var radiusss = 0f
    private var radiussss = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        w = resources.displayMetrics.widthPixels / 100F
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, width / 2f, width.toFloat(), height / 2f,
                intArrayOf(
                    ContextCompat.getColor(context, R.color.main_color),
                    ContextCompat.getColor(context, R.color.main_color2)
                ), null, Shader.TileMode.CLAMP)
            alpha = (10 * 255 / 100f).toInt()
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (isCreate) {
            isCreate = false
            count = width / 6f
            plus = 2f
            val f = width / 16f
            radius = count
            radiuss = count + f
            radiusss = count + 2 * f
            radiussss = count + 3 * f
            runBeat1()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            drawCircle(width / 2f, height / 2f, radius, paint)
            drawCircle(width / 2f, height / 2f, radiuss, paint)
//            drawCircle(width / 2f, height / 2f, radiusss, paint)
//            drawCircle(width / 2f, height / 2f, radiussss, paint)
        }
    }

    private fun runBeat1() {
        valueAnimator = ValueAnimator.ofFloat(0f, 2f)
        valueAnimator?.let {
            it.repeatCount = ValueAnimator.INFINITE
            it.addUpdateListener {
                if (radius > width * 5 / 12f) radius = count
                radius += plus
                if (radiuss > width * 5 / 12f) radiuss = count
                radiuss += plus
                if (radiusss > width * 5 / 12f) radiusss = count
                radiusss += plus
                if (radiussss > width * 5 / 12f) radiussss = count
                radiussss += plus
                invalidate()
            }
        }
    }
}