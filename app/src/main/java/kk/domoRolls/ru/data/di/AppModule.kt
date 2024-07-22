package kk.domoRolls.ru.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.data.prefs.DataStoreServiceImpl

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDataStoreService(@ApplicationContext context: Context): DataStoreService {
        return DataStoreServiceImpl(context)
    }
}