package com.example.mymechanic_user

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val PERMISSION_REQUEST = 10

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location?= null
    private var locationNetwork: Location?= null
    private var mlocation: Location?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        disableView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission(permissions)){
                enableView()
            }
            else{
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }
        else{
            enableView()
        }
    }

    private fun disableView(){
        Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
    }
    private fun enableView(){
        Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
        getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(hasGps || hasNetwork){

            if(hasGps){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?){
                        if (location != null){
                            locationGps = location
                            mlocation = location
                            //Toast.makeText(applicationContext, "lat : " + location.latitude.toString(), Toast.LENGTH_SHORT).show()
                            //Toast.makeText(applicationContext, "long : " + location.longitude.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                    }

                    override fun onProviderEnabled(p0: String?) {
                    }

                    override fun onProviderDisabled(p0: String?) {
                    }
                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null){
                    locationGps = localGpsLocation
                    mlocation = localGpsLocation
                }
            }

            if (hasNetwork){

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0f, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?){
                        if (location != null){
                            locationGps = location
                            mlocation = location
                            //Toast.makeText(applicationContext, "lat : " + location.latitude.toString(), Toast.LENGTH_SHORT).show()
                            //Toast.makeText(applicationContext, "long : " + location.longitude.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                    }

                    override fun onProviderEnabled(p0: String?) {
                    }

                    override fun onProviderDisabled(p0: String?) {
                    }
                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null){
                    locationGps = localNetworkLocation
                    mlocation = localNetworkLocation
                }
            }

            if (locationGps != null && locationNetwork != null){
                if (locationGps!!.accuracy > locationNetwork!!.accuracy){
                    //Toast.makeText(applicationContext, "lat : " + locationNetwork!!.latitude.toString(), Toast.LENGTH_SHORT).show()
                    //Toast.makeText(applicationContext, "long : " + locationNetwork!!.longitude.toString(), Toast.LENGTH_SHORT).show()
                    mlocation = locationGps
                }
                else{
                    //Toast.makeText(applicationContext, "lat : " + locationGps!!.latitude.toString(), Toast.LENGTH_SHORT).show()
                    //Toast.makeText(applicationContext, "long : " + locationGps!!.longitude.toString(), Toast.LENGTH_SHORT).show()
                    mlocation = locationNetwork
                }
            }

        }
        else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
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
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST){
            var allSuccess = true
            for(i in permissions.indices){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allSuccess = false

                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])

                    if (requestAgain){
                        Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "GOTO SETTINGS", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess){
                enableView()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val curLocation = LatLng(mlocation!!.latitude, mlocation!!.longitude)
        mMap.addMarker(MarkerOptions().position(curLocation).title("Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation))

    }
}
