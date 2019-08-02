package com.zhao.customview.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.zhao.customview.R


class CustomView2 :View{
    var paint = Paint()
    var textPaint = Paint()
    var progress = 0f
    var setpro = 0f

    private val valueAnimator by lazy { createAnimator() }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    init {
        paint.color = resources.getColor(R.color.colorPrimary)
        paint.isAntiAlias = true
        textPaint.color = Color.BLUE
        textPaint.textSize = 20f
        textPaint.style = Paint.Style.FILL
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawColor(Color.WHITE)
        paint.color = resources.getColor(R.color.colorPrimary)
        val rectf = RectF(0f,0f,200f,200f)
        paint.style = Paint.Style.FILL
        canvas.drawArc(rectf,0f,360f,true,paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 15f
        val newRectf = RectF(250f,50f,450f,250f)
        canvas.drawArc(newRectf,0f,360f,true,paint)
        canvas.drawLine(500f,150f,600f,150f,paint)

        //*加上蒙版*
        paint.color = resources.getColor(R.color.translucent0)
        paint.style = Paint.Style.FILL
        canvas.drawArc(rectf,0f,progress,true,paint)

        //画圆弧  类似进度
        paint.style = Paint.Style.STROKE
        paint.color = resources.getColor(R.color.colorAccent)
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(newRectf,0f,progress,false,paint)

        canvas.save()
        //显示进度
        val fontMetrics = textPaint.fontMetrics
        val top = fontMetrics.top
        val bottom = fontMetrics.bottom
        val baseLineY =  (newRectf.centerY() - top/2 - bottom/2)
        val per = (progress/360*100).toInt()
        canvas.drawText("加载了$per%",newRectf.centerX(),baseLineY,textPaint)
    }

    fun setPro(pro:Float){
        this.setpro = pro
        valueAnimator.start()
    }
    private fun createAnimator():ValueAnimator{
        val valueAnimator = ValueAnimator.ofFloat(0f,setpro)
        valueAnimator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.duration = 5000
        valueAnimator.interpolator = AccelerateInterpolator()
        return valueAnimator
    }


}