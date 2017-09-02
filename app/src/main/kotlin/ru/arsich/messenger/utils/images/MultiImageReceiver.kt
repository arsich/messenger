package ru.arsich.messenger.utils.images

import android.graphics.Bitmap

interface MultiImageReceiver {
    fun onImagesReceive(bitmaps: List<Bitmap>)
}