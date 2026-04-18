package es.upm.etsisi.mad.helloworld.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import es.upm.etsisi.mad.helloworld.data.ApiError
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://mad.gabrielg.es/api/v1/"

    private var retrofit: Retrofit? = null

    fun getRetrofitInstance(): Retrofit {
        val gson = GsonBuilder()
            .create()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }

    fun parseError(response: Response<*>): String {
        return try {
            val errorBodyString = response.errorBody()?.string()
            val apiError = Gson().fromJson(errorBodyString, ApiError::class.java)

            apiError.error
        } catch (e: Exception) {
            "Error ${response.code()}: ${response.message()}"
        }
    }
}