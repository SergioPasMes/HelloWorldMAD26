package es.upm.etsisi.mad.helloworld

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {

    private val avatarMap = mapOf(
        "default_avatar" to R.drawable.default_avatar,
        "avatar1" to R.drawable.avatar1,
        "avatar2" to R.drawable.avatar2,
        "avatar3" to R.drawable.avatar3
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val avatarImage = findViewById<ImageView>(R.id.avatarImage)
        val savedName = ProfilePreferences.getProfilePic(this)

        // Convert name to ID. If not found, use default_avatar
        val resId = if (savedName.isNotBlank()) {
            resources.getIdentifier(savedName, "drawable", packageName)
        } else {
            R.drawable.default_avatar
        }
        avatarImage.setImageResource(if (resId != 0) resId else R.drawable.default_avatar)

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
        val resId = avatarMap[savedImageName] ?: R.drawable.default_avatar
        avatarImage.setImageResource(resId)
    }

    private fun showUserAliasDialogue() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter your Alias")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val inputAlias = input.text.toString()
            if (inputAlias.isNotBlank() && inputAlias.length <= 20) {
                val currentAlias = findViewById<TextView>(R.id.userAlias)
                currentAlias.text = inputAlias
                ProfilePreferences.setAlias(this, inputAlias)
                Toast.makeText(this, "Alias updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Alias cannot be empty or above 20 characters", Toast.LENGTH_SHORT).show()
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
            if (inputAge != null && inputAge in 1..100) {
                val currentAge = findViewById<TextView>(R.id.userAge)
                currentAge.text = stringAge
                ProfilePreferences.setAge(this, inputAge)
                Toast.makeText(this, "Age updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Age value must be between 1 and 100", Toast.LENGTH_SHORT).show()
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
}