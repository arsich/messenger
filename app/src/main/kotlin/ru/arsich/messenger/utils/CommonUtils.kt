package ru.arsich.messenger.utils

import android.content.Context
import android.os.Build
import com.vk.sdk.api.model.*
import ru.arsich.messenger.R
import java.text.SimpleDateFormat
import java.util.*


class CommonUtils {
    companion object {
        fun getLocale(context: Context):Locale {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return context.resources.configuration.locales[0]
            } else {
                return context.resources.configuration.locale
            }
        }

        fun getFormattedDate(timestamp: Int, locale: Locale?): String {
            val timestampInMillis = timestamp.toLong() * 1000
            val now = Calendar.getInstance()
            now.set(Calendar.HOUR, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
            val today = now.time.time

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestampInMillis

            val sdfToday = SimpleDateFormat("HH:mm", locale)
            val sdfDay = SimpleDateFormat("dd MMM", locale)
            if (today < timestampInMillis) {
                return sdfToday.format(calendar.time).replace(".", "")
            } else {
                return sdfDay.format(calendar.time).replace(".", "")
            }
        }

        fun getMessageAttachmentName(message: VKApiMessage, context: Context): String {
            if (message.attachments.isEmpty()) {
                return ""
            }
            var result = ""
            message.attachments.forEach {
                if (it is VKApiPhoto) {
                    // no label if photo exists
                    return ""
                } else if (result.isEmpty()) {
                    if (it is VKApiDocument) {
                        result = context.getString(R.string.document)
                    } else if (it is VKApiVideo) {
                        result = context.getString(R.string.video)
                    } else if (it is VKApiAudio) {
                        result = context.getString(R.string.audio)
                    } else if (it is VKApiPoll) {
                        result = context.getString(R.string.poll)
                    } else if (it is VKApiPost) {
                        result = context.getString(R.string.post)
                    } else if (it is VKApiPhotoAlbum) {
                        result = context.getString(R.string.album)
                    } else {
                        result = ""
                    }
                }
            }
            return result
        }
    }
}