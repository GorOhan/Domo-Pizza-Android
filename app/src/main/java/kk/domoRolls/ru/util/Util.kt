package kk.domoRolls.ru.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
    timeSlots.map { isWorkingTimeForSlot(it) }.any { it }

fun formatNumber(input: String,format: String): String {
    // Ensure the input contains only digits
    require(input.all { it.isDigit() }) { "Input must contain only digits" }

    var formattedNumber = format
    var inputIndex = 0

    for (i in formattedNumber.indices) {
        if (formattedNumber[i] == '#') {
            require(inputIndex < input.length) { "Input does not have enough digits for the format" }
            formattedNumber = formattedNumber.replaceFirst('#', input[inputIndex])
            inputIndex++
        }
    }

    return formattedNumber
}

fun copyTextToClipboard(context: Context, text: String) {
    // Get the clipboard manager
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // Create a ClipData object with the text to copy
    val clip = ClipData.newPlainText("Промокод скопирован", text)
    Toast.makeText(context, "Промокод скопирован", Toast.LENGTH_SHORT).show()

    // Set the ClipData object as the primary clip
    clipboard.setPrimaryClip(clip)
}

fun Long.convertMillisToDateFormat(): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val date = Date(this)
    return dateFormat.format(date)
}

fun String.formatToScreenType(): String {
    // Define the input and output date formats
    val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    // Parse the input date string
    val date = inputFormat.parse(this)

    // Format the date into the output format
    return outputFormat.format(date)
}