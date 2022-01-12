package com.example.configuraciondemonitor

import android.bluetooth.BluetoothSocket
import java.io.IOException

class ConnectionPagePresenter: Thread() {

    fun sendCommand(input: String, myBtSocket: BluetoothSocket) {
        if (myBtSocket != null) {
            try{
                myBtSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun receiveCommand( myBtnSocket: BluetoothSocket): String {
        var numbOfBytes = 0
        var aux : String
        var networks = String()
        var input = ByteArray(2014)
        if (myBtnSocket != null) {
            try {
                numbOfBytes = myBtnSocket!!.inputStream.read(input)
                println("numbOfBytes: " + numbOfBytes)
                aux = input.toString(Charsets.UTF_8)
                networks =  aux.substring(0,numbOfBytes)

            } catch (e: IOException) {
                e.printStackTrace()

            }
        }
        return networks
    }

}


