package kk.domoRolls.ru.data.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kk.domoRolls.ru.data.api.AuthApi
import kk.domoRolls.ru.data.api.ServiceApi
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.data.repository.AuthRepositoryImpl
import kk.domoRolls.ru.data.repository.FirebaseConfigRepositoryImpl
import kk.domoRolls.ru.data.repository.ServiceRepositoryImpl
import kk.domoRolls.ru.domain.repository.AuthRepository
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
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
    fun provideServiceRepository(serviceApi: ServiceApi,firebaseRemoteConfig:FirebaseRemoteConfig): ServiceRepository {
        return ServiceRepositoryImpl(serviceApi,firebaseRemoteConfig)
    }

    @Singleton
    @Provides
    fun provideFirebaseConfigRepository(firebaseRemoteConfig:FirebaseRemoteConfig,dataStoreService: DataStoreService): FirebaseConfigRepository {
        return FirebaseConfigRepositoryImpl(firebaseRemoteConfig,dataStoreService)
    }
}