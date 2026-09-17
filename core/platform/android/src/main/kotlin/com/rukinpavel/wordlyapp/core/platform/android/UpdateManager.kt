package com.rukinpavel.wordlyapp.core.platform.android

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityProvider: ActivityProvider,
) : DefaultLifecycleObserver {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
    private var updateResultLauncher: ActivityResultLauncher<IntentSenderRequest>? = null

    private val installStateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Log.d("UpdateManager", "Update downloaded, ready to install")
        }
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appUpdateManager.registerListener(installStateListener)
    }

    fun setupLauncher(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        this.updateResultLauncher = launcher
    }

    override fun onStart(owner: LifecycleOwner) {
        checkForUpdates()
    }

    override fun onResume(owner: LifecycleOwner) {
        resumeUpdate()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        appUpdateManager.unregisterListener(installStateListener)
        super.onDestroy(owner)
    }

    fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    private fun checkForUpdates() {
        val launcher = updateResultLauncher ?: return
        if (activityProvider.getActivity() == null) return

        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (updateInfo.isImmediateUpdateAllowed) {
                    appUpdateManager.startUpdateFlowForResult(
                        updateInfo,
                        launcher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                    )
                } else if (updateInfo.isFlexibleUpdateAllowed) {
                    appUpdateManager.startUpdateFlowForResult(
                        updateInfo,
                        launcher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                    )
                }
            }
        }.addOnFailureListener { e ->
            Log.e("UpdateManager", "Check for updates failed", e)
        }
    }

    private fun resumeUpdate() {
        val launcher = updateResultLauncher ?: return
        if (activityProvider.getActivity() == null) return

        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    updateInfo,
                    launcher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                )
            } else if (updateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                Log.d("UpdateManager", "Update is already downloaded")
            }
        }
    }
}
