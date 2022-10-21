package com.armius.dicoding.storyapp.ui.maps

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.armius.dicoding.storyapp.R
import com.armius.dicoding.storyapp.core.net.ApiConfig
import com.armius.dicoding.storyapp.databinding.ActivityMapsBinding
import com.armius.dicoding.storyapp.ui.utils.Event
import com.armius.dicoding.storyapp.ui.viewmodel.ViewModelFactory2
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import org.json.JSONTokener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private val llBoundsBuilder = LatLngBounds.Builder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapsViewModel = ViewModelProvider(this, ViewModelFactory2(ApiConfig.getApiService(this.applicationContext)))[MapsViewModel::class.java]
        mapsViewModel.getStoryList(1, 50)
        mapsViewModel.isLoading.observe(this, {
            showLoading(it)
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        setMapStyle()

        mapsViewModel.listStory.observe(this, { storyList ->
            val customMarker = getCustomMarker()
            for(story in storyList) {
                val ll = LatLng(story.lat, story.lon)
                mMap.addMarker(MarkerOptions().position(ll).title(story.name).icon(customMarker))
                llBoundsBuilder.include(ll)
            }

            val bounds = llBoundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        })
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find style. Error: ", exception)
        }
    }

    private fun getCustomMarker(): BitmapDescriptor {
        val bitmap = getDrawable(R.drawable.map_marker)!!.toBitmap(80,80, null)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun showLoading(isLoading: Boolean) = if (isLoading) {
        binding.loadingView.root.visibility = View.VISIBLE
    } else {
        binding.loadingView.root.visibility = View.GONE
        mapsViewModel.infoMsg.observe(this, {
            it.getContentIfNotHandled()?.let { infoMsg ->
                if(infoMsg!="") {
                    var msg = ""
                    if(infoMsg== Event.ON_FAILURE) {
                        msg = this.getString(R.string.connection_error)
                    } else if(infoMsg== Event.NO_DATA) {
                        msg = this.getString(R.string.no_data)
                    } else {
                        val json = JSONTokener(infoMsg).nextValue() as JSONObject
                        msg = json.getString("message")
                    }
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_INDEFINITE)
                        .setAction(this.getString(R.string.ok)) { }
                        .setAction(this.getString(R.string.retry)) {
                            mapsViewModel.getStoryList(1, 50)
                        }
                        .show()
                }
            }
        })
    }
}