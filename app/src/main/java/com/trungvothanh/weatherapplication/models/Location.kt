package com.trungvothanh.weatherapplication.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Location : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var name: String = "_"
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var temp: Float = 0F
    var description: String = ""
}