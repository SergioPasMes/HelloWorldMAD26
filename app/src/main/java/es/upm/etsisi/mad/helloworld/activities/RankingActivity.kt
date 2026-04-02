package es.upm.etsisi.mad.helloworld.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upm.etsisi.mad.helloworld.data.ProfilePreferences
import es.upm.etsisi.mad.helloworld.R
import es.upm.etsisi.mad.helloworld.api.RankingAdapter
import es.upm.etsisi.mad.helloworld.api.RetrofitAPI
import es.upm.etsisi.mad.helloworld.api.RetrofitInstance
import es.upm.etsisi.mad.helloworld.data.User
import es.upm.etsisi.mad.helloworld.helpers.AppActivityState
import es.upm.etsisi.mad.helloworld.helpers.NavigationActivityHelper
import es.upm.etsisi.mad.helloworld.helpers.unbundleAppState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingActivity : AppCompatActivity() {
    private lateinit var appState: AppActivityState
    private lateinit var navigationManager: NavigationActivityHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        val progress: ProgressBar = findViewById(R.id.progress)
        val errorText: TextView = findViewById(R.id.errorText)

        appState = intent.unbundleAppState()
        navigationManager = NavigationActivityHelper(this, R.id.nav_view, appState)

        recyclerView.layoutManager = LinearLayoutManager(this)

        progress.visibility = View.VISIBLE

        val api = RetrofitInstance.getRetrofitInstance().create(RetrofitAPI::class.java)
        val myToken = ProfilePreferences.getToken(this)
        api.getUsers(myToken).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>?>, response: Response<List<User>?>) {
                progress.visibility = View.GONE

                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    val sortedUsers = users.sortedByDescending { it.score ?: 0 }
                    recyclerView.adapter = RankingAdapter(sortedUsers)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    errorText.text = getString(R.string.API_error, response.code(), errorResponse)
                    errorText.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                progress.visibility = View.GONE
                errorText.text = t.message
                errorText.visibility = View.VISIBLE
            }
        })
    }
}