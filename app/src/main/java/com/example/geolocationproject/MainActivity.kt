package com.example.geolocationproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.geolocationproject.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import permissions.dispatcher.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private val PERMISSION_UPDATE_USER_LAST_LOCATION: Array<String> =
            arrayOf("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION")
        private const val REQUEST_UPDATE_USER_LAST_LOCATION: Int = 0
    }

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
            updateUserLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun updateUserLastLocation() {
        if (PermissionUtils.hasSelfPermissions(this, *PERMISSION_UPDATE_USER_LAST_LOCATION)) {
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
        } else {
            if (PermissionUtils.shouldShowRequestPermissionRationale(
                    this,
                    *PERMISSION_UPDATE_USER_LAST_LOCATION
                )
            ) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.access_geolocation_dialog_title))
                    .setPositiveButton(R.string.access_geolocation_dialog_positive_button_text) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            PERMISSION_UPDATE_USER_LAST_LOCATION,
                            REQUEST_UPDATE_USER_LAST_LOCATION
                        )
                    }
                    .setNegativeButton(R.string.access_geolocation_dialog_negative_button_text) { _, _ ->
                        onAccessGeolocationDenied()
                    }.show()
            } else {
                // TODO go to Settings
                onAccessGeolocationNeverAskAgain()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != REQUEST_UPDATE_USER_LAST_LOCATION) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (PermissionUtils.verifyPermissions(*grantResults)) {
            updateUserLastLocation()
        } else {
            // TODO Permission denied
        }
    }

    fun onAccessGeolocationDenied() {
        Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show()
    }

    fun onAccessGeolocationNeverAskAgain() {
        Toast.makeText(this, "Never Ask Again", Toast.LENGTH_SHORT).show()
    }
}
