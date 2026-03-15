package es.upm.etsisi.mad.helloworld

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface RetrofitAPI {

    @POST("user")
    fun createPost(@Body dataModal: User?): Call<User?>?
}