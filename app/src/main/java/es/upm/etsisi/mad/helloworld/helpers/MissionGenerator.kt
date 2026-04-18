package es.upm.etsisi.mad.helloworld.helpers

import android.content.Context
import android.location.Location
import android.util.Log
import es.upm.etsisi.mad.helloworld.data.Mission
import es.upm.etsisi.mad.helloworld.data.ProfilePreferences

class MissionGenerator {
    private val TAG = this::class.simpleName

    private enum class GeneratorState {
        IDLE,
        GENERATING
    }


    private var generatedMission: Mission? = null
    private var state: GeneratorState = GeneratorState.IDLE

    fun isMissionGenerating(): Boolean {
        return state != GeneratorState.IDLE
    }

    /**
     * Enqueues the generation of a new mission, executing the `onMissionGenerated` callback when
     * the mission is generated. In case of an error `onFailure` is called.
     *
     * While this method is running, use `isMissionGenerating` to check if the method has finished.
     */
    fun generateMission(
        context: Context, currentLocation: Location,
        onMissionGenerated: (Mission) -> Unit, onFailure: ((String?) -> Unit)?
    ) {
        val plant = ProfilePreferences.getPlant(context)
        val mainNecessity = plant?.mainNecessity
        if(mainNecessity == null) {
            val error = "Error generating mission, plant is not configured"
            Log.w(TAG, "Error generating mission, plant is not configured")
            onFailure?.invoke(error)
            return
        }
        APIManager().getMission(
            context, mainNecessity, currentLocation.latitude, currentLocation.longitude,
            onSuccess = onMissionGenerated,
            onError = { e -> onFailure?.invoke(e) }
        )
    }
}