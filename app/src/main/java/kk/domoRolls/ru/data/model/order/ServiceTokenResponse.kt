package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class ServiceTokenResponse(
    @SerializedName("correlationId")
    val correlationId: String,
    @SerializedName("token")
    val token: String,
)