package com.example.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintSet.Motion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PressProgressButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private val customStrokeWidth = 150f
    private val innerArcPadding = 20f
    private var progress = 0f
    private var duration = 1000L

    private var maxValue = 100

    private var onCompletionListener: OnCompletionListener? = null

    private var animator: ValueAnimator? = null

    var job: Job? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("라닉 Event: ", "${event}")

                performClick()

                job?.cancel()
                job = CoroutineScope(Dispatchers.Main).launch {
                    setProgress(100f)
                    Log.d("라닉 progress:", progress.toString())
                }

                return true
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                Log.d("라닉 Event: ", "${event}")

                clearProgress()
                return true
            }
            else -> {}
        }
        return false
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.AT_MOST) {
            heightSize = widthSize
        } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.EXACTLY) {
            widthSize = heightSize
        }

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas?) {
        val paint = Paint().apply {
            color = Color.RED
            strokeWidth = customStrokeWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        val mainLeft = 0f + (customStrokeWidth / 2)
        val mainTop = 0f + (customStrokeWidth / 2)

        val mainRight = width.toFloat() - (customStrokeWidth / 2)
        val mainBottom = height.toFloat() - (customStrokeWidth / 2)

        canvas?.drawArc(
            mainLeft,
            mainTop,
            mainRight,
            mainBottom,
            270f,
            progress * (360f / maxValue.toFloat()),
            false,
            paint
        )

        val innerArcPaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
        }

        val innerLeft = 0f + customStrokeWidth + innerArcPadding
        val innerTop = 0f + customStrokeWidth + innerArcPadding
        val innerRight = width.toFloat() - customStrokeWidth - innerArcPadding
        val innerBottom = width.toFloat() - customStrokeWidth - innerArcPadding

        canvas?.drawArc(
            innerLeft,
            innerTop,
            innerRight,
            innerBottom,
            90f,
            360f,
            false,
            innerArcPaint
        )
    }

    fun setProgress(progress: Float) {
        animator?.cancel()
        animator = null

        animator = ValueAnimator.ofFloat(this.progress, progress).apply {
            duration = this@PressProgressButton.duration
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                this@PressProgressButton.progress = (valueAnimator.animatedValue as Float)
                invalidate()

                if(this@PressProgressButton.progress >= 100f) {
                    complete()
                }
            }
        }
        animator?.start()
    }

    fun complete() {
        onCompletionListener?.onComplete()
        clearProgress()
    }

    fun clearProgress(isWithAnimation: Boolean = true) {
        job?.cancel()
        job = null

        this.progress = 0f
        if (isWithAnimation) {
            animator?.cancel()
            animator = null
        }
        invalidate()
    }

    fun setCompleteListener(onCompletionListener: OnCompletionListener) {
        this.onCompletionListener = onCompletionListener
    }

    fun interface OnCompletionListener {
        fun onComplete()
    }
}