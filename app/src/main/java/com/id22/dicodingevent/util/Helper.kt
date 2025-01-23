package com.id22.dicodingevent.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class Helper {
    companion object {
        fun convertDateString(input: String, formatDate: String = "MMM dd, yyyy HH:mm"): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val outputFormat = DateTimeFormatter.ofPattern(formatDate, Locale.getDefault())
                val dateTime = LocalDateTime.parse(input, inputFormat)
                dateTime.format(outputFormat)
            } else {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM, dd yyyy", Locale.getDefault())
                val date: Date = inputFormat.parse(input) ?: return ""
                outputFormat.format(date)
            }
        }
    }
}
