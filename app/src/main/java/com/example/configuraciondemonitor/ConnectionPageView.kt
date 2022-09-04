package com.example.configuraciondemonitor

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*

class ConnectionPageView: AppCompatActivity(){

    lateinit var getNetworksBtn: Button
    lateinit var getGPSBtn: Button
    lateinit var showNetworksBtn: Button
    lateinit var lon: TextView
    lateinit var lat: TextView
    lateinit var localAddress: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var geoCor: Geocoder
    lateinit var connectionPagePresenter: ConnectionPagePresenter
    lateinit var networksAux:String

    companion object {
        var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var mybluetoothSocket: BluetoothSocket? = null
        lateinit var myProgress: ProgressDialog
        lateinit var myBluetoothAdapter: BluetoothAdapter
        var mIsConnected: Boolean = false
        lateinit var myAddress: String
        val networks:String =""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection_page)
        myAddress = intent.getStringExtra(BTPageView.extraAddress).toString()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geoCor = Geocoder(applicationContext)

        connectionPagePresenter = ConnectionPagePresenter()

        getNetworksBtn = findViewById(R.id.getNetworks)
        getGPSBtn = findViewById(R.id.getGPS)
        showNetworksBtn = findViewById(R.id.showNetworks)
        lon = findViewById(R.id.lon)
        lat = findViewById(R.id.lat)
        localAddress = findViewById(R.id.dir)


        ConnectToDevice(this).execute()

        getNetworksBtn.setOnClickListener { getNetworks() }
        getGPSBtn.setOnClickListener { getCoordinates() }
        showNetworksBtn.setOnClickListener { getCoordinates() }
    }

    fun getNetworks(){
        mybluetoothSocket?.let { connectionPagePresenter.sendCommand("a", it) }
        mybluetoothSocket?.let { networksAux = connectionPagePresenter.receiveCommand( it) }
        println("networks: " + networksAux)

        val intent  = Intent(this, ShowNetworks::class.java)
        intent.putExtra(networks, networksAux)
        startActivity(intent)

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
    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            myProgress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (mybluetoothSocket == null || !mIsConnected) {
                    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = myBluetoothAdapter.getRemoteDevice(myAddress)
                    mybluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    mybluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                mIsConnected = true
            }
            myProgress.dismiss()
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
        this.lat.setText("Latitud: " + lat)
    }

    private fun printLongitude(lon: String) {
        this.lon.setText("Longitud: " + lon)
    }
    private fun printDirection(dir: String) {
        this.localAddress.setText("Dirección: " + dir)
    }

}