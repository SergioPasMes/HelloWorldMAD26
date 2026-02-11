package es.upm.etsisi.mad.helloworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonNext = findViewById<Button>(R.id.button_main_to_second)
        buttonNext.setOnClickListener {
            Log.i(TAG, "Transitioning to SecondActivity")
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        Log.i(TAG, "Created activity $TAG")
    }
}