package com.example.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProgressButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private val customStrokeWidth = 60f
    private val innerArcPadding = 30f
    private var progress = 0f
    private var duration = 300L

    private var onCompletionListener: OnCompletionListener? = null

    private var animator: ValueAnimator? = null

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
            progress * (360 / 100f),
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

        animator = ValueAnimator.ofFloat(this.progress, progress).apply {
            duration = this@ProgressButton.duration
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                this@ProgressButton.progress = (valueAnimator.animatedValue as Float)
                invalidate()
            }
        }
        animator?.start()
    }

    fun complete() {
        onCompletionListener?.onComplete()
    }

    fun clearProgress(isWithAnimation: Boolean = true) {
        this.progress = 0f
        if (isWithAnimation) {
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