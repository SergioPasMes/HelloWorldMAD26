package es.upm.etsisi.mad.helloworld.helpers

import android.location.Location
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class MissionManager {

    private var onMissionGenerated: ((Location) -> Unit)? = null
    private var onMissionCompleted: (() -> Unit)? = null

    companion object {
        private const val missionAcceptance = 50f
    }

    fun onMissionGenerated(callback: (Location) -> Unit): MissionManager {
        this.onMissionGenerated = callback
        return this
    }

    fun onMissionCompleted(callback: () -> Unit): MissionManager {
        this.onMissionCompleted = callback
        return this
    }

    fun onLocationChanged(location: Location, appState: AppActivityState) {
        // Generate a mission if one doesn't exist (First time or just completed)
        if (appState.currentMission == null) {
            val currentMission = generateRandomDestination(location, 5000.0) // Mission Destination within 5km radius
            appState.currentMission = currentMission
            onMissionGenerated?.invoke(currentMission)
        }

        // Calculate distance to the mission
        val distanceToGoal = location.distanceTo(appState.currentMission!!) // We can use !! here because we just generated it if it was null

        // Check for arrival
        if (distanceToGoal <= missionAcceptance) {
            appState.currentMission = null // Reset so the next update generates a new one
            onMissionCompleted?.invoke()
        }
    }

    private fun generateRandomDestination(center: Location, maxRadiusMeters: Double): Location {
        val radiusInDegrees = maxRadiusMeters / 111320.0 // 1 degree of latitude is roughly 111,320 meters

        // Use sqrt(random) to ensure uniform distribution inside the circle
        val radius = radiusInDegrees * sqrt(Random.Default.nextDouble())
        val theta = 2 * PI * Random.Default.nextDouble()

        val dest = Location(center)
        dest.latitude = center.latitude + (radius * cos(theta))
        dest.longitude = center.longitude + (radius * sin(theta) /
                cos(center.latitude * PI / 180.0))
        return dest
    }
}