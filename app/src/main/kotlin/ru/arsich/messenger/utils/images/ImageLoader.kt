package ru.arsich.messenger.utils.images

import android.content.Context


interface ImageLoader {
    /**
     * @param context Use applicationContext for preventing memory leaks
     */
    fun load(context: Context)
    fun interrupt()
}