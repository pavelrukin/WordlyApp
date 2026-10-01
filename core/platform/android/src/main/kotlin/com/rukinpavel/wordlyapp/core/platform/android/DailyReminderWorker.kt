package com.rukinpavel.wordlyapp.core.platform.android

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

class DailyReminderWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            DailyReminderWorkerEntryPoint::class.java,
        )
        val appPreferences = entryPoint.appPreferencesRepository()

        val notificationsEnabled = appPreferences.notificationsEnabled.first()
        if (!notificationsEnabled) {
            return Result.success()
        }

        NotificationHelper.createNotificationChannel(appContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.success()
            }
        }

        val launchIntent = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = if (launchIntent != null) {
            PendingIntent.getActivity(
                appContext,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        } else {
            null
        }

        val title = appContext.getString(CoreUiR.string.notification_title)
        val message = appContext.getString(CoreUiR.string.notification_message)

        val iconRes = appContext.applicationInfo.icon.let { if (it != 0) it else R.drawable.sym_def_app_icon }

        val builder = NotificationCompat.Builder(appContext, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(appContext).notify(NotificationHelper.NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // Permission might have been revoked
        }

        return Result.success()
    }
}
