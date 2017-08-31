package ru.arsich.messenger.ui.views


import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.util.AttributeSet
import android.widget.ImageView
import java.util.*

/**
 * Improved version of https://github.com/stfalcon-studio/MultiImageView
 */
class MultiImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {
    // Shape of view
    var shape = Shape.NONE
        set(value) {
            field = value
            invalidate()
        }

    // Corners radius for rectangle shape
    var rectCorners = 100

    private val paintDivider = Paint(Paint.ANTI_ALIAS_FLAG)

    var dividerColor = Color.TRANSPARENT
        set(value) {
            field = value
            paintDivider.color = value
            invalidate()
        }
    var dividerWidth = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        paintDivider.isAntiAlias = true
        paintDivider.color = dividerColor
    }

    private val bitmaps = ArrayList<Bitmap>()
    private val path = Path()
    private val rect = RectF()
    private var multiDrawable: Drawable? = null

    /**
     * Add image to view
     */
    fun addImage(bitmap: Bitmap) {
        bitmaps.add(bitmap)
        refresh()
    }

    /**
     * Add images to view
     */
    fun addImages(newBitmaps: List<Bitmap>) {
        bitmaps.addAll(newBitmaps)
        refresh()
    }

    /**
     * Remove all images
     */
    fun clear() {
        bitmaps.clear()
        refresh()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refresh()
    }

    /**
     * recreate MultiDrawable and set it as Drawable to ImageView
     */
    private fun refresh() {
        multiDrawable = MultiDrawable(bitmaps, dividerWidth)
        setImageDrawable(multiDrawable)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null || drawable == null || shape == Shape.NONE) {
            super.onDraw(canvas)
            return
        }
        path.reset()
        // ImageView size
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        if (shape == Shape.RECTANGLE) {
            path.addRoundRect(rect, rectCorners.toFloat(),
                    rectCorners.toFloat(), Path.Direction.CW)
        } else {
            path.addOval(rect, Path.Direction.CW)
        }
        // clip with shape
        canvas.drawPath(path, paintDivider)
        canvas.clipPath(path)
        super.onDraw(canvas)
    }

    // Types of shape
    enum class Shape {
        CIRCLE, RECTANGLE, NONE
    }
}

class MultiDrawable(val bitmaps: ArrayList<Bitmap>, val dividerWidth: Float) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val items = ArrayList<PhotoItem>()

    /**
     * Create PhotoItem with position and size depends of count of images
     */
    private fun init() {
        items.clear()
        val halfOfDivider = (dividerWidth / 2).toInt()

        if (bitmaps.size == 1) {
            val bitmap = scaleCenterCrop(bitmaps[0], bounds.width(), bounds.height())
            items.add(PhotoItem(bitmap, Rect(0, 0, bounds.width(), bounds.height())))
        } else if (bitmaps.size == 2) {
            val bitmap1 = scaleCenterCrop(bitmaps[0], bounds.width(), bounds.height() / 2)
            val bitmap2 = scaleCenterCrop(bitmaps[1], bounds.width(), bounds.height() / 2)
            items.add(PhotoItem(bitmap1, Rect(0, 0, (bounds.width() / 2) - halfOfDivider, bounds.height())))
            items.add(PhotoItem(bitmap2, Rect((bounds.width() / 2) + halfOfDivider, 0, bounds.width(), bounds.height())))
        } else if (bitmaps.size == 3) {
            val bitmap1 = scaleCenterCrop(bitmaps[0], bounds.width(), bounds.height() / 2)
            val bitmap2 = scaleCenterCrop(bitmaps[1], bounds.width() / 2, bounds.height() / 2)
            val bitmap3 = scaleCenterCrop(bitmaps[2], bounds.width() / 2, bounds.height() / 2)
            items.add(PhotoItem(bitmap1, Rect(0, 0, (bounds.width() / 2) - halfOfDivider, bounds.height())))
            items.add(PhotoItem(bitmap2, Rect((bounds.width() / 2) + halfOfDivider, 0, bounds.width(), (bounds.height() / 2) - halfOfDivider)))
            items.add(PhotoItem(bitmap3, Rect((bounds.width() / 2) + halfOfDivider, (bounds.height() / 2) + halfOfDivider, bounds.width(), bounds.height())))
        } else if (bitmaps.size == 4) {
            val bitmap1 = scaleCenterCrop(bitmaps[0], bounds.width() / 2, bounds.height() / 2)
            val bitmap2 = scaleCenterCrop(bitmaps[1], bounds.width() / 2, bounds.height() / 2)
            val bitmap3 = scaleCenterCrop(bitmaps[2], bounds.width() / 2, bounds.height() / 2)
            val bitmap4 = scaleCenterCrop(bitmaps[3], bounds.width() / 2, bounds.height() / 2)
            items.add(PhotoItem(bitmap1, Rect(0, 0, (bounds.width() / 2) - halfOfDivider, (bounds.height() / 2) - halfOfDivider)))
            items.add(PhotoItem(bitmap2, Rect(0, (bounds.height() / 2) + halfOfDivider, (bounds.width() / 2) - halfOfDivider, bounds.height())))
            items.add(PhotoItem(bitmap3, Rect((bounds.width() / 2) + halfOfDivider, 0, bounds.width(), (bounds.height() / 2) - halfOfDivider)))
            items.add(PhotoItem(bitmap4, Rect((bounds.width() / 2) + halfOfDivider, (bounds.height() / 2) + halfOfDivider, bounds.width(), bounds.height())))
        }
    }

    override fun draw(canvas: Canvas?) {
        if (canvas != null) {
            items.forEach {
                canvas.drawBitmap(it.bitmap, bounds, it.position, paint)
            }
        }
    }

    /**
     * scale and center crop image
     */
    private fun scaleCenterCrop(source: Bitmap, newHeight: Int, newWidth: Int): Bitmap {
        return ThumbnailUtils.extractThumbnail(source, newWidth, newHeight)
    }

    /***
     * Data class for store bitmap and position
     */
    data class PhotoItem(val bitmap: Bitmap, val position: Rect)


    //***Needed to override***//
    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        init()
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter) {
        paint.colorFilter = colorFilter
    }
    //***------------------***//
}