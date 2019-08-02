package com.zhao.customview.release.FadeView

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.zhao.customview.R
import kotlin.math.sqrt


class FadeView : View {
    var mPaint = Paint()
    private val centerPoint by lazy {
        FloatArray(2).also {
            it[0] = width.toFloat() / 2
            it[1] = height.toFloat() / 2
        }
    }
    private val xfermode by lazy{
        PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }
    var proRadius = 0f
    private val radius by lazy {
        sqrt(width * width.toDouble() + height * height)/2
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onDraw(canvas: Canvas) {
        val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        mPaint.color = resources.getColor(R.color.translucent1)
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rectF, 10f, 10f, mPaint) //dst:目标图像

        mPaint.color = resources.getColor(R.color.translucent2)
        mPaint.xfermode = xfermode
        canvas.drawCircle(centerPoint[0], centerPoint[1], proRadius, mPaint) //src:源图像
        mPaint.xfermode = null
        canvas.restoreToCount(layerId)
    }

    fun startAnimation() {
       val animator =  ObjectAnimator.ofFloat(0f, radius.toFloat())
        animator.duration = 3000
        animator.addUpdateListener {
                proRadius = it.animatedValue as Float
                 Log.i("FadeView","proRadius=$proRadius")
                invalidate()
            }
        animator.start()
    }
}