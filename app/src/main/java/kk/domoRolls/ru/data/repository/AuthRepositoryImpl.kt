package kk.domoRolls.ru.data.repository

import kk.domoRolls.ru.data.api.AuthApi
import kk.domoRolls.ru.data.model.auth.SendOTPRequest
import kk.domoRolls.ru.data.model.auth.TokenRequest
import kk.domoRolls.ru.domain.repository.AuthRepository
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authApi: AuthApi,
) : AuthRepository {
    override fun getToken(tokenRequest: TokenRequest) = emitFlow {
        val token = authApi.getToken(tokenRequest)
        return@emitFlow token
    }

    override fun sendOTP(sendOTPRequest: SendOTPRequest, token: String) = emitFlow {
        val otp = authApi.sendOTP(sendOTPRequest, token = token)
        return@emitFlow otp
    }

}

fun <T> emitFlow(action: suspend () -> T) = flow { emit(action.invoke()) }
