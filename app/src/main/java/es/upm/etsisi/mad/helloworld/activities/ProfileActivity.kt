package es.upm.etsisi.mad.helloworld.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.upm.etsisi.mad.helloworld.data.ProfilePictureManager
import es.upm.etsisi.mad.helloworld.data.ProfilePicturePickerManager
import es.upm.etsisi.mad.helloworld.data.ProfilePreferences
import es.upm.etsisi.mad.helloworld.R
import es.upm.etsisi.mad.helloworld.helpers.AppActivityState
import es.upm.etsisi.mad.helloworld.helpers.LocationActivityHelper
import es.upm.etsisi.mad.helloworld.helpers.NavigationActivityHelper
import es.upm.etsisi.mad.helloworld.helpers.UpdateManager
import es.upm.etsisi.mad.helloworld.helpers.unbundleAppState

class ProfileActivity : AppCompatActivity() {
    val TAG = this::class.simpleName

    private lateinit var appState: AppActivityState

    private lateinit var navigationManager: NavigationActivityHelper
    private lateinit var locationManager: LocationActivityHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        appState = intent.unbundleAppState()
        navigationManager = NavigationActivityHelper(this, R.id.nav_view, appState)
            .onBackPressed { navigationManager.transitionTo(MainActivity::class.java) }
        locationManager = LocationActivityHelper(this)
            .onLocationUpdated { appState.location = it }

        val avatarImage = findViewById<ImageView>(R.id.avatarImage)
        // If not found, use default avatar.
        val savedName = ProfilePreferences.getProfilePic(this).ifBlank { ProfilePreferences.DEFAULT_PFP }
        val profilePictureImage = ProfilePictureManager.getImage(this, savedName)!!

        avatarImage.setImageBitmap(profilePictureImage.toBitmap())

        showCurrentProfileData()

        val changeAliasButton: Button = findViewById(R.id.changeAliasButton)
        changeAliasButton.setOnClickListener {
            showUserAliasDialogue()
        }

        val changeAgeButton: Button = findViewById(R.id.changeAgeButton)
        changeAgeButton.setOnClickListener {
            showUserAgeDialogue()
        }

        val changeAvatarButton: Button = findViewById(R.id.changeAvatarButton)
        changeAvatarButton.setOnClickListener {
            showUserAvatarDialogue()
        }
    }

    private fun showCurrentProfileData() {
        val aliasText = findViewById<TextView>(R.id.userAlias)
        val ageText = findViewById<TextView>(R.id.userAge)
        val avatarImage = findViewById<ImageView>(R.id.avatarImage)

        aliasText.text = ProfilePreferences.getAlias(this) ?: "Set Alias"
        val savedAge = ProfilePreferences.getAge(this)
        ageText.text = if (savedAge > 0) savedAge.toString() else "Set Age"

        val savedImageName = ProfilePreferences.getProfilePic(this).ifBlank { ProfilePreferences.DEFAULT_PFP }
        val savedImage = ProfilePictureManager.getImage(this, savedImageName)!!
        avatarImage.setImageBitmap(savedImage.toBitmap())
    }

    private fun showUserAliasDialogue() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter your Alias")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val inputAlias = input.text.toString()
            if (inputAlias.length in ProfilePreferences.USERNAME_MIN_LENGTH..ProfilePreferences.USERNAME_MAX_LENGTH) {
                val currentAlias = findViewById<TextView>(R.id.userAlias)
                currentAlias.text = inputAlias
                UpdateManager.updateAlias(this, inputAlias)
                ProfilePreferences.setAlias(this, inputAlias)
                Toast.makeText(this, "Alias updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Alias must be between ${ProfilePreferences.USERNAME_MIN_LENGTH} " +
                            "and ${ProfilePreferences.USERNAME_MAX_LENGTH} characters",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(this, "Alias update cancelled", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }
        builder.show()
    }

    private fun showUserAgeDialogue() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter your Age")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val stringAge = input.text.toString()
            val inputAge = stringAge.toIntOrNull()
            if (inputAge != null && inputAge in ProfilePreferences.AGE_MIN..ProfilePreferences.AGE_MAX) {
                val currentAge = findViewById<TextView>(R.id.userAge)
                currentAge.text = stringAge
                UpdateManager.updateAge(this, inputAge)
                ProfilePreferences.setAge(this, inputAge)
                Toast.makeText(this, "Age updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Age value must be between ${ProfilePreferences.AGE_MIN} and ${ProfilePreferences.AGE_MAX}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(this, "Age update cancelled", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }
        builder.show()
    }

    private fun showUserAvatarDialogue() {
        val profileImages = ProfilePictureManager.getAllImages(this)

        ProfilePicturePickerManager(this, profileImages) { image ->
            UpdateManager.updateProfilePicture(this, image.name)
            ProfilePreferences.setProfilePic(this, image.name)
            showCurrentProfileData()
        }.show()
    }
}