package es.upm.etsisi.mad.helloworld

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(@Expose var alias: String, @Expose var age: Int?, @Expose var profile_picture: String) {

    /**
     * Using Transient to indicate this field is not part of the POST request body, but will be used from the response.
     */
    @SerializedName("user_token")
    @Expose(serialize = false, deserialize = true)
    val user_token: String? = null
}
