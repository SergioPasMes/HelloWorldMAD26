package es.upm.etsisi.mad.helloworld.helpers

import android.content.Context
import android.util.Log
import es.upm.etsisi.mad.helloworld.data.ProfilePreferences
import es.upm.etsisi.mad.helloworld.api.RetrofitAPI
import es.upm.etsisi.mad.helloworld.api.RetrofitInstance
import es.upm.etsisi.mad.helloworld.api.RetrofitInstance.parseError
import es.upm.etsisi.mad.helloworld.data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UpdateManager {
    private const val TAG = "APIPost"


    fun updateScore(context: Context, newScore: Int) {
        val updateData = User(score = newScore)
        sendUpdate(context, updateData, "Score")
    }

    fun updateAlias(context: Context, newAlias: String) {
        val updateData = User(alias = newAlias)
        sendUpdate(context, updateData, "Alias")
    }

    fun updateAge(context: Context, newAge: Int) {
        val updateData = User(age = newAge)
        sendUpdate(context, updateData, "Age")
    }

    fun updateProfilePicture(context: Context, newPicturePath: String) {
        val updateData = User(profilePicture = newPicturePath)
        sendUpdate(context, updateData, "Profile Picture")
    }

    private fun sendUpdate(context: Context, updateData: User, fieldName: String) {
        val token = ProfilePreferences.getToken(context)
        val api = RetrofitInstance.getRetrofitInstance().create(RetrofitAPI::class.java)

        api.updateUser(token, updateData).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val successResponse = response.body()
                    Log.i(TAG, "$successResponse")
                } else {
                    val errorResponse = parseError(response)
                    Log.e(TAG, errorResponse)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e(TAG, "Connection failure while updating $fieldName: ${t.message}")
            }
        })
    }
}