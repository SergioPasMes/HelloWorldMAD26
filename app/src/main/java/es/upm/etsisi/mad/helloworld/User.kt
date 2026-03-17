package es.upm.etsisi.mad.helloworld

import com.google.gson.annotations.SerializedName

data class User(
    var alias: String,
    var age: Int?,
    var profile_picture: String,
    var score: Int? = null,
    @SerializedName("user_token")
    val user_token: String? = null
)
