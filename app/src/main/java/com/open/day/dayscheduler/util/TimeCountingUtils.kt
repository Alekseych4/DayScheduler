package com.open.day.dayscheduler.util

import android.icu.util.Calendar
import android.icu.util.DateInterval

class TimeCountingUtils {
    companion object {
        fun getCurrentDayInterval(): DateInterval {
            val dayStart = Calendar.getInstance()
            dayStart.set(Calendar.SECOND, 0)
            dayStart.set(Calendar.MINUTE, 0)
            dayStart.set(Calendar.HOUR, 0)

            val dayEnd = Calendar.getInstance()
            dayEnd.set(Calendar.SECOND, 59)
            dayEnd.set(Calendar.MINUTE, 59)
            dayEnd.set(Calendar.HOUR, 23)

            return DateInterval(dayStart.timeInMillis, dayEnd.timeInMillis)
        }
    }

}