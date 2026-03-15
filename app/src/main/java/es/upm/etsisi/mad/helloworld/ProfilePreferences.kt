package es.upm.etsisi.mad.helloworld

import android.content.Context
import android.content.SharedPreferences

object ProfilePreferences {

    const val USERNAME_MIN_LENGTH = 3
    const val USERNAME_MAX_LENGTH = 16

    const val AGE_MIN = 16
    const val AGE_MAX = 100
    const val DEFAULT_PFP = "pfp_0.png"

    private const val fileName = "UserProfile"
    private const val keyAlias = "alias"
    private const val keyAge = "age"
    private const val keyPicturePath = "profile_picture"

    private const val keyToken = "user_token"


    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    fun getAlias(context: Context): String {
        return getPreferences(context).getString(keyAlias, "") ?: ""
    }

    fun getAge(context: Context): Int {
        return getPreferences(context).getInt(keyAge, 0)
    }

    fun getProfilePic(context: Context): String {
        return getPreferences(context).getString(keyPicturePath, "") ?: ""
    }

    fun getToken(context: Context): String {
        return getPreferences(context).getString(keyToken, "") ?: ""
    }

    fun setAlias(context: Context, alias: String) {
        getPreferences(context).edit().putString(keyAlias, alias).apply()
    }

    fun setAge(context: Context, age: Int) {
        getPreferences(context).edit().putInt(keyAge, age).apply()
    }

    fun setProfilePic(context: Context, path: String) {
        getPreferences(context).edit().putString(keyPicturePath, path).apply()
    }

    fun setToken(context: Context, token: String) {
        getPreferences(context).edit().putString(keyToken, token).apply()
    }
}