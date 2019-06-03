package com.zhao.customview.view

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.graphics.PaintFlagsDrawFilter
import com.zhao.customview.R


class CustomView1 :View{
    var paint = Paint()
    var progress = 0f
    var setpro = 0f

    private val valueAnimator by lazy { createAnimator() }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    init {
        paint.color = resources.getColor(R.color.colorPrimary)
        paint.isAntiAlias = true
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
//        canvas.drawCircle(350f,150f,100f,paint)
        val newRectf = RectF(250f,50f,450f,250f)
        canvas.drawArc(newRectf,0f,360f,true,paint)
        canvas.drawLine(500f,150f,600f,150f,paint)
        paint.color = resources.getColor(R.color.colorAccent)
        paint.style = Paint.Style.FILL
        canvas.drawArc(rectf,0f,progress,true,paint)

        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(newRectf,0f,progress,false,paint)
    }

    fun setPro(pro:Float){
        this.setpro = pro
        valueAnimator.start()
    }
    private fun createAnimator():ValueAnimator{
        Log.i("test","createAnimator=$progress")
        val valueAnimator = ValueAnimator.ofObject(TypeEvaluator<Float> { fraction, startValue, endValue ->
            var dis =  endValue - startValue
            progress =  startValue+dis*fraction
            Log.i("test","progress=$progress")
            postInvalidate()
            progress
        },0f,setpro)
        valueAnimator.duration = 5000
        return valueAnimator
    }


}