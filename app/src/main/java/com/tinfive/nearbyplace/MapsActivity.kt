package com.tinfive.nearbyplace

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tinfive.nearbyplace.Common.Common
import com.tinfive.nearbyplace.Model.MyPlaces
import com.tinfive.nearbyplace.Remote.IGoogleAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var latitude: Double = 0.toDouble()
    private var longtitude: Double = 0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker: Marker? = null

    //Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
    }

    lateinit var mService: IGoogleAPIService

    internal lateinit var currentPlaces: MyPlaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mService = Common.googleApiService

        //Requeest runtime Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermisson()) {
                buildLocationRequest()
                buildLocationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            }
        } else {
            buildLocationRequest()
            buildLocationCallback()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }
        /*bottom_navigation_view.setOnNavigationItemSelectedListener { item->
            when(item.itemId)
            {
                R.id.action_hospital -> nearByPlace ("Hospital")
                R.id.action_restaurant -> nearByPlace ("Restaurant")
//                R.id.action_market -> nearByPlace ("Market")
                R.id.action_school -> nearByPlace ("School")
            }
            true
        }*/
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations.get(p0.locations.size - 1) //Get Last Location
                if (mMarker != null) {
                    mMarker!!.remove()
                }

                latitude = mLastLocation.latitude
                longtitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longtitude)
                val url = getUrl(latitude, longtitude)

                mService.getNearbyPlaces(url)

                    .enqueue(object : Callback<MyPlaces> {
                        override fun onFailure(call: Call<MyPlaces>, t: Throwable?) {
                            Toast.makeText(baseContext, "" + t!!.message, Toast.LENGTH_SHORT).show()
                        }

                        val geoCoder = Geocoder(this@MapsActivity, Locale.getDefault())

                        override fun onResponse(
                            call: Call<MyPlaces>,
                            response: Response<MyPlaces>
                        ) {
                            currentPlaces = response.body()!!

                            if (response.isSuccessful) {
                                for (i in 0 until response.body()!!.results!!.size) {
                                    val markerOptions = MarkerOptions()
                                        .position(latLng)
                                        .title("Your Position")
                                        .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_GREEN
                                            )
                                        )
                                    val googlePlaces = response.body()!!.results!![i]
                                    val lat = googlePlaces.geometry!!.location!!.lat
                                    val lng = googlePlaces.geometry!!.location!!.lng
                                    val placeName = googlePlaces.name
                                    val latLng = LatLng(lat, lng)

                                    markerOptions.position(latLng)
                                    markerOptions.title(placeName)

                                    //Add marker to map
                                    mMap.addMarker(markerOptions)

                                }

                            }
                            try {
                                val listAddress: List<Address> =
                                    geoCoder.getFromLocation(latitude, longtitude, 1)
                                if (null != listAddress && listAddress.size > 0) {
                                    val placeAddress = listAddress.get(0).getAddressLine(0)
                                    val placeName = listAddress.get(0).featureName
                                    Log.d(
                                        "location me",
                                        "${listAddress.get(0).featureName} ${listAddress.get(0).adminArea} ${listAddress.get(
                                            0
                                        ).subLocality} ${listAddress.get(0).locale}"
                                    )
                                    Toast.makeText(
                                        applicationContext,
                                        "$placeName $placeAddress",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            //Move Camera
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            val cu = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                            // Animate Camera
                            mMap.animateCamera(cu)
                        }

                    })
            }
        }

    }

    private fun getUrl(latitude: Double, longtitude: Double): String {
        val googlePlaceUrl =
            StringBuilder("https://raw.githubusercontent.com/achmade96/lokasimasjid/master/lokasimasjid.json")
        /*googlePlaceUrl.append("?location=$latitude,$longtitude")
        googlePlaceUrl.append("&radius=1000") //1000=1km
        googlePlaceUrl.append("&type=$typePlace")
        googlePlaceUrl.append("&key=AIzaSyBHkbWKsDCZtTUPn-qW-Lzjzmkbj7_1LmY")*/

        Log.d("URL_DEBUG", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }

    /*private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations.get(p0.locations.size - 1) //Get Last Location
                if (mMarker != null) {
                    mMarker!!.remove()
                }

                latitude = mLastLocation.latitude
                longtitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longtitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                //tes

                mMarker = mMap.addMarker(markerOptions)

                //Move Camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17f))
            }
        }
    }*/

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun checkLocationPermisson(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            else
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            return false
        } else
            return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        if (checkLocationPermisson()) {
                            buildLocationRequest()
                            buildLocationCallback()

                            fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()
                            )
                            mMap.isMyLocationEnabled = true
                        }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
                mMap.isMyLocationEnabled = true
        } else
            mMap.isMyLocationEnabled = true

        //Enable Zoom Control
        mMap.uiSettings.isZoomControlsEnabled
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }
}
