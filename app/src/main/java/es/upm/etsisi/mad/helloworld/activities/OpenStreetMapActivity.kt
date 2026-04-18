package es.upm.etsisi.mad.helloworld.activities

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.upm.etsisi.mad.helloworld.R
import es.upm.etsisi.mad.helloworld.helpers.AppActivityState
import es.upm.etsisi.mad.helloworld.helpers.LocationActivityHelper
import es.upm.etsisi.mad.helloworld.helpers.MissionManager
import es.upm.etsisi.mad.helloworld.helpers.NavigationActivityHelper
import es.upm.etsisi.mad.helloworld.helpers.unbundleAppState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class OpenStreetMapActivity : AppCompatActivity() {
    private val TAG = this::class.simpleName

    private lateinit var appState: AppActivityState

    private lateinit var locationManager: LocationActivityHelper
    private lateinit var navigationManager: NavigationActivityHelper

    private var userMarker: Marker? = null
    private lateinit var map: MapView

    private lateinit var missionManager: MissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Starting activity $TAG")

        enableEdgeToEdge()
        setContentView(R.layout.activity_open_street_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        appState = intent.unbundleAppState()
        missionManager = MissionManager()
            .onMissionCompleted {
                Toast.makeText(this, getString(R.string.mission_completion), Toast.LENGTH_LONG).show()
                appState.currentMission = null
                Log.i(TAG, "User reached destination. Mission cleared.")
                navigationManager.transitionTo(MainActivity::class.java) // When reached, we go back to MainActivity.
            }

        locationManager = LocationActivityHelper(this)
            .onLocationUpdated { appState.location = it; onUserMovement(it, appState) }
        navigationManager = NavigationActivityHelper(this, R.id.nav_view, appState)
            .onBackPressed { navigationManager.transitionTo(MainActivity::class.java) }

        // Configure the user agent before loading the configuration
        Configuration.getInstance().userAgentValue = "es.upm.btb.mad26SG"
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        val location = appState.location
        val startPoint = if(location != null) {
            Log.d(TAG, "onCreate: Location[${location.altitude}][${location.latitude}][${location.longitude}]")
            GeoPoint(location.latitude, location.longitude)
        } else {
            Log.d(TAG, "onCreate: Location is null, using default coordinates")
            GeoPoint(40.389683644051864, -3.627825356970311)
        }

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(15.0)
        map.controller.setCenter(startPoint)

        appState.currentMission?.let { missionDestination ->
            val missionPoint = GeoPoint(missionDestination.latitude, missionDestination.longitude)
            val missionMarker = Marker(map)
            missionMarker.position = missionPoint
            missionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            // We will use a star icon to mark the destination
            missionMarker.icon = ContextCompat.getDrawable(this, android.R.drawable.btn_star_big_on) as BitmapDrawable
            missionMarker.title = getString(R.string.mission_marker)
            map.overlays.add(missionMarker)
        }
    }

    fun addGymkhanaMarkers(map: MapView, coords: List<GeoPoint>, names: List<String>, context: Context) {
        for (i in coords.indices) {
            val marker = Marker(map)
            marker.position = coords[i]
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass) as BitmapDrawable
            marker.title = names[i]
            map.overlays.add(marker)
        }
    }

    fun addRouteMarkers(map: MapView, coords: List<GeoPoint>, names: List<String>, context: Context) {
        val polyline = Polyline(map)
        polyline.setPoints(coords)

        for (i in coords.indices) {
            val marker = Marker(map)
            marker.position = coords[i]
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass) as BitmapDrawable
            marker.title = names[i]
            map.overlays.add(marker)
        }

        map.overlays.add(polyline)
    }

    fun onUserMovement(location: Location, appState: AppActivityState) {
        // We will use our plant icon to indicate the user movement position
        val userPoint = GeoPoint(location.latitude, location.longitude)
        if (userMarker == null) {
            userMarker = Marker(map)
            userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            userMarker?.icon = ContextCompat.getDrawable(this, R.drawable.icon_round)
            map.overlays.add(userMarker)
        }
        // When the user moves, we update the position of the marker and refresh the map.
        userMarker?.position = userPoint
        map.invalidate()

        // Destination Check
        missionManager.onLocationChanged(location, appState)
    }
}