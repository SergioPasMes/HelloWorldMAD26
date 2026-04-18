package es.upm.etsisi.mad.helloworld.api

import es.upm.etsisi.mad.helloworld.data.Mission
import es.upm.etsisi.mad.helloworld.data.Plant
import es.upm.etsisi.mad.helloworld.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface RetrofitAPI {

    @POST("user")
    fun createPost(@Body dataModal: User?): Call<User?>?

    @GET("ranking")
    fun getUsers(@Header("User-Token") token: String): Call<List<User>>

    @PUT("user")
    fun updateUser(@Header("User-Token") token: String, @Body data: User): Call<User>

    @GET("plant")
    fun getPlants(@Header("User-Token") token: String): Call<List<Plant>>

    @GET("mission")
    fun getMission(
        @Header("User-Token") token: String,
        @Query("necessity") necessity: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double): Call<Mission>
}