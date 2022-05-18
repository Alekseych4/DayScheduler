package com.open.day.dayscheduler.util

import android.icu.util.Calendar
import android.icu.util.DateInterval
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeCountingUtils {
    companion object {
        fun getCurrentDayInterval(): DateInterval {
            val dayStart = Calendar.getInstance()
            dayStart.set(Calendar.SECOND, 0)
            dayStart.set(Calendar.MINUTE, 0)
            dayStart.set(Calendar.HOUR_OF_DAY, 0)

            val dayEnd = Calendar.getInstance()
            dayEnd.set(Calendar.SECOND, 59)
            dayEnd.set(Calendar.MINUTE, 59)
            dayEnd.set(Calendar.HOUR_OF_DAY, 23)

            return DateInterval(dayStart.timeInMillis, dayEnd.timeInMillis)
        }

        fun utcMillisToLocalHoursAndMinutes(utcMillis: Long): String {
            val sdf = SimpleDateFormat("kk:mm", Locale.getDefault())
            return sdf.format(Date(utcMillis))
        }

        fun utcMillisToLocalDayDateMonth(utcMillis: Long): String {
            val sdf = SimpleDateFormat("cc, dd LLL", Locale.getDefault())
            return sdf.format(Date(utcMillis))
        }

        fun localHourToUTCHour(localHour: Int): Int {
            val offset = Calendar.getInstance().timeZone.getOffset(Date().time)
            return localHour - offset
        }

        fun changedValue(oldV: Int, newV: Int): Int {
            return newV - oldV
        }

        fun areTimePeriodsOverlap(s1: Long, e1: Long, s2: Long, e2: Long): Boolean {
            if (s1 in s2 until e2) return true
            if (e1 in (s2+1)..e2) return true
            if (s2 in s1..e1 && e2 in s1..e1) return true

            return false
        }
    }

}