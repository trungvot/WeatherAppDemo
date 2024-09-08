package com.trungvothanh.weatherapplication.network

import android.content.Context
import android.location.Geocoder
import java.lang.Exception
import java.util.*

class GoogleMaps(val context: Context) {

    private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())


    //get lat, long from address
    fun geocode(address: String): MutableList<Any>? {

        try {
            //getting list of addresses that was found, limit is set to 1 => it is returning max 1 address
            val addresses = this.geocoder.getFromLocationName(address, 1)

            //check if at least one address has been found
            if (addresses != null && addresses.size > 0) {

                // getting lat, long
                val lat: Double = addresses.get(0).latitude // example: -33.868819699999996
                val long: Double = addresses.get(0).longitude // example: 151.2092955
                val locality: String = addresses.get(0).locality // example: Sydney

                //wrap lat, long in mutable array
                val location = mutableListOf<Any>()
                location.add(0, lat)
                location.add(1, long)
                location.add(2, locality)


                return location
            }

            return null

        } catch (e: Exception) {
            return null
        }

    }

    //get locality name from lat, long
    fun reverseGeocode(lat: Double, long: Double): String? {

        try {
            //getting list of addresses that was found, limit is set to 1 => it is returning max 1 address
            val addresses = this.geocoder.getFromLocation(lat, long, 1)

            //check if at least one address has been found
            if (addresses != null && addresses.size > 0) {

                //getting locality name
                var locality: String? = addresses[0].locality

                //check if locality is not null, we want to always return something.
                //if locality is null, try to get country name, if country name is also null return unknown location
                if (locality == null) {
                    val countryName = addresses[0].countryName
                    if (countryName != null) {
                        locality = countryName
                    } else {
                        locality = "Unknown"
                    }
                }

                return locality
            }

            return "Unknown"

        } catch (e: Exception) {
            return "Unknown"
        }

    }


}