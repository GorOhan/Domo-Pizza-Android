package kk.domoRolls.ru.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kk.domoRolls.ru.data.api.AuthApi
import kk.domoRolls.ru.data.api.ServiceApi
import kk.domoRolls.ru.data.repository.AuthRepositoryImpl
import kk.domoRolls.ru.data.repository.ServiceRepositoryImpl
import kk.domoRolls.ru.domain.repository.AuthRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideAuthRepository(authApi: AuthApi): AuthRepository {
        return AuthRepositoryImpl(authApi)
    }

    @Singleton
    @Provides
    fun provideServiceRepository(serviceApi: ServiceApi): ServiceRepository {
        return ServiceRepositoryImpl(serviceApi)
    }
}