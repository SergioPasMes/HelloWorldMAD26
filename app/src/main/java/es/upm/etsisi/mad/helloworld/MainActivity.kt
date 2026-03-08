package es.upm.etsisi.mad.helloworld

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import es.upm.etsisi.mad.helloworld.ProfilePreferences.getAlias
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity(),
    LocationListener,
    NavigationView.OnNavigationItemSelectedListener {
    private val TAG = this::class.toString()
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var latestLocation: Location? = null

    private var currentMission: Location? = null

    private val missionAcceptance = 50f // Complete the mission if within 50 meters

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val welcomeTextWidget = findViewById<TextView>(R.id.mainTextView)
        val welcomeTextString = welcomeTextWidget.text.toString().format(getAlias(this))
        welcomeTextWidget.text = welcomeTextString

        val buttonNext = findViewById<Button>(R.id.button_main_to_profile)
        buttonNext.setOnClickListener {
            Log.i(TAG, "Transitioning to ProfileActivity")
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val buttonOsm: Button = findViewById(R.id.osmButton)
        buttonOsm.setOnClickListener {
            if (latestLocation != null && currentMission != null) {
                val intent = Intent(this, OpenStreetMapActivity::class.java)
                val bundle = Bundle()
                // We send the user's location and the mission destination so they can be displayed on the map.
                bundle.putParcelable("location", latestLocation)    // Blue dot (User)
                bundle.putParcelable("destination", currentMission) // Star marker (Goal)
                intent.putExtra("locationBundle", bundle)
                startActivity(intent)
            }
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        //Check for location permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), locationPermissionCode)
        } else {
            //If the permissions are granted, the location will change every 5000 ms (or 5s) or when the user moves 5 meters
            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 5000, 5f, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 5000, 5f, this)
                }

            }
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onLocationChanged(location: Location) {
        this.latestLocation = location
        val mission: TextView = findViewById(R.id.missionObjective)

        // Generate a mission if one doesn't exist (First time or just completed)
        if (currentMission == null) {
            currentMission = generateRandomDestination(location, 5000.0) // Mission Destination within 5km radius
            val formattedLatitude = String.format("%.4f", currentMission?.latitude)
            val formattedLongitude = String.format("%.4f", currentMission?.longitude)
            mission.text = getString(R.string.mission_destination, formattedLatitude, formattedLongitude)
            Log.i(TAG, "New Mission Set: ${formattedLatitude}, $formattedLongitude")
        }

        // Calculate distance to the mission
        val distanceToGoal = location.distanceTo(currentMission!!) // We can use !! here because we just generated it if it was null

        // Check for arrival
        if (distanceToGoal <= missionAcceptance) {
            currentMission = null // Reset so the next update generates a new one
            Toast.makeText(this, getString(R.string.mission_completion), Toast.LENGTH_SHORT).show()
        }
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Already in home
            }

            R.id.nav_profile_activity -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_open_street_map -> {
                if (latestLocation != null) {
                    val intent = Intent(this, OpenStreetMapActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("location", latestLocation)
                    intent.putExtra("locationBundle", bundle)
                    startActivity(intent)
                } else {
                    Log.e(TAG, "Location is not yet set")
                }
            }
        }
        drawerLayout.closeDrawers()
        return true
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

}