package es.upm.etsisi.mad.helloworld.data

import com.google.gson.annotations.SerializedName

data class User(
    var alias: String? = null,
    var age: Int? = null,
    var profile_picture: String? = null,
    var score: Int? = null,
    @SerializedName("user_token")
    val user_token: String? = null
)