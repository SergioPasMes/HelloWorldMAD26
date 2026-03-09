package es.upm.etsisi.mad.helloworld

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat


/**
 * Helper class to simplify permission checks and requests.
 */
class PermissionManager {
    private val TAG = this::class.simpleName

    private val permissionList: MutableList<String> = mutableListOf()
    private var onPermissionGrantedCallback: (() -> Unit)? = null
    private var onPermissionDeniedCallback: (() -> Unit)? = null

    /**
     * Adds a required permission to the manager.
     */
    fun permission(permission: String): PermissionManager {
        permissionList.add(permission)
        return this
    }

    /**
     * Adds multiple required permissions to the manager.
     */
    fun permissions(permissions: Iterable<String>): PermissionManager {
        permissionList.addAll(permissions)
        return this
    }

    /**
     * Configures a function to be called when all requested permissions are granted by the user.
     */
    fun onPermissionGranted(callback: () -> Unit): PermissionManager {
        onPermissionGrantedCallback = callback
        return this
    }

    /**
     * Configures a function to be called if any requested permission is denied by the user.
     */
    fun onPermissionDenied(callback: () -> Unit): PermissionManager {
        onPermissionDeniedCallback = callback
        return this
    }

    /**
     * Checks and returns whether all configured permissions have been granted.
     */
    fun granted(context: Context): Boolean {
        return permissionList.map {
            ActivityCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }.all { it }
    }

    /**
     * Requests the user to grant the configured permissions. **May only be called during activity
     * initialization.**
     *
     * If granted, calls the `onPermissionGranted` callback.
     * Otherwise, calls the `onPermissionDenied` callback.
     *
     * If the permissions have previously been granted, calls the `onPermissionGranted` callback.
     */
    fun request(activity: ComponentActivity) {

        // Only request permissions if they have not been granted.
        if(granted(activity)) {
            Log.i(TAG, "Skipping permission request for: [$permissionList], already granted")
            onPermissionGrantedCallback?.invoke()
            return
        }

        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                grantedMap ->
            val allGranted = grantedMap.values.all { it }
            if(allGranted) {
                Log.i(TAG, "Permissions $permissionList granted")
                onPermissionGrantedCallback?.invoke()
            } else {
                Log.w(TAG, "User denied $permissionList permissions")
                onPermissionDeniedCallback?.invoke()
            }
        }.launch(permissionList.toTypedArray())
    }
}