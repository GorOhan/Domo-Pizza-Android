package kk.domoRolls.ru.domain.repository

import kk.domoRolls.ru.data.model.auth.SendOTPRequest
import kk.domoRolls.ru.data.model.auth.SendOTPResponse
import kk.domoRolls.ru.data.model.auth.TokenRequest
import kk.domoRolls.ru.data.model.auth.TokenResponse
import kotlinx.coroutines.flow.Flow
interface AuthRepository {
    fun getToken(tokenRequest: TokenRequest): Flow<TokenResponse>
    fun sendOTP(sendOTPRequest: SendOTPRequest,token: String): Flow<SendOTPResponse>
}