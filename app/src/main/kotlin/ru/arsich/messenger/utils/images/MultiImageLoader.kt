package ru.arsich.messenger.utils.images


import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import ru.arsich.messenger.R

class MultiImageLoader(private val urls: Array<String>, private val receiver: MultiImageReceiver): ImageLoader {
    private val bitmaps: MutableList<Bitmap> = mutableListOf()
    private var interrupted = false

    private var lastTarget: SimpleTarget<Bitmap>? = null

    override fun load(context: Context) {
        if (interrupted) {
            bitmaps.clear()
            return
        }

        val bitmapSize = bitmaps.size
        if (bitmapSize == urls.size) {
            receiver.onImagesReceive(bitmaps)
            return
        }

        val url = urls[bitmapSize]

        lastTarget = object: SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                if (resource != null) {
                    bitmaps.add(resource)
                } else {
                    interrupted = true
                }
                load(context)
            }
        }

        // Glide has internal cache inside
        // we do not need store bitmaps in LruCache here
        Glide.with(context)
                .load(url)
                .asBitmap()
                .error(R.drawable.camera)
                .into(lastTarget)
    }

    override fun interrupt() {
        lastTarget?.let {
            Glide.clear(it)
        }
        bitmaps.clear()
        interrupted = true
    }
}