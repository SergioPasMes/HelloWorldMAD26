package es.upm.etsisi.mad.helloworld

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LandingActivity : AppCompatActivity() {
    private val TAG = this::class.toString()

    private var currentPfp: ProfilePicture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_landing)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check whether to show the activity or directly transition to MainActivity.
        if(isProfileConfigured()) {
            Log.i(TAG, "Profile has already been configured, transitioning to `MainActivity`")
            switchToMainActivity()
            return
        }

        // Update the app name in the welcome string.
        val welcomeTextWidget = findViewById<TextView>(R.id.welcome_text_header)
        val welcomeTextString = welcomeTextWidget.text.toString().format(getString(R.string.app_name))
        welcomeTextWidget.text = welcomeTextString

        val pfpView = findViewById<ImageView>(R.id.pfp)
        pfpView.setOnClickListener { openProfilePictureDialog() }

        val confirmButton = findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener { onConfirmPressed() }
    }

    /**
     * Checks whether the user profile has been configured.
     */
    private fun isProfileConfigured(): Boolean {
        // Assume that, if the alias is configured, so is the profile.
        return ProfilePreferences.getAlias(this) != ""
    }

    /**
     * Transitions to MainActivity.
     */
    private fun switchToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Opens the profile picture picker dialog.
     */
    private fun openProfilePictureDialog() {
        val profilePictures = ProfilePictureManager.getAllImages(this)
        ProfilePicturePickerManager(this, profilePictures) { chosenPfp ->
            currentPfp = chosenPfp
            val pfpView = findViewById<ImageView>(R.id.pfp)
            val bitmap = BitmapFactory.decodeByteArray(chosenPfp.image, 0, chosenPfp.image.size)
            pfpView.setImageBitmap(bitmap)
        }.show()
    }

    /**
     * Executed when the confirm button is pressed.
     */
    private fun onConfirmPressed() {
        val username = getUsernameInput()
        val age = getAgeInput()

        val usernameOkay = checkUsernameAndShowError(username)
        val ageOkay = checkAgeAndShowError(age)

        if(usernameOkay && ageOkay) {
            Log.i(TAG, "Saving preferences:")
            Log.i(TAG, "    Username:   $username")
            Log.i(TAG, "    Age:        $age")
            Log.i(TAG, "    Picture:    ${currentPfp?.name ?: "(none)"}")

            ProfilePreferences.setAlias(this, username)
            ProfilePreferences.setAge(this, age!!)
            currentPfp?.let {
                ProfilePreferences.setProfilePic(this, it.name)
            }

            Toast.makeText(
                this,
                getString(R.string.landing_activity_toast_profile_created),
                Toast.LENGTH_LONG
            ).show()

            switchToMainActivity()
        }
    }

    /**
     * Returns the username input value.
     */
    private fun getUsernameInput(): String {
        val input = findViewById<EditText>(R.id.username_input)
        return input.text.toString()
    }

    /**
     * Returns the age input value.
     */
    private fun getAgeInput(): Int? {
        val input = findViewById<EditText>(R.id.age_input)

        return when(input.text.length) {
            0 -> null
            else -> Integer.parseInt(input.text.toString())
        }
    }

    /**
     * Checks whether the username meets the criteria. If not, shows an error.
     *
     * @return `true` if the value meets the criteria, `false` otherwise.
     */
    private fun checkUsernameAndShowError(username: String): Boolean {
        val error = when {
            username.isEmpty() -> getString(R.string.landing_activity_username_error_missing)

            username.length < ProfilePreferences.USERNAME_MIN_LENGTH ->
                getString(R.string.landing_activity_username_error_too_short)
                    .format(ProfilePreferences.USERNAME_MIN_LENGTH)

            username.length > ProfilePreferences.USERNAME_MAX_LENGTH ->
                getString(R.string.landing_activity_username_error_too_long)
                    .format(ProfilePreferences.USERNAME_MAX_LENGTH)

            else -> null
        }

        if(error == null) {
            return true
        }

        val input = findViewById<EditText>(R.id.username_input)
        input.error = error
        return false
    }

    /**
     * Checks whether the age meets the criteria. If not, shows an error.
     *
     * @return `true` if the value meets the criteria, `false` otherwise.
     */
    private fun checkAgeAndShowError(age: Int?): Boolean {
        val error = when {
            age == null -> getString(R.string.landing_activity_age_error_missing)

            age < 0 ->
                getString(R.string.landing_activity_age_error_negative)

            age < ProfilePreferences.AGE_MIN ->
                getString(R.string.landing_activity_age_error_too_low)
                    .format(ProfilePreferences.AGE_MIN)

            age > ProfilePreferences.AGE_MAX ->
                getString(R.string.landing_activity_age_error_too_high)
                    .format(ProfilePreferences.AGE_MAX)

            else -> null
        }

        if(error == null) {
            return true
        }

        val input = findViewById<EditText>(R.id.age_input)
        input.error = error
        return false
    }
}