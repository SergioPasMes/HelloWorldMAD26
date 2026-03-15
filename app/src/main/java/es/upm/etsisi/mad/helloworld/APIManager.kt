package es.upm.etsisi.mad.helloworld

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Response

class APIManager {

    fun postUserData(context: Context, user: User, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val retrofitAPI = RetrofitInstance.getRetrofitInstance().create(RetrofitAPI::class.java)
        val call = retrofitAPI.createPost(user)
        val TAG = "APIPost"

        call?.enqueue(object : retrofit2.Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                val code = response.code() // Get the code returned by the API
                if (response.isSuccessful) {
                    val token = response.body()?.user_token
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
}