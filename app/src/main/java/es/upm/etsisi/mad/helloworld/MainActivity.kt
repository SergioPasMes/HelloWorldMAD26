package es.upm.etsisi.mad.helloworld

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.upm.etsisi.mad.helloworld.ProfilePreferences.getAlias
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.simpleName

    private val missionAcceptance = 50f // Complete the mission if within 50 meters

    private lateinit var appState: AppActivityState

    private lateinit var navigationManager: NavigationActivityHelper
    private lateinit var locationManager: LocationActivityHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        appState = intent.unbundleAppState()
        navigationManager = NavigationActivityHelper(this, R.id.nav_view, appState)
        locationManager = LocationActivityHelper(this)
            .onLocationUpdated { appState.location = it; onLocationChanged(it) }

        val welcomeTextWidget = findViewById<TextView>(R.id.mainTextView)
        val welcomeTextString = welcomeTextWidget.text.toString().format(getAlias(this))
        welcomeTextWidget.text = welcomeTextString

        val buttonNext = findViewById<Button>(R.id.button_main_to_profile)
        buttonNext.setOnClickListener {
            navigationManager.transitionTo(ProfileActivity::class.java)
        }

        val buttonOsm: Button = findViewById(R.id.osmButton)
        buttonOsm.setOnClickListener {
            navigationManager.transitionTo(OpenStreetMapActivity::class.java)
        }

        appState.currentMission?.let { showMission(it) }
    }

    fun onLocationChanged(location: Location) {
        // Generate a mission if one doesn't exist (First time or just completed)
        if (appState.currentMission == null) {
            val currentMission = generateRandomDestination(location, 5000.0) // Mission Destination within 5km radius
            appState.currentMission = currentMission
            showMission(currentMission)
        }

        // Calculate distance to the mission
        val distanceToGoal = location.distanceTo(appState.currentMission!!) // We can use !! here because we just generated it if it was null

        // Check for arrival
        if (distanceToGoal <= missionAcceptance) {
            appState.currentMission = null // Reset so the next update generates a new one
            Toast.makeText(this, R.string.mission_completion, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Updates the activity to show the current mission's location.
     */
    @SuppressLint("DefaultLocale")
    private fun showMission(missionLocation: Location) {
        val mission: TextView = findViewById(R.id.missionObjective)
        val formattedLatitude = String.format("%.4f", missionLocation.latitude)
        val formattedLongitude = String.format("%.4f", missionLocation.longitude)
        mission.text = getString(R.string.mission_destination, formattedLatitude, formattedLongitude)
        Log.i(TAG, "New Mission Set: ${formattedLatitude}, ${formattedLongitude}")
    }

    private fun generateRandomDestination(center: Location, maxRadiusMeters: Double): Location {
        val radiusInDegrees = maxRadiusMeters / 111320.0 // 1 degree of latitude is roughly 111,320 meters

        // Use sqrt(random) to ensure uniform distribution inside the circle
        val radius = radiusInDegrees * sqrt(Random.nextDouble())
        val theta = 2 * PI * Random.nextDouble()

        val dest = Location(center)
        dest.latitude = center.latitude + (radius * cos(theta))
        dest.longitude = center.longitude + (radius * sin(theta) /
                cos(center.latitude * PI / 180.0))
        return dest
    }
}