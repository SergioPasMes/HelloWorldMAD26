package es.upm.etsisi.mad.helloworld

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface RetrofitAPI {

    @POST("user")
    fun createPost(@Body dataModal: User?): Call<User?>?

    @GET("ranking")
    fun getUsers(@Header("User-Token") token: String): Call<List<User>>
}