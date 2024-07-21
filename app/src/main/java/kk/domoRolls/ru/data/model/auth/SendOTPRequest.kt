package kk.domoRolls.ru.data.model.auth

import com.google.gson.annotations.SerializedName

data class SendOTPRequest(
    @SerializedName("user")
    val user: String = "KonstantinKiski",
    @SerializedName("pass")
    val pass: String = "xadrat-2cyxjo-nefFix",
    @SerializedName("to")
    val to: String,
    @SerializedName("txt")
    val txt: String,
    @SerializedName("from")
    val from: String = "DomoRolls",
)