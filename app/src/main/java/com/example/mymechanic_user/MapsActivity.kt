package com.example.mymechanic_user

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

private const val PERMISSION_REQUEST = 10

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location?= null
    private var locationNetwork: Location?= null
    private var mlocation: Location?= null

    var distance = 0

    private lateinit var selectBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        distance = intent.getIntExtra("Distance", 0)

        selectBtn = findViewById(R.id.selectBtn)
        selectBtn.setOnClickListener {
                rangeSelection()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        if(checkPermission(permissions)){
            enableView()
        }
        else{
            requestPermissions(permissions, PERMISSION_REQUEST)
        }

    }

    private fun rangeSelection() {
        val intent = Intent(this, RangeSelectionActivity::class.java)
        intent.putExtra("CurrentLatitude", mlocation!!.latitude)
        intent.putExtra("CurrentLongitude", mlocation!!.longitude)
        startActivity(intent)
    }

    private fun enableView(){
        Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
        getLocation()
    }

    //@SuppressLint("MissingPermission")
    private fun getLocation(){

        if (checkPermission(permissions)) {

            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (hasGps || hasNetwork) {

                if (hasGps) {

                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        0f,
                        object :
                            LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationGps = location
                                    mlocation = location
                                }
                            }

                            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                            }

                            override fun onProviderEnabled(p0: String?) {
                            }

                            override fun onProviderDisabled(p0: String?) {
                            }
                        })

                    val localGpsLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (localGpsLocation != null) {
                        locationGps = localGpsLocation
                        mlocation = localGpsLocation
                    }
                }

                if (hasNetwork) {

                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        5000,
                        0f,
                        object :
                            LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationGps = location
                                    mlocation = location
                                }
                            }

                            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                            }

                            override fun onProviderEnabled(p0: String?) {
                            }

                            override fun onProviderDisabled(p0: String?) {
                            }
                        })

                    val localNetworkLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (localNetworkLocation != null) {
                        locationGps = localNetworkLocation
                        mlocation = localNetworkLocation
                    }
                }

                if (locationGps != null && locationNetwork != null) {

                    when(locationGps!!.accuracy > locationNetwork!!.accuracy){
                        true -> mlocation = locationGps
                        false -> mlocation = locationNetwork
                    }
                }

            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
        else{
            requestPermissions(permissions, PERMISSION_REQUEST)
        }

        if (locationGps != null && locationNetwork != null) {

            when(locationGps!!.accuracy > locationNetwork!!.accuracy){
                true -> mlocation = locationGps
                false -> mlocation = locationNetwork
            }
        }
    }

    private fun checkPermission(permissionArray: Array<String>):Boolean{

        var allSuccess = true

        for(i in permissionArray.indices){
            if(checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED){
                allSuccess = false
            }
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST){
            var allSuccess = true
            for(i in permissions.indices){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allSuccess = false

                    val requestAgain = shouldShowRequestPermissionRationale(permissions[i])

                    if (requestAgain){
                        Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "GOTO Settings and provide Permissions", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess){
                enableView()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val curLocation = LatLng(mlocation!!.latitude, mlocation!!.longitude)
        mMap.addMarker(MarkerOptions().position(curLocation).title("Current Location")).setIcon(
            BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

        mMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 15f))
        mMap.animateCamera(CameraUpdateFactory.zoomIn())
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)

        var db = FirebaseFirestore.getInstance()

        db.collection("Profile").get()
            .addOnSuccessListener {
                for (dc in it){

                    val lat = dc.data["Latitude"].toString().toDouble()
                    val lng = dc.data["Longitude"].toString().toDouble()
                    val mechanicLocation = LatLng(lat, lng)

                    var dist = calcDistance(curLocation, mechanicLocation)

                    if (dist <= distance) {
                        mMap.addMarker(MarkerOptions().position(mechanicLocation).title(dc.data["Name"].toString()))
                    }

                }
            }

    }
}
