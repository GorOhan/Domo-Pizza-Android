package kk.domoRolls.ru.data.prefs

import android.content.Context
import kk.domoRolls.ru.domain.model.User

interface DataStoreService {
    fun setUserData(user: User)
    fun getUserData(): User
}


class DataStoreServiceImpl(context: Context) : DataStoreService {

    private val sharedPrefs = context.getSharedPreferences("info", Context.MODE_PRIVATE)
    private val editor = sharedPrefs.edit()

    private val userIdKey = "userIdKey"
    private val userPhoneKey = "userPhoneKey"
    private val userNameKey = "userNameKey"


    private fun set(key: String, value: Any?) {
        when (value) {
            is Int -> editor.putInt(key, value).apply()
            is Long -> editor.putLong(key, value).apply()
            is Float -> editor.putFloat(key, value).apply()
            is String -> editor.putString(key, value).apply()
            is Boolean -> editor.putBoolean(key, value).apply()
            else -> throw UnsupportedOperationException("Not implemented type")
        }
    }

    private inline operator fun <reified T> get(
        key: String,
        defaultValue: Any? = null,
    ): T {
        return when (T::class) {
            Int::class -> sharedPrefs.getInt(key, 0) as T
            Long::class -> sharedPrefs.getLong(key, 0L) as T
            Float::class -> sharedPrefs.getFloat(key, 0F) as T
            String::class -> sharedPrefs.getString(key, defaultValue as String).orEmpty() as T
            Boolean::class -> sharedPrefs.getBoolean(key, false) as T
            else -> throw UnsupportedOperationException("Not implemented type")
        }
    }

    override fun setUserData(user: User) {
        set(userIdKey,user.id)
        set(userNameKey,user.name)
        set(userPhoneKey,user.phone)

    }

    override fun getUserData() = User(
        id = get(userIdKey, ""),
        name = get(userNameKey,""),
        phone = get(userPhoneKey,"")
    )

}
