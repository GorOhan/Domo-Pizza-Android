package kk.domoRolls.ru

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import ru.tinkoff.acquiring.sdk.AcquiringSdk
import ru.tinkoff.acquiring.sdk.TinkoffAcquiring
import ru.tinkoff.acquiring.sdk.loggers.Logger
import ru.tinkoff.acquiring.sdk.utils.EnvironmentMode
import ru.tinkoff.acquiring.sdk.utils.SampleAcquiringTokenGenerator

@HiltAndroidApp
class DomoApplication : Application() {

    override fun onCreate() {

        val tinkoffAcquiring = TinkoffAcquiring(applicationContext, "1713117993164", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv5yse9ka3ZQE0feuGtemYv3IqOlLck8zHUM7lTr0za6lXTszRSXfUO7jMb+L5C7e2QNFs+7sIX2OQJ6a+HG8kr+jwJ4tS3cVsWtd9NXpsU40PE4MeNr5RqiNXjcDxA+L4OsEm/BlyFOEOh2epGyYUd5/iO3OiQFRNicomT2saQYAeqIwuELPs1XpLk9HLx5qPbm8fRrQhjeUD5TLO8b+4yCnObe8vy/BMUwBfq+ieWADIjwWCMp2KTpMGLz48qnaD9kdrYJ0iyHqzb2mkDhdIzkim24A3lWoYitJCBrrB2xM05sm9+OdCI1f7nPNJbl5URHobSwR94IRGT7CJcUjvwIDAQAB")


        tinkoffAcquiring.initSbpPaymentSession()
        tinkoffAcquiring.initTinkoffPayPaymentSession()
        tinkoffAcquiring.initMirPayPaymentSession()


        AcquiringSdk.logger = object :Logger{
            override fun log(message: CharSequence) {
                Log.i("TINKOFF",message.toString())
            }

            override fun log(e: Throwable) {
                Log.i("TINKOFF",e.message.toString())
            }

        }


        MapKitFactory.setApiKey("efdec1a2-97d3-4cd3-9a3a-78330b85ce8b")

        FirebaseApp.initializeApp(this)

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 36
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        super.onCreate()
    }
}