package com.example.geolocationproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.geolocationproject.databinding.ActivityMainBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMap()
    }

    override fun onMapReady(map: GoogleMap) {
        map.addMarker(
            MarkerOptions().apply {
                position(LatLng(0.0, 0.0))
                title("Test")
            }
        )
    }

    private fun initMap() {
        (supportFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment)
            .getMapAsync(this)
    }
}
