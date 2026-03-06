package es.upm.etsisi.mad.helloworld

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView

/**
 * Common code for activities which share the common Navigation Drawer system.
 *
 * Manages the navigation drawer and maintaining state between activity transitions.
 */
class NavigationActivityHelper {
    private val TAG = this::class.simpleName

    private val activity: Activity
    private val state: AppActivityState

    constructor(activity: Activity, navViewId: Int, state: AppActivityState) {
        this.activity = activity
        this.state = state

        val navView = activity.findViewById<NavigationView>(navViewId)
        navView.setNavigationItemSelectedListener(::onNavigationItemSelected)
    }

    /**
     * Launches the specified Activity and finishes the current one.
     */
    fun transitionTo(destinationClass: Class<out AppCompatActivity>) {
        Log.i(TAG, "Transitioning to: ${destinationClass.simpleName}")
        val intent = Intent(activity,  destinationClass)
        intent.injectAppState(state)
        activity.startActivity(intent)
        activity.finish()
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        val destinationClass = when(item.itemId) {
            R.id.nav_home -> MainActivity::class.java
            R.id.nav_profile_activity -> ProfileActivity::class.java
            R.id.nav_open_street_map -> OpenStreetMapActivity::class.java
            else -> return false
        }

        transitionTo(destinationClass)
        return true
    }
}