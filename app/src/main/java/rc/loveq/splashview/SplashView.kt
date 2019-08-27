package rc.loveq.splashview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.R.attr.centerY
import android.R.attr.centerX


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

        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
            drawCircle(canvas)
        }

        private fun drawCircle(canvas: Canvas) {
            //每个圆的弧度
            val rotateAngle = (Math.PI * 2 / circleColor.size)
            // 根据6种颜色，获取6个球
            for (i in 0 until circleColor.size) {
                // 获取第i个圆的角度 ；如果旋转的话需要加上角度
                val angle = i * rotateAngle
                // x = r * cos(a) + centerX
                // y = r * sin(a) + centerY
                val cx = (currentRotateRadius * Math.cos(angle.toDouble()) + centerX) as Float
                val cy = (currentRotateRadius * Math.sin(angle.toDouble()) + centerY) as Float
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