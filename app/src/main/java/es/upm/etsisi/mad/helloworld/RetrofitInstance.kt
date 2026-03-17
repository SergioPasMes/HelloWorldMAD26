package es.upm.etsisi.mad.helloworld

import com.google.gson.GsonBuilder
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
}