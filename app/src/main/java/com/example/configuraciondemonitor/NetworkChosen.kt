package com.example.configuraciondemonitor


class NetworkChosen {

    var ssid:String
    var password:String
    var lon: Double = 0.0
    var lat: Double = 0.0

    constructor(ssid:String, password: String, lon:Double, lat:Double){

        this.ssid = ssid
        this.password = password
        this.lon = lon
        this.lat = lat
    }
}