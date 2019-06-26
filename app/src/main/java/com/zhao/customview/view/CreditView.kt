package com.zhao.customview.view

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.zhao.customview.R


class CreditView : View {
    private val levelStrs  = arrayOf("较差","中等","良好","优秀","极好","完美")
    private val levels  = arrayOf("350","550","600","650","700","950","1150")
    private val proPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = wStrokeWidth
            strokeCap = Paint.Cap.ROUND
            color = Color.WHITE
//          shader = LinearGradient(paddingLeft.toFloat(),radus,(width-paddingRight).toFloat(),radus,Color.RED,Color.BLUE,Shader.TileMode.CLAMP)
            shader = LinearGradient(paddingLeft.toFloat(),radus,(width-paddingRight).toFloat(),radus, intArrayOf(Color.RED,Color.BLUE,Color.GREEN),null,Shader.TileMode.CLAMP)
        }
    }  //外环进度画笔
    private val proTextPaint by lazy {
        Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            textSize = 35f
            textAlign = Paint.Align.CENTER
        }
    }
    private var radus = 0f//外环半径
    private val wPaint = Paint() //外环画笔
    private val nPaint = Paint() //内环画笔
    private val skPaint = Paint() //小刻度
    private val lkPaint = Paint() //大刻度
    private val mBitmapPaint = Paint()//小圆点画笔
    private val sTextPaint = Paint() //刻度上的字体
    private val wStrokeWidth = 10f
    private val nStrokeWidth = 30f
   private val cirLocation by lazy {  floatArrayOf(paddingLeft.toFloat(),radus) }
    private var distance = 15f//外环于内环的间距
    private var defalutSize = 250f//默认长宽
    private var progress = 0f
    private var setpro = 0f
    private val valueAnimator by lazy { createAnimator() }
    private var maxSize:Float = 0f
    private var minSize:Float = 0f
    private val wRectf by lazy { WRectf(paddingLeft.toFloat(),paddingTop.toFloat(), (width-paddingRight).toFloat(),(width-paddingBottom).toFloat())}
    private val nRectf by lazy { NRectf(paddingLeft+distance, paddingTop+distance,width-paddingRight - distance,width-paddingBottom - distance)}
    private val lScale by lazy { LittleScale(radus, paddingTop+distance-nStrokeWidth/2,radus,paddingTop+distance+nStrokeWidth/2) }
    private val bScale by lazy { BigScale(lScale.startX, lScale.startY,lScale.startX,lScale.stopY+dp2px(3f)) }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val typedArray =context?.obtainStyledAttributes(attrs, R.styleable.credit)
        maxSize = typedArray?.getDimension(R.styleable.credit_maxSize,defalutSize)!!
        minSize = typedArray?.getDimension(R.styleable.credit_minSize,defalutSize)!!
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    init{
        defalutSize = dp2px(defalutSize)
        distance = dp2px(distance)


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

        skPaint.isAntiAlias = true
        skPaint.color = Color.WHITE
        skPaint.alpha = 80

        lkPaint.isAntiAlias = true
        lkPaint.color = Color.WHITE
        lkPaint.strokeWidth = 2f

        sTextPaint.isAntiAlias = true
        sTextPaint.color = Color.WHITE
        sTextPaint.textSize = 20f
        sTextPaint.alpha = 100
        sTextPaint.textAlign = Paint.Align.CENTER

        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.color = Color.WHITE

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        //画外环
        val wRectf = RectF(wRectf.left,wRectf.top,wRectf.right,wRectf.bottom)
        canvas.drawArc(wRectf,180f,180f,false,wPaint)
        //画内环
        val nRectf = RectF(nRectf.left,nRectf.top,nRectf.right,nRectf.bottom)
        canvas.drawArc(nRectf,180f,180f,false,nPaint)
        //画小刻度
        canvas.save()
        canvas.rotate(-90f,radus,radus)
        for(i in 1..36){
            canvas.drawLine(lScale.startX,lScale.startY,lScale.stopX,lScale.stopY,skPaint)
            canvas.rotate(5f,radus,radus)
        }
        canvas.restore() //回滚save之前得状态
        //画大刻度
        canvas.save()
        val baseLineY = bScale.stopY+ dp2px(10f)
        canvas.rotate(-90f,radus,radus)
        for(i in 0 until levels.size){
            canvas.drawLine(bScale.startX,bScale.startY,bScale.stopX,bScale.stopY,lkPaint)
            canvas.drawText(levels[i],bScale.stopX,baseLineY,sTextPaint)
            canvas.rotate(180f/(levels.size-1),radus,radus)
        }
        canvas.restore() //回滚save之前得状态
        //画信用等级
        canvas.save() //相当于保存之前画布的界面，然后在新的画布操作
        canvas.rotate(-75f,radus,radus)
        for(i in 0 until levelStrs.size){
            canvas.drawText(levelStrs[i],bScale.stopX,baseLineY,sTextPaint)
            canvas.rotate(180f/levelStrs.size,radus,radus)
        }
        canvas.restore() //回滚save之前得状态
        //把渐变设置到笔刷
//        proPaint.shader = LinearGradient(paddingLeft.toFloat(),radus,cirLocation[0],cirLocation[1],Color.RED,Color.BLUE,Shader.TileMode.CLAMP)
        //画外环进度
        canvas.drawArc(wRectf,180f,progress,false,proPaint)
       //画外圆环进度的小圆点
        val path = Path()
        path.addArc(wRectf,180f,progress)
        val pathMeasure = PathMeasure(path,false)
        pathMeasure.getPosTan(pathMeasure.length,cirLocation,null)
        canvas.drawCircle(cirLocation[0],cirLocation[1],8f,mBitmapPaint)

        //画进度文本
        val per = (progress/180 * 800+350).toInt()
        canvas.drawText("信用积分:$per",radus,(2*height/3).toFloat(),proTextPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width: Int
        val widthMode=MeasureSpec.getMode(widthMeasureSpec)
        val widthSize=MeasureSpec.getSize(widthMeasureSpec)
        val heightMode=MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val height =  when(heightMode){
            MeasureSpec.EXACTLY->{
                heightSize
            }
            else->{
                radus.toInt()+paddingBottom
            }
        }
        width = when {
                widthSize>maxSize -> maxSize.toInt()
                widthSize<minSize -> minSize.toInt()
                else -> widthSize
            }
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