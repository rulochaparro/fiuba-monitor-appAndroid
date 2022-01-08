package com.example.configuraciondemonitor

interface ConnectionPage {

    interface View{
        fun printLatitude(lat: String)
        fun printLongitude(lon: String)
        fun printDirection(dir: String)
    }

    interface Prsenter{
        fun getCoordinates()
    }
}