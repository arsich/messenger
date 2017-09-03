package ru.arsich.messenger.utils.images

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import ru.arsich.messenger.R


class SingleImageLoader(private val url: String, private val receiver: SingleImageReceiver) : ImageLoader {

    private var lastTarget: SimpleTarget<Bitmap>? = null

    override fun load(context: Context) {
        lastTarget = object: SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                resource?.let {
                    receiver.onImageReceive(it, url)
                }
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
    }
}