package kk.domoRolls.ru.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

data class ChooseTime(
    var startTime: Date,
    var endTime: Date,
    var isSelected: Boolean = false
)
 fun generateTimeItems(calendar: Calendar): List<ChooseTime> {
    val timeIntervals = mutableListOf<ChooseTime>()

    val startDate = calendar.time.apply {
        calendar.add(Calendar.MINUTE, 90) // Advance the calendar by 90 minutes
    }

    var currentDate = startDate

    val endOfDay = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    while (currentDate.before(endOfDay)) {
        val endDate = Calendar.getInstance().apply {
            time = currentDate
            add(Calendar.MINUTE, 30)
        }.time

        timeIntervals.add(ChooseTime(startTime = currentDate, endTime = endDate))

        currentDate = endDate
    }

    // Set the first item as selected, if the list is not empty
    if (timeIntervals.isNotEmpty()) {
        timeIntervals[0].isSelected = true
    }

    return timeIntervals
}


fun ChooseTime.getTimeRange(): String {
    val formatter = SimpleDateFormat("HH:mm")
    val start = formatter.format(startTime)
    val end = formatter.format(endTime)
    return "$start - $end"
}