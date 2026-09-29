package com.rukinpavel.wordlyapp

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.rukinpavel.wordlyapp.core.platform.android.ActivityProvider
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WordlyApplication : Application() {
    @Inject
    lateinit var activityProvider: ActivityProvider

    override fun onCreate() {
        super.onCreate()
        try {
            MobileAds.initialize(this)
        } catch (_: Exception) {
        }
    }
}
