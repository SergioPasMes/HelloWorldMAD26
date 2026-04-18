package es.upm.etsisi.mad.helloworld.helpers

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import es.upm.etsisi.mad.helloworld.helpers.PermissionManager
import es.upm.etsisi.mad.helloworld.R
import es.upm.etsisi.mad.helloworld.helpers.UserLocationManager

/**
 * Common code for activities which interact with the device's location.
 *
 * Manages requesting location permissions and handling location updates.
 */
class LocationActivityHelper: DefaultLifecycleObserver {
    private val TAG = this::class.simpleName

    /**
     * Contains the device's last known location
     */
    val lastLocation
        get() = locationManager.lastLocation

    private val activity: ComponentActivity
    private val locationManager: UserLocationManager

    constructor(activity: ComponentActivity) {
        this.activity = activity
        activity.lifecycle.addObserver(this)

        locationManager = UserLocationManager(activity)
            .locationProvider(UserLocationManager.ProviderType.FUSED)

        PermissionManager()
            .permissions(UserLocationManager.Companion.REQUIRED_LOCATION_PERMISSIONS.asIterable())
            .onPermissionDenied { showPermissionErrorMessage() }
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
    private fun showPermissionErrorMessage() {
        Log.w(TAG, "Location permissions denied")
        Toast.makeText(
            activity,
            R.string.location_activity_helper_permission_warning,
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Automatically stop location updates when the activity calls `finish()`.
     */
    override fun onDestroy(owner: LifecycleOwner) {
        locationManager.stop()
    }
}