package com.rukinpavel.wordlyapp

import android.app.Application
import com.rukinpavel.wordlyapp.core.platform.android.ActivityProvider
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WordlyApplication : Application() {
    @Inject
    lateinit var activityProvider: ActivityProvider
}
