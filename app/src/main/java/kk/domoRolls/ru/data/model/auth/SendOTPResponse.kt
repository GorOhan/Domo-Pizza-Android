package kk.domoRolls.ru.data.model.auth

import com.google.gson.annotations.SerializedName

data class SendOTPResponse(
    @SerializedName("request_id")
    val requestId: String,
)