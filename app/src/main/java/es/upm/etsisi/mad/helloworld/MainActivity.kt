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

    private lateinit var missionManager: MissionManager

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
        missionManager = MissionManager()
            .onMissionGenerated { location ->
                showMission(location)
            }
            .onMissionCompleted {
                Toast.makeText(this, R.string.mission_completion, Toast.LENGTH_SHORT).show()
            }
        locationManager = LocationActivityHelper(this)
            .onLocationUpdated { appState.location = it; missionManager.onLocationChanged(it, appState) }

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
}