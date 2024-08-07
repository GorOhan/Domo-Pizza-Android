package kk.domoRolls.ru.data.api

import kk.domoRolls.ru.data.model.auth.SendOTPRequest
import kk.domoRolls.ru.data.model.auth.SendOTPResponse
import kk.domoRolls.ru.data.model.auth.TokenRequest
import kk.domoRolls.ru.data.model.auth.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("account/token")
    suspend fun getToken(
        @Body tokenRequest: TokenRequest,
    ): TokenResponse

    @POST("/sms/send")
    suspend fun sendOTP(
        @Body sendOTPRequest: SendOTPRequest,
        @Header("Authorization") token: String,
        ): SendOTPResponse
}