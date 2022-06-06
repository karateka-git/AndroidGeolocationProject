package com.example.geolocationproject

import android.Manifest
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
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

    private fun initListeners() {
        binding.getMyLocationButton.setOnClickListener {
            updateUserLastLocationWithPermissionCheck()
        }
    }

    @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    @SuppressLint("MissingPermission")
    fun updateUserLastLocation() {
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

    @OnShowRationale(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun showAccessGeolocationDialog(request: PermissionRequest) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.access_geolocation_dialog_title))
            .setPositiveButton(R.string.access_geolocation_dialog_positive_button_text) { _, _ ->
                request.proceed()
            }
            .setNegativeButton(R.string.access_geolocation_dialog_negative_button_text) { _, _ ->
                request.cancel()
            }
            .show()
    }
}
