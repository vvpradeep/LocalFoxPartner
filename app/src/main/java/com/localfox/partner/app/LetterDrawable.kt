package com.localfox.partner.app

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.localfox.partner.R

class LetterDrawable(private val letter: String, val context: Context) : Drawable() {
    val customFont: Typeface? = ResourcesCompat.getFont(context, R.font.inter_bold)

    private val borderPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#F3F4F6")
        strokeWidth = 4f
        pathEffect = CornerPathEffect(17f)
    }
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#94A3B8")
        textSize = 48f
        typeface = customFont
        textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()

        // Draw the border
        canvas.drawRect(bounds, borderPaint)

        //canvas.drawColor(Color.parseColor("#F3F4F6"))

        // Draw the letter in the center
        canvas.drawText(letter.toString(), centerX, centerY - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
    }

    override fun setAlpha(alpha: Int) {
        // Not needed for this implementation
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // Not needed for this implementation
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}
