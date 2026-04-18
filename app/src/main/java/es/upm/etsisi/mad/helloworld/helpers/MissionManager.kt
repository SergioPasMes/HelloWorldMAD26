package es.upm.etsisi.mad.helloworld.helpers

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import es.upm.etsisi.mad.helloworld.R
import es.upm.etsisi.mad.helloworld.data.Mission
import es.upm.etsisi.mad.helloworld.data.ProfilePreferences
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class MissionManager {
    private val TAG = this::class.simpleName

    private val MISSION_POINTS = 100;
    private val MISSION_NECESSITY_BONUS = 1.5;

    private var onMissionGenerated: ((Mission) -> Unit)? = null
    private var onMissionCompleted: ((MissionManager, Mission) -> Unit)? = null

    private val missionGenerator: MissionGenerator = MissionGenerator()

    companion object {
        private const val missionAcceptance = 50f
    }

    fun onMissionGenerated(callback: (Mission) -> Unit): MissionManager {
        this.onMissionGenerated = callback
        return this
    }

    fun onMissionCompleted(callback: (MissionManager, Mission) -> Unit): MissionManager {
        this.onMissionCompleted = callback
        return this
    }

    fun completeMission(context: Context, mission: Mission) {
        val currentScore = ProfilePreferences.getScore(context)

        // Give a bonus if location meets necessities.
        val plant = ProfilePreferences.getPlant(context)

        val newScore = if(mission.satisfiesNecessities.contains(plant?.mainNecessity)) {
            (currentScore + MISSION_POINTS * MISSION_NECESSITY_BONUS).toInt()
        } else {
            currentScore + MISSION_POINTS
        }

        ProfilePreferences.setScore(context, newScore)
        UpdateManager.updateScore(context, newScore)
        Toast.makeText(context, R.string.mission_completion, Toast.LENGTH_SHORT).show()
    }

    fun onLocationChanged(location: Location, context: Context, appState: AppActivityState) {
        // Generate a mission if one doesn't exist (First time or just completed)
        if (appState.currentMission == null) {
            if(missionGenerator.isMissionGenerating()) {
                Log.i(TAG, "Mission is still generating")
            } else {
                missionGenerator.generateMission(
                    context,
                    location,
                    onMissionGenerated = { mission ->
                        appState.currentMission = mission
                        onMissionGenerated?.invoke(mission)
                        Log.i(
                            TAG, "New mission generated. Go to ${mission.locationName}" +
                            "(${mission.latitude}, ${mission.longitude})"
                        )
                    },
                    onFailure = { err ->
                        Log.e(TAG, "Error generating mission: $err")
                    }
                )
            }

            return
        }


        // Calculate distance to the mission
        val currentMission = appState.currentMission!! // We can use !! here because we just generated it if it was null
        val distanceToGoal = location.distanceTo(currentMission.location)

        // Check for arrival
        if (distanceToGoal <= missionAcceptance) {
            appState.currentMission = null // Reset so the next update generates a new one
            onMissionCompleted?.invoke(this, currentMission)
        }
    }
}