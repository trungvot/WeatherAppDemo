package com.trungvothanh.weatherapplication

import android.app.Application
import com.trungvothanh.weatherapplication.helpers.Constants
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm

import io.realm.RealmConfiguration

@HiltAndroidApp
class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name(Constants.REALM_DB_NAME)
            .allowWritesOnUiThread(true)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }
}