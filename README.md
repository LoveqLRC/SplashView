# 先看一下要实现的效果
![要实现的效果](https://upload-images.jianshu.io/upload_images/2018603-f7bd29517886ff6a.gif?imageMogr2/auto-orient/strip)

从上面的动画分析，可以分为以下几步
1.  六个圆围绕圆心旋转。
2. 旋转完成后，先进行扩散，后聚合。
3. 聚合完成后，从圆心慢慢扩散，展示后面的视图。

# 第一步画六个圆

```kotlin
package rc.loveq.splashview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
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
                val angle = i * rotateAngle
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
```
![六个静态的圆](https://upload-images.jianshu.io/upload_images/2018603-deea1fc2f50bcefc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


# 第二步让六个圆动起来

```kotlin
package rc.loveq.splashview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
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
```
上面的代码中，在`RotateStatus `构造函数中，使用`ValueAnimator `来动态更新每个圆的角度，从而使六个圆动起来。
![旋转6个圆](https://upload-images.jianshu.io/upload_images/2018603-6e75c513501e82a0.gif?imageMogr2/auto-orient/strip)

# 第三步实现聚合效果

```kotlin
package rc.loveq.splashview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
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
    private var currentRotateRadius = rotateRadius

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


            valueAnimator.addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    splashState = MerginStatus()
                }


            })

            valueAnimator.start()
        }

        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
            drawCircle(canvas)
        }


    }


    /**
     * 2.聚合扩散
     */
    inner class MerginStatus : SplashState() {

        init {
            val valueAnimator = ValueAnimator.ofFloat(circleRadius, rotateRadius)
                .apply {
                    duration = rotateDuration.toLong()
                    interpolator = OvershootInterpolator()
                }
            valueAnimator.addUpdateListener {
                currentRotateRadius = it.animatedValue as Float
                invalidate()
            }

            //这里没有使用start,而是使用reverse
            //reverse 会从ValueAnimator end的地方开始，应用OvershootInterpolator进行值的更新
            valueAnimator.reverse()


        }

        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
            drawCircle(canvas)
        }


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
```
旋转完成后，进行聚合动画，动画使用的差值器是`OvershootInterpolator`，这个差值器是有弹性回弹的效果。

![聚合效果](https://upload-images.jianshu.io/upload_images/2018603-76febc6a8945afc0.gif?imageMogr2/auto-orient/strip)

# 第四步扩展
```kotlin
package rc.loveq.splashview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import kotlin.math.hypot
import kotlin.properties.Delegates
import kotlin.math.cos
import kotlin.math.sin
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


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
    private var currentRotateRadius = rotateRadius

    // 6个圆半径
    private val circleRadius = 18f

    // 当前splash的状态
    private var splashState: SplashState? = null


    // 旋转动画时长
    private val rotateDuration = 1200


    // 当前大圆的旋转角度
    private var currentRotateAngle = 0f


    // 扩散圆的半径
    private var currentHoleRadius = 0f

    private var holePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.STROKE
            color = bgColor
        }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //获取中心点
        centerX = (w / 2).toFloat()
        centerY = (h / 2).toFloat()

        //当前View对角线的一半
        distance = (hypot(w.toDouble(), h.toDouble()) / 2f).toFloat()

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
                    repeatCount = 1
                    duration = rotateDuration.toLong()
                    interpolator = LinearInterpolator()
                }

            valueAnimator.addUpdateListener {
                currentRotateAngle = it.animatedValue as Float

                invalidate()
            }


            valueAnimator.addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    splashState = MerginStatus()
                }


            })

            valueAnimator.start()
        }

        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
            drawCircle(canvas)
        }


    }


    /**
     * 2.聚合扩散
     */
    inner class MerginStatus : SplashState() {

        init {
            val valueAnimator = ValueAnimator.ofFloat(circleRadius, rotateRadius)
                .apply {
                    duration = rotateDuration.toLong()
                    interpolator = OvershootInterpolator()
                }
            valueAnimator.addUpdateListener {
                currentRotateRadius = it.animatedValue as Float
                invalidate()
            }


            valueAnimator.addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    splashState = ExpandState()
                }


            })

            //这里没有使用start,而是使用reverse
            //reverse 会从ValueAnimator end的地方开始，应用OvershootInterpolator进行值的更新
            valueAnimator.reverse()


        }

        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
            drawCircle(canvas)
        }


    }

    /**
     * 3.扩展
     */
    inner class ExpandState : SplashState() {

        init {
            val valueAnimator = ValueAnimator.ofFloat(circleRadius, distance)
                .apply {
                    duration = rotateDuration.toLong()
                    interpolator = LinearInterpolator()
                }
            valueAnimator.addUpdateListener {
                currentHoleRadius = it.animatedValue as Float
                invalidate()
            }
            valueAnimator.start()

        }


        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
        }


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
        if (currentHoleRadius > 0) {
            //绘制空心圆
            val strokeWidth = distance - currentHoleRadius
            val radius = strokeWidth / 2 + currentHoleRadius
            holePaint.strokeWidth = strokeWidth
            canvas.drawCircle(centerX, centerY, radius, holePaint)

        } else {
            //画背景色
            canvas.drawColor(bgColor)
        }

    }

}
```
水波的效果，就是通过画空心圆实现，不断增加空心圆的半径，减少画笔的宽度。
![最终效果](https://upload-images.jianshu.io/upload_images/2018603-f7bd29517886ff6a.gif?imageMogr2/auto-orient/strip)



