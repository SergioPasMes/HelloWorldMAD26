package es.upm.etsisi.mad.helloworld.activities

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import es.upm.etsisi.mad.helloworld.activities.OpenStreetMapActivity
import es.upm.etsisi.mad.helloworld.activities.ProfileActivity
import es.upm.etsisi.mad.helloworld.data.ProfilePreferences
import es.upm.etsisi.mad.helloworld.R
import es.upm.etsisi.mad.helloworld.data.Mission
import es.upm.etsisi.mad.helloworld.data.ProfilePicture
import es.upm.etsisi.mad.helloworld.data.ProfilePictureManager
import es.upm.etsisi.mad.helloworld.helpers.AppActivityState
import es.upm.etsisi.mad.helloworld.helpers.LocationActivityHelper
import es.upm.etsisi.mad.helloworld.helpers.MissionManager
import es.upm.etsisi.mad.helloworld.helpers.NavigationActivityHelper
import es.upm.etsisi.mad.helloworld.helpers.PlantManager
import es.upm.etsisi.mad.helloworld.helpers.UpdateManager
import es.upm.etsisi.mad.helloworld.helpers.unbundleAppState
import kotlin.text.format

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
            .onMissionGenerated { showMission(it) }
            .onMissionCompleted { manager, mission -> manager.completeMission(this, mission) }
        locationManager = LocationActivityHelper(this)
            .onLocationUpdated { appState.location = it; missionManager.onLocationChanged(it, this, appState) }

        val welcomeTextWidget = findViewById<TextView>(R.id.mainTextView)
        val welcomeTextString = welcomeTextWidget.text.toString().format(
            ProfilePreferences.getAlias(
                this
            )
        )
        welcomeTextWidget.text = welcomeTextString

        val plantImageView = findViewById<ImageView>(R.id.currentPlantImageView)

        val currentPlantUrl = ProfilePreferences.getPlant(this)?.picture

        if (!currentPlantUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(currentPlantUrl)
                .placeholder(R.drawable.profile_icon)
                .error(R.drawable.profile_icon)
                .into(plantImageView)
        }

        PlantManager.checkPlantRoutine(this)

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
    private fun showMission(mission: Mission) {
        val missionText: TextView = findViewById(R.id.missionObjective)
        val formattedLatitude = String.format("%.4f", mission.latitude)
        val formattedLongitude = String.format("%.4f", mission.longitude)
        missionText.text = getString(R.string.mission_destination, mission.locationName)
        Log.i(TAG, "New Mission Set: ${formattedLatitude}, ${formattedLongitude}")
    }
}