package ru.arsich.messenger.ui.views

import android.graphics.*
import android.graphics.drawable.Drawable

class CenterCropDrawable(private val target: Drawable) : Drawable() {

    override fun setBounds(bounds: Rect) {
        super.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        val sourceRect = RectF(0f, 0f, target.intrinsicWidth.toFloat(), target.intrinsicHeight.toFloat())
        val screenRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())

        val matrix = Matrix()
        matrix.setRectToRect(screenRect, sourceRect, Matrix.ScaleToFit.CENTER)

        val inverse = Matrix()
        matrix.invert(inverse)
        inverse.mapRect(sourceRect)

        target.setBounds(Math.round(sourceRect.left), Math.round(sourceRect.top),
                Math.round(sourceRect.right), Math.round(sourceRect.bottom))

        super.setBounds(left, top, right, bottom)
    }

    override fun draw(canvas: Canvas) {
        canvas.save(Canvas.CLIP_SAVE_FLAG)
        canvas.clipRect(bounds)
        target.draw(canvas)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        target.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        target.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = target.opacity
}