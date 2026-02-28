package es.upm.etsisi.mad.helloworld

import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class ProfileActivity : AppCompatActivity(),
NavigationView.OnNavigationItemSelectedListener {

    private val avatarMap = mapOf(
        "default_avatar" to R.drawable.home_icon, // CHANGE "home_icon" TO "default_avatar"
        "avatar1" to R.drawable.location_icon,
        "avatar2" to R.drawable.profile_icon,
        "avatar3" to R.drawable.ic_launcher_foreground
    )

    private lateinit var drawerLayout: DrawerLayout

    private var latestLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val avatarImage = findViewById<ImageView>(R.id.avatarImage)
        val savedName = ProfilePreferences.getProfilePic(this)

        // Convert name to ID. If not found, use default_avatar
        val resId = if (savedName.isNotBlank()) {
            resources.getIdentifier(savedName, "drawable", packageName)
        } else {
            R.drawable.home_icon  // CHANGE "home_icon" TO "default_avatar"
        }
        avatarImage.setImageResource(if (resId != 0) resId else R.drawable.home_icon) // CHANGE "home_icon" TO "default_avatar"

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

        val savedImageName = ProfilePreferences.getProfilePic(this)
        val resId = avatarMap[savedImageName] ?: R.drawable.home_icon // CHANGE TO "home_icon" to "default_avatar"
        avatarImage.setImageResource(resId)
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
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an Avatar")

        // Get lists from our map
        val names = avatarMap.keys.toList()
        val ids = avatarMap.values.toList()

        val gridView = layoutInflater.inflate(R.layout.dialog_avatar_picker, null) as GridView
        gridView.adapter = AvatarAdapter(this, ids)
        builder.setView(gridView)

        val dialog = builder.create()

        gridView.setOnItemClickListener { _, _, position, _ ->
            ProfilePreferences.setProfilePic(this, names[position])
            showCurrentProfileData()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_profile_activity -> {
                // Already in profile
            }

            R.id.nav_open_street_map -> {
                val intent = Intent(this, OpenStreetMapActivity::class.java)
                startActivity(intent)
                /* NEED TO FIX FOR WEEK 5
                if (latestLocation != null) {
                    val intent = Intent(this, OpenStreetMapActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("location", latestLocation)
                    intent.putExtra("locationBundle", bundle)
                    startActivity(intent)
                } else {
                    Log.e(TAG, "Location is not yet set")
                }
                */
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}