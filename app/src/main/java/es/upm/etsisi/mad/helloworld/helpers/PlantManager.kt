package es.upm.etsisi.mad.helloworld.helpers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import es.upm.etsisi.mad.helloworld.R
import es.upm.etsisi.mad.helloworld.api.RetrofitAPI
import es.upm.etsisi.mad.helloworld.api.RetrofitInstance
import es.upm.etsisi.mad.helloworld.api.RetrofitInstance.parseError
import es.upm.etsisi.mad.helloworld.data.Plant
import es.upm.etsisi.mad.helloworld.data.ProfilePreferences
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

object PlantManager {
    private const val TAG = "PlantManager"
    private const val TWO_WEEKS_MILLIS = 14L * 24 * 60 * 60 * 1000

    fun checkPlantRoutine(activity: AppCompatActivity) {
        val lastTime = ProfilePreferences.getLastPlantTime(activity)
        val currentTime = System.currentTimeMillis()

        if (lastTime == 0L || (currentTime - lastTime) >= TWO_WEEKS_MILLIS) {
            fetchPlants(activity)
        }
    }

    private fun fetchPlants(activity: AppCompatActivity) {
        val token = ProfilePreferences.getToken(activity)
        val api = RetrofitInstance.getRetrofitInstance().create(RetrofitAPI::class.java)

        api.getPlants(token).enqueue(object : Callback<List<Plant>>{
            override fun onResponse(call: Call<List<Plant>>, response: Response<List<Plant>>) {
                if (response.isSuccessful) {
                    response.body()?.let { showSelectionDialog(activity, it) }
                } else {
                    val errorMsg = parseError(response)
                    Log.e(TAG, "Error API: $errorMsg")
                    Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Plant>>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message?: "Unknown error"}")
            }
        })
    }

    private fun showSelectionDialog(activity: AppCompatActivity, plants: List<Plant>) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose new plant")
        builder.setCancelable(false)

        // Adapter to use dialogue layout for each plant item
        val adapter = object : ArrayAdapter<Plant>(activity, R.layout.plant_dialogue, plants) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.plant_dialogue, parent, false)

                val plant = getItem(position)
                val nameTv = view.findViewById<TextView>(R.id.plantName)
                val imageIv = view.findViewById<ImageView>(R.id.plantImage)

                nameTv.text = plant?.name
                Glide.with(activity).load(plant?.picture).into(imageIv)

                return view
            }
        }

        builder.setAdapter(adapter) { _, which ->
            saveSelection(activity, plants[which])
        }
        builder.show()
    }

    private fun saveSelection(activity: AppCompatActivity, plant: Plant) {
        ProfilePreferences.setLastPlantTime(activity, System.currentTimeMillis())

        val imageUrl = plant.picture ?: ""
        ProfilePreferences.setPlantImage(activity, imageUrl)

        Toast.makeText(activity, "You chose: ${plant.name}!", Toast.LENGTH_SHORT).show()

        activity.recreate()
    }
}