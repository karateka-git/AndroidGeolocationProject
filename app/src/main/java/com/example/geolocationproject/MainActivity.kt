package com.example.geolocationproject

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.geolocationproject.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMap()
        initListeners()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
    }

    private fun initMap() {
        (supportFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment)
            .getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    private fun initListeners() {
        binding.getMyLocationButton.setOnClickListener {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { lastLocationTask ->
                if (lastLocationTask.isSuccessful) {
                    val lastLocation = lastLocationTask.result
                    map?.addMarker(
                        MarkerOptions().apply {
                            position(
                                LatLng(lastLocation.latitude, lastLocation.longitude)
                            )
                        }
                    )
                } else {
                    map?.clear()
                }
            }
        }
    }
}
