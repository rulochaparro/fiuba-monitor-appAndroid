package com.example.configuraciondemonitor.connectionpage

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.configuraciondemonitor.R
import com.example.configuraciondemonitor.btpage.BTPageView
import java.io.IOException
import java.util.*

class ConnectionPageView: AppCompatActivity() {

    lateinit var getNetworksBtn: Button
    lateinit var getNetworksBtn: Button
    lateinit var getNetworksBtn: Button


    companion object {
        var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var mybluetoothSocket: BluetoothSocket? = null
        lateinit var myProgress: ProgressDialog
        lateinit var myBluetoothAdapter: BluetoothAdapter
        var mIsConnected: Boolean = false
        lateinit var myAddress: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection_page)
        myAddress = intent.getStringExtra(BTPageView.extraAddress).toString()
        ConnectToDevice(this).execute()

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
}