package com.example.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer.OnCompletionListener
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import kotlinx.coroutines.NonCancellable.start
import java.util.concurrent.TimeUnit

class ProgressButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private val customStrokeWidth = 40f
    private val innerArcPadding = 20f
    private var progress = 0f
    val angle: Float
        get() {
            return progress * (360 / 100)
        }


    private var onCompletionListener: OnCompletionListener? = null

    val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = TimeUnit.SECONDS.toMillis(1)
        interpolator = LinearInterpolator()
        addUpdateListener { valueAnimator ->
            progress = (valueAnimator.animatedValue as Float) * 360f
            invalidate()
        }
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

        Log.d("라닉 angle : ", angle.toString())
        canvas?.drawArc(mainLeft, mainTop, mainRight, mainBottom, 270f, angle, false, paint)

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
        if (animator.isRunning.not()) {
            animator.start()
        }

        this.progress = progress

        if (this.progress == 100f) {
            onCompletionListener?.onComplete()
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