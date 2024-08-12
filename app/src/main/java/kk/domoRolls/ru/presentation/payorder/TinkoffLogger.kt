package kk.domoRolls.ru.presentation.payorder

import android.util.Log
import ru.tinkoff.acquiring.sdk.loggers.Logger


class TinkoffLogger(val onSuccess: (String) -> Unit) : Logger {
    override fun log(message: CharSequence) {
        onSuccess(message.toString())
    }

    override fun log(e: Throwable) {
        Log.i("TINKOFF", e.localizedMessage.toString())

    }
}
