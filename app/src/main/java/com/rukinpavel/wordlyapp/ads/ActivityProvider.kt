package com.rukinpavel.wordlyapp.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityProvider @Inject constructor(application: Application) : Application.ActivityLifecycleCallbacks {
    private var currentActivity: WeakReference<Activity>? = null

    init {
        Log.d("ActivityProvider", "Initializing and registering callbacks")
        application.registerActivityLifecycleCallbacks(this)
    }

    fun getActivity(): Activity? {
        val activity = currentActivity?.get()
        Log.d("ActivityProvider", "Get activity called, returning: $activity")
        return activity
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d("ActivityProvider", "onActivityCreated: $activity")
        currentActivity = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d("ActivityProvider", "onActivityStarted: $activity")
        currentActivity = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d("ActivityProvider", "onActivityResumed: $activity")
        currentActivity = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity?.get() == activity) {
            currentActivity = null
        }
    }
}
