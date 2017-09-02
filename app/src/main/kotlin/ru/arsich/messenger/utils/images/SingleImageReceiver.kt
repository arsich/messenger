package ru.arsich.messenger.utils.images

import android.graphics.Bitmap


interface SingleImageReceiver {
    fun onImageReceive(bitmap: Bitmap, url: String)
}