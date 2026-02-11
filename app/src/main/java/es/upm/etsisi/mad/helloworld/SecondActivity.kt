package es.upm.etsisi.mad.helloworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    private val TAG = this::class.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonPrevious = findViewById<Button>(R.id.button_second_to_main)
        buttonPrevious.setOnClickListener {
            Log.i(TAG, "Transitioning to MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonNext = findViewById<Button>(R.id.button_second_to_third)
        buttonNext.setOnClickListener {
            Log.i(TAG, "Transitioning to ThirdActivity")
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        Log.i(TAG, "Created activity $TAG")
    }
}