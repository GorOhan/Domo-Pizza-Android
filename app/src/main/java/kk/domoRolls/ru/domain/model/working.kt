package kk.domoRolls.ru.domain.model

import com.google.gson.annotations.SerializedName

data class WorkingHoursWrapper(
    @SerializedName("working_hours")
    val workingHours: HashMap<String, List<TimeSlot>>
)

data class TimeSlot(
    val open: String = "",
    val close: String = ""
)