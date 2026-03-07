package es.upm.etsisi.mad.helloworld

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import org.osmdroid.views.overlay.Polyline
import android.widget.Toast

class OpenStreetMapActivity : AppCompatActivity() {
    private val TAG = this::class.toString()

    private lateinit var appState: AppActivityState

    private lateinit var locationManager: LocationActivityHelper
    private lateinit var navigationManager: NavigationActivityHelper

    private var userMarker: Marker? = null
    private lateinit var map: MapView

    val gymkhanaCoords = listOf(
        GeoPoint(40.38779608214728, -3.627687914352839), // Tennis
        GeoPoint(40.38788595319803, -3.627048250272035), // Futsal outdoors
        GeoPoint(40.3887315224542, -3.628643539758645), // Fashion and design
        GeoPoint(40.38926842612264, -3.630067893975619), // Topos
        GeoPoint(40.38956358584258, -3.629046081389352), // Teleco
        GeoPoint(40.38992125672989, -3.6281366497769714), // ETSISI
        GeoPoint(40.39037466191718, -3.6270256763598447), // Library
        GeoPoint(40.389855884803005, -3.626782180787362) // CITSEM
    )

    val gymkhanaNames = listOf(
        "Tennis",
        "Futsal outdoors",
        "Fashion and design school",
        "Topography school",
        "Telecommunications school",
        "ETSISI",
        "Library",
        "CITSEM"
    )

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
        locationManager = LocationActivityHelper(this)
            .onLocationUpdated { appState.location = it; onLocationChanged(it) }
        navigationManager = NavigationActivityHelper(this, R.id.nav_view, appState)

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
        map.controller.setZoom(15.0)
        map.controller.setCenter(startPoint)


        //Add list of markers
        addGymkhanaMarkers(map, gymkhanaCoords, gymkhanaNames, this)
        addRouteMarkers(map, gymkhanaCoords, gymkhanaNames, this)
        map.controller.setZoom(18.0)

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

    fun onLocationChanged(location: Location) {
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
        appState.currentMission?.let { mission ->
            val distance = location.distanceTo(mission)
            if (distance <= 50f) {
                Toast.makeText(this, getString(R.string.mission_completion), Toast.LENGTH_LONG).show()
                appState.currentMission = null
                Toast.makeText(this, getString(R.string.mission_completion), Toast.LENGTH_LONG).show()
                Log.i(TAG, "User reached destination. Mission cleared.")
                navigationManager.transitionTo(MainActivity::class.java) // When reached, we go back to MainActivity.
            }
        }
    }
}