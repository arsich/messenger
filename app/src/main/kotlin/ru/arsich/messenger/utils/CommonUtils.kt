package ru.arsich.messenger.utils

import android.content.Context
import android.os.Build
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
            val now = Calendar.getInstance()
            now.set(Calendar.HOUR, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
            val today = now.time.time

            val calendar = Calendar.getInstance()
            val tz = TimeZone.getDefault()
            calendar.timeInMillis = timestamp.toLong() * 100
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))

            val sdfToday = SimpleDateFormat("HH:mm", locale)
            val sdfDay = SimpleDateFormat("dd MMM", locale)
            if (today < timestamp * 1000) {
                return sdfToday.format(calendar.time).replace(".", "")
            } else {
                return sdfDay.format(calendar.time).replace(".", "")
            }
        }
    }
}