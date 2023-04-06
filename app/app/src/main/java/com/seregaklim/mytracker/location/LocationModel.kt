package com.seregaklim.mytracker.location

import org.osmdroid.util.GeoPoint
import java.io.Serializable

data class LocationModel(
    //cорость
    val velocity: Float = 0.0f,
    val distance: Float = 0.0f,
    val geoPointList: ArrayList<GeoPoint>
    //превращаем в байты
    ):Serializable
