package es.upm.etsisi.mad.helloworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LandingActivity : AppCompatActivity() {
    private val TAG = this::class.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_landing)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Update the app name in the welcome string.
        val welcomeTextWidget = findViewById<TextView>(R.id.welcome_text_header)
        val welcomeTextString = welcomeTextWidget.text.toString().format(getString(R.string.app_name))
        welcomeTextWidget.text = welcomeTextString

        val confirmButton = findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener { onConfirmPressed() }
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
     * Executed when the confirm button is pressed.
     */
    private fun onConfirmPressed() {
        val username = getUsernameInput()
        val age = getAgeInput()

        Log.i(TAG, "Saving preferences:")
        Log.i(TAG, "    Username:   $username")
        Log.i(TAG, "    Age:        $age")

        ProfilePreferences.setAlias(this, username)
        ProfilePreferences.setAge(this, age!!)

        switchToMainActivity()
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
}