package kk.domoRolls.ru.util

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoStory
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.WorkingHoursWrapper
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.model.map.Polygon

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
    return try {
        Gson().fromJson(this, Array<PromoCode>::class.java).toList()
    } catch (e: Exception) {
        null
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

fun String.parseToMapData(): List<Polygon>? {
    return try {
        Gson().fromJson(this, Array<Polygon>::class.java).toList()

    } catch (e: Exception) {
        null
    }
}


fun String.parseToAddress(): Address? {
    try {
        val gson = Gson()
        val type = object : TypeToken<Address>() {}.type

        return gson.fromJson(this, type)

    } catch (e: Exception) {
        return null
    }
}

fun Address.toJson(): String {
    val gson: Gson = GsonBuilder().create()
    val userJson = gson.toJson(this)
    return userJson
}