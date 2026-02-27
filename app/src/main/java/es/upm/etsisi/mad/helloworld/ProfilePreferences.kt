package es.upm.etsisi.mad.helloworld

import android.content.Context
import android.content.SharedPreferences

object ProfilePreferences {
    private const val fileName = "UserProfile"
    private const val keyAlias = "alias"
    private const val keyAge = "age"
    private const val keyPicturePath = "profile_picture"


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

    fun setAlias(context: Context, alias: String) {
        getPreferences(context).edit().putString(keyAlias, alias).apply()
    }

    fun setAge(context: Context, age: Int) {
        getPreferences(context).edit().putInt(keyAge, age).apply()
    }

    fun setProfilePic(context: Context, path: String) {
        getPreferences(context).edit().putString(keyPicturePath, path).apply()
    }
}