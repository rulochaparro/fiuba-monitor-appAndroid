package com.example.configuraciondemonitor

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.*
import android.location.Geocoder
import com.example.configuraciondemonitor.ConnectionPage
import com.example.configuraciondemonitor.ConnectionPageView


class ConnectionPagePresenter(private val connectionView: ConnectionPageView): AppCompatActivity(),
    ConnectionPage.Prsenter{

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var geoCor: Geocoder

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
//    fun start(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(connectionView)
        geoCor = Geocoder(applicationContext)

    }
    override fun getCoordinates() {
        var address: List<Address>
        println("override fun getCoordinates")
        if (connectionView.checkPermissions()) {
            println("Entro primer if")
            if (connectionView.isLocationEnabled()) {
                println("Entro segundo if")
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED) {
                    println("ENTRO AL IF")
                    fusedLocationClient.lastLocation.addOnCompleteListener(connectionView) { task ->
                        var location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            println("ENTRO AL ELSE antes de setear")
                            connectionView.printLatitude(location.latitude.toString())
                            connectionView.printLongitude(location.longitude.toString())
//                            lat.setText("LATITUD = " + location.latitude.toString())
//                            lon.setText("LONGITUD = " + location.longitude.toString())
                            address = geoCor.getFromLocation(location.latitude,location.longitude,1)
                            connectionView.printDirection(address.get(0).getAddressLine(0))
//                            localAddress.setText("Dirección: " + address.get(0).getAddressLine(0))
                        }
                    }
                } else {
                    println("NO ENTRO AL IF")
                }
            }
        }
        println("Salgo FALSE")

    }
//    private fun checkPermissions(): Boolean {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(connectionView, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//        ) {
//            return true
//        }
//        println("devuelvo false")
//        return false
//    }

//    private fun isLocationEnabled(): Boolean {
//        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }
    @SuppressLint("MissingPermission")
    fun requestNewLocationData(){
        var mLocationRequest = LocationRequest()
//            .create().apply{
//            interval = 100
//            fastestInterval = 50
//            priority = LocationRequest.PRIORITY_HIGH_ACCUARANCY
//            maxWaitTime = 100
//            numUpdates = 1
//        }
        mLocationRequest.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(connectionView)
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper())
    }

    private val mLocationCallBack = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var myLastLocation : Location = locationResult.lastLocation
            connectionView.printLatitude(myLastLocation.latitude.toString())
            connectionView.printLongitude(myLastLocation.longitude.toString())
//            lat.setText("LATITUD = " + mLastLocation.latitude.toString())
//            lon.setText("LONGITUD = " + mLastLocation.longitude.toString())
        }
    }
}