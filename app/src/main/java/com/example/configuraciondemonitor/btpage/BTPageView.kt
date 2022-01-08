package com.example.configuraciondemonitor.btpage

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.configuraciondemonitor.R
import com.example.configuraciondemonitor.connectionpage.ConnectionPageView

class BTPageView : AppCompatActivity() {

    lateinit var searchBtn: Button
    lateinit var refreshBtn: Button
    lateinit var stopSearchBtn: Button
    lateinit var pairedDevicesBtn: Button
    lateinit var devicesList: ListView

    val devicesScanned : ArrayList<BluetoothDevice> = ArrayList()
    val devicesScannedName: ArrayList<String> = ArrayList()

    private var btAdapter: BluetoothAdapter? = null
    private lateinit var pairedDevices: Set<BluetoothDevice>

    companion object {
        val extraAddress: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bt_page)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = bluetoothManager.getAdapter()

        searchBtn = findViewById(R.id.search_BT_devices)
        refreshBtn = findViewById(R.id.update_BT_devices)
        stopSearchBtn = findViewById(R.id.stop_BT_devices)
        pairedDevicesBtn = findViewById(R.id.show_BT_paired_devices)

        devicesList = findViewById(R.id.select_device_list)

        btAdapter = bluetoothManager.getAdapter()

        if (btAdapter == null) {
            this.showToastMessage("Este dispositivo no soporta bluetooth")
            return
        }

        if (!btAdapter!!.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            responseLauncher.launch(enableBTIntent)
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        searchBtn.setOnClickListener { searchDevices() }
        refreshBtn.setOnClickListener { refreshDevices() }
        stopSearchBtn.setOnClickListener { stopSearch() }
        pairedDevicesBtn.setOnClickListener { showPairedDevices() }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    if (devicesScannedName.contains(deviceName.toString()) == false) {
                        devicesScannedName.add(deviceName.toString())
                        devicesScanned.add(device!!)
                        if (deviceName != null) {
                            Log.d("deviceName: " , deviceName)
                        }
                    }
                }
            }
        }
    }

    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                if (btAdapter!!.isEnabled) {
                    this.showToastMessage("Bluettoth habilitado")
                } else {
                    this.showToastMessage("Bluetooth deshabilitado")
                }
            }
        }

    private fun searchDevices() {
        showToastMessage("Aprete searchDevices")
    }

    private fun refreshDevices() {
        showToastMessage("Aprete refreshDevices")
    }

    private fun stopSearch() {
        showToastMessage("Aprete stopSearch")
    }

    private fun showPairedDevices() {
        devicesScannedName.clear()
        devicesScanned.clear()
        pairedDevices = btAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()
        val list2: ArrayList<String> = ArrayList()
        if (!pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in pairedDevices) {
                list.add(device)
                list2.add(device.name)
                Log.d("device", device.name)
            }
        } else {
            this.showToastMessage("No se encontraron dispositivos bluetooth")
//            Toast.makeText(this, "No se encontraron dispositivos bluetooth", Toast.LENGTH_LONG).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list2)
        devicesList.adapter = adapter
        devicesList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this, ConnectionPageView::class.java)
            intent.putExtra(extraAddress, address)
            startActivity(intent)
        }

    }

    fun showToastMessage(message: String) {
        Toast.makeText( this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }
}