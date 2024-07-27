package kk.domoRolls.ru.util

import kk.domoRolls.ru.domain.model.TimeSlot
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getCurrentWeekdayInRussian(): String {
    val russianLocale = Locale("ru")

    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    val weekdays = DateFormatSymbols(russianLocale).weekdays

    return weekdays[dayOfWeek]
}

fun getCurrentTimeFormatted(): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
}

fun isWorkingTimeForSlot(timeSlot: TimeSlot): Boolean {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    val openTime = LocalTime.parse(timeSlot.open, formatter)
    val closeTime = LocalTime.parse(timeSlot.close, formatter)
    val userTime = LocalTime.parse(getCurrentTimeFormatted(), formatter)

    return userTime > openTime && userTime < closeTime
}

fun isWorkingTime(timeSlots: List<TimeSlot>) =
    timeSlots.map { isWorkingTimeForSlot(it) }.any { true }
