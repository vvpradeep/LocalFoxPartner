package com.localfox.partner.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity

class AppUtils {
    companion object {
        @JvmStatic
        fun getLetterDrawable(context: Context, letters: String): Drawable? {
            // Create a bitmap with the same density as the display
            val metrics = context.resources.displayMetrics
            val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, metrics).toInt()
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

            // Draw the letters onto the bitmap
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.setColor(Color.WHITE)
            paint.setTextSize(size * 0.5f)
            paint.setTextAlign(Paint.Align.CENTER)
            paint.setAntiAlias(true)
            canvas.drawPaint(paint)
            paint.setColor(Color.BLACK)
            canvas.drawText(
                letters,
                canvas.getWidth() / 2f,
                canvas.getHeight() / 2f + size * 0.18f,
                paint
            )

            // Create a BitmapDrawable from the bitmap
            val drawable = BitmapDrawable(context.resources, bitmap)
            drawable.isFilterBitmap = true
            drawable.gravity = Gravity.FILL
            return drawable
        }
    }
}