package es.upm.etsisi.mad.helloworld

import android.Manifest
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(),
    LocationListener,
    NavigationView.OnNavigationItemSelectedListener {
    private val TAG = this::class.toString()
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var latestLocation: Location? = null

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

        val buttonNext = findViewById<Button>(R.id.button_main_to_profile)
        buttonNext.setOnClickListener {
            Log.i(TAG, "Transitioning to ProfileActivity")
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val buttonOsm: Button = findViewById(R.id.osmButton)
        buttonOsm.setOnClickListener {
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

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        //Check for location permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), locationPermissionCode)
        } else {
            //If the permissions are granted, the location will change every 5000 ms (or 5s) or when the user moves 5 meters
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
                }

            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val textView : TextView = findViewById(R.id.mainTextView)
        textView.text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
        this.latestLocation = location;
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