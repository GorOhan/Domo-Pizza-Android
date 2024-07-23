package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class ServiceTokenRequest(
    @SerializedName("apiLogin")
    val apiLogin: String = "2f7a82d2-78a",
)