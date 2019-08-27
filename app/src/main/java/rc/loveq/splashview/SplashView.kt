package rc.loveq.splashview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import kotlin.math.hypot
import kotlin.properties.Delegates
import kotlin.math.cos
import kotlin.math.sin


/**
 * Author：Rc
 * 0n 2019/8/26 22:42
 */
class SplashView(context: Context, attrs: AttributeSet) : View(context, attrs) {


    // 背景色
    private val bgColor = Color.WHITE

    // 6个圆的颜色
    private val circleColor: IntArray = context.resources.getIntArray(R.array.splash_circle_colors)

    // 6个圆画笔
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    //View中心点的x坐标
    var centerX by Delegates.notNull<Float>()

    //View中心点的y坐标
    var centerY by Delegates.notNull<Float>()

    //当前View对角线的一半
    var distance by Delegates.notNull<Float>()


    private val rotateRadius = 90f

    // 默认六个圆围绕的外围圆半径
    private val currentRotateRadius = rotateRadius

    // 6个圆半径
    private val circleRadius = 18f

    // 当前splash的状态
    private var splashState: SplashState? = null


    // 旋转动画时长
    private val rotateDuration = 1200


    // 当前大圆的旋转角度
    private var currentRotateAngle = 0f


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //获取中心点
        centerX = (w / 2).toFloat()
        centerY = (h / 2).toFloat()

        //当前View对角线的一半
        distance = (hypot(x.toDouble(), h.toDouble()) / 2).toFloat()

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //初始化默认状态
        splashState ?: splashState.apply {
            splashState = RotateStatus()
        }
        splashState!!.drawState(canvas)

    }


    /**
     * 闪屏页的状态的抽象类
     */
    abstract inner class SplashState {
        abstract fun drawState(canvas: Canvas)
    }

    /**
     * 1.旋转
     */
    inner class RotateStatus : SplashState() {

        init {
            val valueAnimator = ValueAnimator.ofFloat(0f, (Math.PI * 2).toFloat())
                .apply {
                    repeatCount = 2
                    duration = rotateDuration.toLong()
                    interpolator = LinearInterpolator()
                }

            valueAnimator.addUpdateListener {
                currentRotateAngle = it.animatedValue as Float

                invalidate()
            }


            valueAnimator.addListener {

            }

            valueAnimator.start()
        }

        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
            drawCircle(canvas)
        }

        private fun drawCircle(canvas: Canvas) {
            //每个圆的弧度
            val rotateAngle = (Math.PI * 2 / circleColor.size)
            // 根据6种颜色，获取6个球
            for (i in circleColor.indices) {
                // 获取第i个圆的角度 ；如果旋转的话需要加上角度
                val angle = i * rotateAngle + currentRotateAngle
                // x = r * cos(a) + centerX
                // y = r * sin(a) + centerY
                val cx = (currentRotateRadius * cos(angle) + centerX).toFloat()
                val cy = (currentRotateRadius * sin(angle) + centerY).toFloat()
                paint.color = circleColor[i]
                canvas.drawCircle(cx, cy, circleRadius, paint)
            }
        }

        private fun drawBackground(canvas: Canvas) {
            //画背景色
            canvas.drawColor(bgColor)

        }

    }


}