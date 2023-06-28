package com.example.clase12retrofitterremotos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private var listOfQuakes: MutableList<Feature> = mutableListOf()
    private lateinit var textViewTitle: TextView
    private lateinit var adapter: QuakeAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewTitle = findViewById(R.id.textViewTitle)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = QuakeAdapter()
        recyclerView.adapter = adapter

        getListOfQuakes()
    }

    private fun getListOfQuakes() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getAllByWeek()
            val response = call.body()

            runOnUiThread {
                if (call.isSuccessful) {
                    val title = response?.metadata?.title ?: "Listado de terremotos"
                    val quakes = response?.features

                    quakes?.forEach { quake ->
                        listOfQuakes.add(quake)
                    }
                    adapter.submitList(listOfQuakes)
                    textViewTitle.text = title
                } else {
                    val error = call.errorBody().toString()
                    Log.e("error", error,)
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}