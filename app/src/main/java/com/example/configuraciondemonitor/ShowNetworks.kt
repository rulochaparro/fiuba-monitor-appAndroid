package com.example.configuraciondemonitor

import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class ShowNetworks : AppCompatActivity(){

    lateinit var networks:ScannedNetworks

    var pageIndex: Int = 0

    lateinit var option1Btn: Button
    lateinit var option2Btn: Button
    lateinit var option3Btn: Button
    lateinit var option4Btn: Button
    lateinit var option5Btn: Button
    lateinit var option6Btn: Button
    lateinit var buttons: Array<Button>

    companion object{
        lateinit var json :String
        val selectedNetwork:String = "selectedNetwork"
        var mybluetoothSocket: BluetoothSocket? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_networks)

        val gson = Gson()
        json = intent.getStringExtra(ConnectionPageView.networks).toString()

        networks = gson.fromJson(json, ScannedNetworks::class.java)
        Log.d("Parseo:", networks.quantity.toString())
        for (x: Int in 0 until networks.quantity) {
            networks.networks?.get(x)?.ssid?.let { Log.d("ssid: ", it) }
        }

        option1Btn = findViewById(R.id.option1)
        option2Btn = findViewById(R.id.option2)
        option3Btn = findViewById(R.id.option3)
        option4Btn = findViewById(R.id.option4)
        option5Btn = findViewById(R.id.option5)
        option6Btn = findViewById(R.id.option6)
        buttons = arrayOf(option1Btn,option2Btn,option3Btn,option4Btn,option5Btn,option6Btn)

        showSSID(pageIndex,networks.quantity)
    }

    fun showSSID( index:Int, lastNetwork: Int){

        for(x: Int in 0 until 6){
            if(x + 6 * index < lastNetwork ) {
                buttons.get(x).visibility = View.VISIBLE
                buttons.get(x).setText(networks.networks?.get(x + 6 * index)?.ssid)
            }
            else{
                buttons.get(x).visibility = View.INVISIBLE
            }
        }
    }
    fun nextPage(view: View){

        if(networks.quantity < ++pageIndex * networks.quantity){
            pageIndex--
        }
        showSSID(pageIndex, networks.quantity)
    }

    fun previousPage(view: View){
        if(--pageIndex < 0){
            pageIndex++
        }
        showSSID(pageIndex, networks.quantity)
    }

    fun inputPassword(view: View){
        var iterator : Int = 0
        for (iterator in 0 until networks.quantity) {
            if(buttons.get(iterator).bottom == view.bottom){
                println("ssid: " + buttons.get(iterator).text)
                break
            }
        }
        val intent  = Intent(this, PasswordPage::class.java)
        intent.putExtra(selectedNetwork, buttons.get(iterator).text)
        startActivity(intent)
    }
}