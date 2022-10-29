package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var progress: Float = 0f
    private var buttonText: String
    private val valueAnimator = ValueAnimator()
    private val textRect = Rect()
    private var buttonBackgroundColor = R.attr.buttonBackgroundColor
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) {
            property, oldValue, newValue ->
            when (newValue) {
                ButtonState.Loading -> {
                    setText("We are Downloading")
                    setBgColor("#004349")
                    valueAnimator.apply {
                        setFloatValues(0f, 1f)
                        repeatMode = ValueAnimator.REVERSE
                        repeatCount = ValueAnimator.INFINITE
                        duration = 6000
                        addUpdateListener {
                            progress = animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
                    disableLoadingButton()
                }
                ButtonState.Completed -> {
                    setText("Downloaded")
                    setBgColor("#07C2AA")
                    valueAnimator.cancel()
                    resetProgress()
                    enableLoadingButton()
                }
                ButtonState.Clicked -> {
                }
            }
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        style = Paint.Style.FILL
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    init {
        isClickable = true
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                buttonText = "Download"
            } finally {
                recycle()
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(buttonBackgroundColor)
        textPaint.getTextBounds(buttonText, 0, buttonText.length, textRect)
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
        if (buttonState == ButtonState.Loading) {
            paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            canvas?.drawRect(0f, 0f, progress * measuredWidth, measuredHeight.toFloat(), paint)
//            setText("${(progress*100).toString()} %")
        }
        paint.color = Color.YELLOW
        val arcDiameter = 20
        val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter

        canvas?.drawArc(paddingStart.toFloat()+arcDiameter,
            paddingTop.toFloat()+arcDiameter,
            arcRectSize,
            arcRectSize,
            0f,
            progress*360f,
            true,
            paint
        )

        canvas?.drawText(buttonText, measuredWidth.toFloat() / 2f, measuredHeight.toFloat() / 2f - textRect.centerY(), textPaint)

    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun enableLoadingButton() {
        custom_button.isEnabled = true
    }
    private fun disableLoadingButton() {
        custom_button.isEnabled = false
    }
    fun setLoadButtonState(state: ButtonState) {
        buttonState = state
    }
    fun getLoadButtonState() : ButtonState{
       return buttonState
    }
    private fun resetProgress() {
        progress = 0f
    }
    fun setText(text: String) {
        this.buttonText = text
    }
    private fun setBgColor(color: String) {
        this.buttonBackgroundColor = Color.parseColor(color)
    }


}