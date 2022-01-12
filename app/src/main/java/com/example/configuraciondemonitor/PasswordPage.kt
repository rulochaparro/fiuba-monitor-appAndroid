package com.example.configuraciondemonitor

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.gson.Gson

class PasswordPage : AppCompatActivity() {

    lateinit var inputPassword: EditText
    lateinit var getGPSBtn: Button
    lateinit var lon: TextView
    lateinit var lat: TextView
    lateinit var localAddress: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var geoCor: Geocoder
    lateinit var sendMessageBtn: Button
    var latitude : String = ""
    var longitude : String = ""

    companion object {
        var selectedNetwork: String = "Device_address"
        var mybluetoothSocket: BluetoothSocket? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passwrod_page)

        inputPassword = findViewById(R.id.inputPass)
        getGPSBtn = findViewById(R.id.getGPS)
        lon = findViewById(R.id.lon)
        lat = findViewById(R.id.lat)
        localAddress = findViewById(R.id.dir)
        sendMessageBtn = findViewById(R.id.sendMessage)

        selectedNetwork = intent.getStringExtra(ShowNetworks.selectedNetwork).toString()
        mybluetoothSocket = ConnectionPageView.mybluetoothSocket
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geoCor = Geocoder(applicationContext)

        getGPSBtn.setOnClickListener { getCoordinates() }
        sendMessageBtn.setOnClickListener { sendMessage() }
    }

    fun clear(view: View){
        inputPassword.setText("")
    }

    fun getCoordinates() {

        var address: List<Address>
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                        var location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            printLatitude(location.latitude.toString())
                            printLongitude(location.longitude.toString())
                            address = geoCor.getFromLocation(location.latitude,location.longitude,1)
                            printDirection(address.get(0).getAddressLine(0))
                        }
                    }
                }
            }
        }
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

            return true
        }
        println("devuelvo false")
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        return true
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(){
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = android.location.LocationRequest.QUALITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper())
    }

    private val mLocationCallBack = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var myLastLocation : Location = locationResult.lastLocation
            printLatitude(myLastLocation.latitude.toString())
            printLongitude(myLastLocation.longitude.toString())
        }
    }
    private fun printLatitude(lat: String) {
        latitude = lat
        this.lat.setText("Latitud: " + lat)
    }

    private fun printLongitude(lon: String) {
        longitude = lon
        this.lon.setText("Longitud: " + lon)
    }
    private fun printDirection(dir: String) {
        this.localAddress.setText("Dirección: " + dir)
    }

    fun sendMessage() {
        var connectionPagePresenter = ConnectionPagePresenter()
        var message:String = "{\"ssid\" : \"" + selectedNetwork+ "\", \"password\" : \"" + inputPassword.text + "\", \"lon\" : \"" + longitude + "\",\"lat\" : \"" + latitude +  "\"}"
        println(message)
        mybluetoothSocket?.let { connectionPagePresenter.sendCommand(message, it) }
    }
}