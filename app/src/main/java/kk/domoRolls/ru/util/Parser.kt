package kk.domoRolls.ru.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kk.domoRolls.ru.domain.model.Promo
import kk.domoRolls.ru.domain.model.WorkingHoursWrapper

fun String.parseJson(): List<Promo>? {
    try {

        val gson = Gson()
        val type = object : TypeToken<List<Promo>>() {}.type

        return gson.fromJson(this, type)
    } catch (e: Exception) {
        return null
    }
}

fun String.parseToWorkingHours(): WorkingHoursWrapper? {
    try {
        val gson = Gson()
        val type = object : TypeToken<WorkingHoursWrapper>() {}.type

        return gson.fromJson(this, type)

    } catch (e: Exception) {
        return null
    }
}