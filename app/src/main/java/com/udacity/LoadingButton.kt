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
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var downloadProgress: Float = 0f
    private var btnText: String
    private val valueAnimator = ValueAnimator()
    private val textRect = Rect()
    private var buttonBackgroundColor = R.attr.buttonBackgroundColor
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) {
            _, _, newValue ->
            when (newValue) {
                ButtonState.Loading -> {
                    downloadProgress = 0f
                    btnText="Downloading"
                    buttonBackgroundColor = Color.DKGRAY
                    valueAnimator.apply {
                        setFloatValues(0f, 1f)
                        repeatMode = ValueAnimator.REVERSE
                        repeatCount = ValueAnimator.INFINITE
                        duration = 6000
                        addUpdateListener {
                            downloadProgress = animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
                    custom_button.isEnabled = false
                }
                ButtonState.Completed -> {
                    btnText="Downloaded"
                    buttonBackgroundColor=Color.LTGRAY
                    valueAnimator.cancel()
                    downloadProgress = 0f
                    custom_button.isEnabled = true
                }
                ButtonState.Clicked -> {}
            }
    }
    private val txtPaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)
        .apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 40.0f
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.LTGRAY
    }

    init {
        isClickable = true
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                btnText = "Download"
            } finally {
                recycle()
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(buttonBackgroundColor)
        txtPaint.getTextBounds(btnText, 0, btnText.length, textRect)
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
        if (buttonState == ButtonState.Loading) {
            paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            canvas?.drawRect(0f, 0f, downloadProgress * measuredWidth, measuredHeight.toFloat(), paint)

        }
        paint.color = Color.YELLOW
        canvas?.drawArc(
            paddingStart.toFloat()+20,
            paddingTop.toFloat()+20,
            measuredHeight.toFloat() - paddingBottom.toFloat() - 20,
            measuredHeight.toFloat() - paddingBottom.toFloat() - 20,
            0f,
            downloadProgress*300f,
            true,
            paint
        )

        canvas?.drawText(btnText, measuredWidth.toFloat() / 2f, measuredHeight.toFloat() / 2f - textRect.centerY(), txtPaint)

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

    fun setCustomButtonState(state: ButtonState) {
        buttonState = state
    }


}