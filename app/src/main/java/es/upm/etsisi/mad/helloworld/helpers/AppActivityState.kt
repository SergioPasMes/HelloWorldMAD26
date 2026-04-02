package es.upm.etsisi.mad.helloworld.helpers

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Class for maintaining common state between activities.
 *
 * Currently, manages:
 *  - User location.
 */
@Parcelize
class AppActivityState(
    /**
     * The device's current location.
     */
    var location: Location? = null,

    /**
     * Current mission's location.
     */
    var currentMission: Location? = null
) : Parcelable {

    companion object {
        const val BUNDLE_KEY = "app_activity_state"
        const val PARCEL_KEY = BUNDLE_KEY


        /**
         * Deserializes a bundle and returns an instance of `AppActivityState`.
         *
         * If the state is not included in the bundle, a default state is returned.
         */
        fun unbundle(intent: Intent): AppActivityState {
            return intent.getBundleExtra(BUNDLE_KEY)
                ?.getParcelable(PARCEL_KEY)
                ?: AppActivityState()
        }
    }

    /**
     * Serializes this state into a bundle and adds it to an intent.
     */
    fun injectBundle(intent: Intent) {
        val bundle = Bundle()
        bundle.putParcelable(PARCEL_KEY, this)

        intent.putExtra(BUNDLE_KEY, bundle)
    }

}

/**
 * Serializes this state into a bundle and adds it to an intent.
 *
 * Same as calling `AppActivityState.injectBundle(intent)`.
 */
fun Intent.injectAppState(state: AppActivityState) = state.injectBundle(this)

/**
 * Deserializes and restores a bundled instance of `AppActivityState`.
 *
 * If the state is not included in the bundle, a default state is returned.
 *
 * Same as calling `AppActivityState.unbundle(intent)`.
 */
fun Intent.unbundleAppState(): AppActivityState = AppActivityState.unbundle(this)