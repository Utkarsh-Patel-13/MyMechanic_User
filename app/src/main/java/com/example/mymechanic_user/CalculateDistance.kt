package com.example.mymechanic_user

import com.google.android.gms.maps.model.LatLng
import java.lang.Math.cos
import java.lang.Math.sin
import kotlin.math.acos

fun calcDistance(latLng1: LatLng, latLng2: LatLng): Double{

    var distance: Double

    var rlat1 = latLng1.latitude/57.29577951
    var rlat2 = latLng2.latitude/57.29577951
    var rlng1 = latLng1.longitude/57.29577951
    var rlng2 = latLng1.longitude/57.29577951

    distance = 3963.0 * acos((sin(rlat1) * sin(rlat2)) + cos(rlat1) * cos(rlat2) * cos(rlng2 - rlng1))

    distance = distance * 1.609344

    return distance
}