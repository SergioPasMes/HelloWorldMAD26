package es.upm.etsisi.mad.helloworld

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity


/**
 * Common code for activities which interact with the device's location.
 *
 * Manages requesting location permissions and handling location updates.
 */
class LocationActivityHelper {
    private val TAG = this::class.simpleName

    /**
     * Contains the device's last known location
     */
    val lastLocation
        get() = locationManager.lastLocation

    private val locationManager: UserLocationManager

    constructor(activity: ComponentActivity) {
        locationManager = UserLocationManager(activity)

        PermissionManager()
            .permissions(UserLocationManager.REQUIRED_LOCATION_PERMISSIONS.asIterable())
            .onPermissionDenied { showPermissionErrorMessage(activity) }
            .onPermissionGranted { locationManager.start(activity) }
            .request(activity)
    }

    /**
     * Configures a function to be called when the location is updated.
     */
    fun onLocationUpdated(callback: (Location) -> Unit): LocationActivityHelper {
        locationManager.onLocationUpdated(callback)
        return this
    }

    /**
     * Notifies the user that location permissions are required.
     */
    private fun showPermissionErrorMessage(context: Context) {
        Log.w(TAG, "Location permissions denied")
        Toast.makeText(
            context,
            context.getString(R.string.location_activity_helper_permission_warning),
            Toast.LENGTH_LONG
        ).show()
    }
}