package kk.domoRolls.ru.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import kk.domoRolls.ru.domain.model.Promo
import java.lang.reflect.Type

fun String.parseJson(): List<Promo>? {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val listMyDataType: Type = Types.newParameterizedType(List::class.java, Promo::class.java)
    val jsonAdapter = moshi.adapter<List<Promo>>(listMyDataType)

    return jsonAdapter.fromJson(this)
}