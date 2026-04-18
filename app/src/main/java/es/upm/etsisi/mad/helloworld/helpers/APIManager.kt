package es.upm.etsisi.mad.helloworld.helpers

import android.content.Context
import android.util.Log
import android.widget.Toast
import es.upm.etsisi.mad.helloworld.data.ProfilePreferences
import es.upm.etsisi.mad.helloworld.api.RetrofitAPI
import es.upm.etsisi.mad.helloworld.api.RetrofitInstance
import es.upm.etsisi.mad.helloworld.api.RetrofitInstance.parseError
import es.upm.etsisi.mad.helloworld.data.Mission
import es.upm.etsisi.mad.helloworld.data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class APIManager {

    fun postUserData(context: Context, user: User, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val retrofitAPI = RetrofitInstance.getRetrofitInstance().create(RetrofitAPI::class.java)
        val call = retrofitAPI.createPost(user)
        val TAG = "APIPost"

        call?.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                val code = response.code() // Get the code returned by the API
                if (response.isSuccessful) {
                    val token = response.body()?.userToken
                    if(token != null) {
                        ProfilePreferences.setToken(context, token)
                        Log.i(TAG, "$code: User data posted successfully, token saved: $token")
                        onSuccess()
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    Log.e(TAG, "Status Code: $code | Error: $errorJson")
                    onError("Error $code: $errorJson")
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Log.e(TAG, "Connection Failure", t)
                onError("Connection failed: ${t.message}")
            }
        })
    }

    fun getMission(
        context: Context, mainNecessity: String, latitude: Double, longitude: Double,
        onSuccess: (Mission) -> Unit, onError: (String?) -> Unit,
    ) {
        val TAG = "APIMission"
        val token = ProfilePreferences.getToken(context)
        val api = RetrofitInstance.getRetrofitInstance().create(RetrofitAPI::class.java)

        api.getMission(token, mainNecessity, latitude, longitude)
            .enqueue(object: Callback<Mission> {
                override fun onResponse(call: Call<Mission>, response: Response<Mission>) {
                    if(!response.isSuccessful || response.body() == null) {
                        val error = parseError(response)
                        Log.e(TAG, "Error fetching mission: $error")
                        onError(error)
                        return
                    }

                    val mission = response.body()
                    if(
                        mission == null ||
                        mission.satisfiesNecessities == null ||
                        mission.locationName == null ||
                        mission.latitude == null ||
                        mission.longitude == null
                        )
                    {
                        onError(null)
                        return
                    }
                    onSuccess(mission)
                }


                override fun onFailure(
                    call: Call<Mission?>?,
                    error: Throwable?
                ) {
                    Log.e(TAG, "Failure when fetching mission: $error")
                    onError(error?.toString())
                }
            }
        )
    }
}