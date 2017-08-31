package ru.arsich.messenger.utils

import android.graphics.Bitmap

interface ImageReceiver {
    fun onReceive(bitmaps: List<Bitmap>)
}