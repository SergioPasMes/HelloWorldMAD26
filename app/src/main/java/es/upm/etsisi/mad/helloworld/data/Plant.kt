package es.upm.etsisi.mad.helloworld.data

import com.google.gson.annotations.SerializedName

data class Plant(
    val name: String? = null,
    val picture: String? = null,
    @SerializedName("main_necessity")
    val mainNecessity: String? = null
)
