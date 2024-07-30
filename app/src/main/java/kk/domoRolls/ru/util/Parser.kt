package kk.domoRolls.ru.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoStory
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.WorkingHoursWrapper

fun String.parseToPromos(): List<PromoStory>? {
    try {

        val gson = Gson()
        val type = object : TypeToken<List<PromoStory>>() {}.type

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

fun String.parseToPromoCodes(): List<PromoCode>? {
    try {

        val gson = Gson()
        val type = object : TypeToken<List<PromoCode>>() {}.type

        return gson.fromJson(this, type)
    } catch (e: Exception) {
        return null
    }
}

fun String.parseToListString(): List<String>? {
    try {

        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type

        return gson.fromJson(this, type)
    } catch (e: Exception) {
        return null
    }
}

fun String.parseToGiftProduct(): GiftProduct? {
    try {
        val gson = Gson()
        val type = object : TypeToken<GiftProduct>() {}.type

        return gson.fromJson(this, type)

    } catch (e: Exception) {
        return null
    }
}