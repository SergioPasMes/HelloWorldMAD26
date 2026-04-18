package es.upm.etsisi.mad.helloworld.data

import android.location.Location
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * API response for a mission.
 */
@Parcelize
data class Mission(
    @SerializedName("name")
    val locationName: String,
    @SerializedName("satisfies_necessities")
    val satisfiesNecessities: List<String>,
    val latitude: Double,
    val longitude: Double,
) : Parcelable {
    val location: Location
        get() {
            val location = Location("mission")
            location.latitude = latitude
            location.longitude = longitude

            return location
        }
}