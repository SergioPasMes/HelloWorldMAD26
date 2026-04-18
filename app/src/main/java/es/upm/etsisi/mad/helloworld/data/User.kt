package es.upm.etsisi.mad.helloworld.data

import com.google.gson.annotations.SerializedName

data class User(
    var alias: String? = null,
    var age: Int? = null,
    @SerializedName("profile_picture")
    var profilePicture: String? = null,
    var score: Int? = null,
    @SerializedName("user_token")
    val userToken: String? = null
)