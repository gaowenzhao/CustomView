package com.zhao.customview.view

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator


class CreditView : View {
    private var distance = 15f//外环于内环的间距
    private var radus = 0f//外环半径
    private var defalutSize = 250f//默认长宽
    private var proPaint = Paint() //外环进度画笔
    private var wPaint = Paint() //外环画笔
    private var nPaint = Paint() //内环画笔
    private var skPaint = Paint() //小刻度
    private var lkPaint = Paint() //大刻度
    private var mBitmapPaint = Paint()//小圆点画笔
    private var sTextPaint = Paint() //刻度上的字体
    private val wStrokeWidth = 10f
    private val nStrokeWidth = 30f
    private val cirLocation by lazy {  FloatArray(2).apply { set(0,paddingLeft.toFloat());set(1,radus) } }
    private val levelStrs  = arrayOf("较差","中等","良好","优秀","极好","完美")
    private val levels  = arrayOf("350","550","600","650","700","950","1150")
//    private
    private var progress = 0f
    private var setpro = 0f
    private val valueAnimator by lazy { createAnimator() }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    init{
        defalutSize = dp2px(defalutSize)
        distance = dp2px(distance)

        proPaint.isAntiAlias = true
        proPaint.style = Paint.Style.STROKE
        proPaint.strokeWidth = wStrokeWidth
        proPaint.strokeCap = Paint.Cap.ROUND
        proPaint.color = Color.WHITE

        wPaint.isAntiAlias = true
        wPaint.color = Color.WHITE
        wPaint.alpha = 50//透明度，取值范围为0~255，数值越小越透明
        wPaint.style = Paint.Style.STROKE
        wPaint.strokeWidth = wStrokeWidth

        nPaint.isAntiAlias = true
        nPaint.color = Color.WHITE
        nPaint.alpha = 50//透明度，取值范围为0~255，数值越小越透明
        nPaint.style = Paint.Style.STROKE
        nPaint.strokeWidth = nStrokeWidth

        skPaint = Paint()
        skPaint.isAntiAlias = true
        skPaint.color = Color.WHITE
        skPaint.alpha = 80

        lkPaint = Paint()
        lkPaint.isAntiAlias = true
        lkPaint.color = Color.WHITE
        lkPaint.strokeWidth = 2f

        sTextPaint = Paint()
        sTextPaint.isAntiAlias = true
        sTextPaint.color = Color.WHITE
        sTextPaint.textSize = 20f
        sTextPaint.alpha = 100
        sTextPaint.textAlign = Paint.Align.CENTER

        mBitmapPaint = Paint()
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        //画外环
        val wRectf = RectF(paddingLeft.toFloat(),paddingTop.toFloat(),(width-paddingRight).toFloat(),(height-paddingBottom).toFloat())
        canvas.drawArc(wRectf,180f,180f,false,wPaint)
        //画内环
        val left = paddingLeft+distance
        val top = paddingTop+distance
        val right = width-paddingRight - distance
        val bottom = height-paddingBottom - distance
        val nRectf = RectF(left,top,right,bottom)
        canvas.drawArc(nRectf,180f,180f,false,nPaint)
        //画小刻度
        val startX = radus
        val startY = paddingTop+distance-nStrokeWidth/2
        val stopX = radus
        val stopY = startY+nStrokeWidth
        val stopYs = stopY+dp2px(3f)
        val baseLineY = stopYs+ dp2px(10f)
        canvas.save()
        canvas.rotate(-90f,radus,radus)
        for(i in 1..36){
            canvas.drawLine(startX,startY,stopX,stopY,skPaint)
            canvas.rotate(5f,radus,radus)
        }
        canvas.restore() //回滚save之前得状态
        //画大刻度
        canvas.save()
        canvas.rotate(-90f,radus,radus)
        for(i in 0 until levels.size){
            canvas.drawLine(startX,startY,stopX,stopYs,lkPaint)
            canvas.drawText(levels[i],stopX,baseLineY,sTextPaint)
            canvas.rotate(180f/(levels.size-1),radus,radus)
        }
        canvas.restore() //回滚save之前得状态
        //画信用等级
        canvas.save() //相当于保存之前画布的界面，然后在新的画布操作
        canvas.rotate(-75f,radus,radus)
        for(i in 0 until levelStrs.size){
            canvas.drawText(levelStrs[i],stopX,baseLineY,sTextPaint)
            canvas.rotate(180f/levelStrs.size,radus,radus)
        }
        canvas.restore() //回滚save之前得状态
        //把渐变设置到笔刷
        proPaint.shader = LinearGradient(paddingLeft.toFloat(),radus,cirLocation[0],cirLocation[1],Color.RED,Color.BLUE,Shader.TileMode.CLAMP)
        //画外环进度
        canvas.drawArc(wRectf,180f,progress,false,proPaint)
       //画外圆环进度的小圆点
        val path = Path()
        path.addArc(wRectf,180f,progress)
        val pathMeasure = PathMeasure(path,false)
        pathMeasure.getPosTan(pathMeasure.length,cirLocation,null)
        canvas.drawCircle(cirLocation[0],cirLocation[1],8f,mBitmapPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width: Int
        var height: Int
        val widthMode=MeasureSpec.getMode(widthMeasureSpec)
        val widthSize=MeasureSpec.getSize(widthMeasureSpec)
        val heightMode=MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        width = if(widthMode==MeasureSpec.EXACTLY) widthSize else Math.min(widthSize,defalutSize.toInt())
        height = if(heightMode==MeasureSpec.EXACTLY) heightSize else Math.min(heightSize,defalutSize.toInt())
        radus = (width/2).toFloat()
        setMeasuredDimension(width,height)
    }
    fun setPro(pro:Float){
        this.setpro = pro
        valueAnimator.start()
    }
    /**
     * dp转px
     *
     * @param values
     * @return
     */
    private fun dp2px(values: Float): Float {
        val density = resources.displayMetrics.density
        return values * density + 0.5f
    }
    private fun createAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofObject(FloatEvaluator(),0f,setpro)
        valueAnimator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.duration = 5000
        valueAnimator.interpolator = AccelerateInterpolator()
        return valueAnimator
    }
}