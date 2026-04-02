package es.upm.etsisi.mad.helloworld.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

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
    private const val keyScore = "score"

    private const val keyLastPlantChoosen = "last_plant_chosen"
    private const val keyCurrentPlantImage = "current_plant_image"


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

    fun getScore(context: Context): Int {
        return getPreferences(context).getInt(keyScore, 0)
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

    fun setScore(context: Context, score: Int) {
        getPreferences(context).edit().putInt(keyScore, score).apply()
    }

    fun setToken(context: Context, token: String) {
        getPreferences(context).edit().putString(keyToken, token).apply()
    }

    fun getLastPlantTime(context: Context): Long {
        // Default value of 0L means that the user has never chosen a plant before.
        return getPreferences(context).getLong(keyLastPlantChoosen, 0L)
    }

    fun setLastPlantTime(context: Context, timeInMillis: Long) {
        getPreferences(context).edit { putLong(keyLastPlantChoosen, timeInMillis) }
    }

    fun setPlantImage(context: Context, imageUrl: String) {
        getPreferences(context).edit {
            putString(keyCurrentPlantImage, imageUrl)
        }
    }

    fun getPlantImage(context: Context): String? {
        return getPreferences(context).getString(keyCurrentPlantImage, null)
    }
}