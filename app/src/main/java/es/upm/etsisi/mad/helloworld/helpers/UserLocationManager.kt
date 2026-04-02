package es.upm.etsisi.mad.helloworld.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService

/**
 * Helper class to access the device's location.
 */
class UserLocationManager {
    private val TAG = this::class.simpleName

    /**
     * Which location provider to use.
     */
    enum class ProviderType(val nativeValue: String) {
        // Use nearby Wi-Fi networks.
        NETWORK(LocationManager.NETWORK_PROVIDER),
        // Use the device's GPS signal.
        GPS(LocationManager.GPS_PROVIDER),
        // Use both methods.
        @RequiresApi(Build.VERSION_CODES.S)
        FUSED(LocationManager.FUSED_PROVIDER)
    }

    companion object {
        /**
         * Mandatory permissions for location access.
         */
        val REQUIRED_LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    /**
     * Contains the device's last known location.
     */
    var lastLocation: Location? = null
        private set

    /**
     * Specifies how location should be determined.
     */
    private var locationProvider: ProviderType = ProviderType.GPS
    /**
     *  How often should the location be updated.
     */
    private var updateInterval: Long = 1000
    /**
     * Triggers a location update if the user moves by this amount.
     */
    private var updateDistance: Float = 5f
    /**
     * Called when the location is updated.
     */
    private var onLocationUpdatedCallback: ((Location) -> Unit)? = null
    private val locationManager: LocationManager


    /**
     * Create a new `LocationHelper`, automatically requesting any required permissions.
     *
     * @param context Parent context. Used for accessing system services.
     */
    constructor(context: Context) {
        locationManager = context.getSystemService<LocationManager>()!!
    }

    /**
     * Configures how the location should be determined.
     */
    fun locationProvider(provider: ProviderType): UserLocationManager {
        locationProvider = provider
        return this
    }

    /**
     * Specifies the frequency of location updates.
     */
    fun updateInterval(interval: Long): UserLocationManager {
        updateInterval = interval
        return this
    }

    /**
     * Triggers a location update if the user moves by this amount.
     */
    fun updateDistance(distance: Float): UserLocationManager {
        updateDistance = distance
        return this
    }

    /**
     * Configures a function to be called when the location is updated.
     */
    fun onLocationUpdated(callback: ((Location) -> Unit)?): UserLocationManager {
        onLocationUpdatedCallback = callback
        return this
    }

    /**
     * Store the location listener as a variable to support cancelling updates.
     */
    private var locationListener = LocationListener { location ->
        lastLocation = location
        onLocationUpdatedCallback?.invoke(location)
    }

    /**
     * Starts location updates with the configured parameters.
     *
     * Requires location permissions.
     */
    @SuppressLint("MissingPermission") // Checked by `hasLocationPermissions`.
    fun start(context: Context) {
        if(!hasLocationPermissions(context)) {
            Log.e(TAG, "Error starting location update: Permission denied")
            return
        }

        locationManager.requestLocationUpdates(
            locationProvider.nativeValue,
            updateInterval,
            updateDistance,
            locationListener
        )

        Log.i(TAG, "Location updates started")
    }

    /**
     * Stops location updates.
     */
    fun stop() {
        locationManager.removeUpdates(locationListener)

        Log.i(TAG, "Location updates stopped")
    }

    /**
     * Checks whether the given context has location permissions.
     */
    private fun hasLocationPermissions(context: Context): Boolean {
        return PermissionManager()
            .permissions(REQUIRED_LOCATION_PERMISSIONS.asIterable())
            .granted(context)
    }
}