package com.echoes.app.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.echoes.app.MainActivity
import com.echoes.app.R

object CapsuleUnlockNotifier {

    const val ACTION_UNLOCK_NOTIFICATION = "com.echoes.app.action.UNLOCK_NOTIFICATION"
    const val EXTRA_CAPSULE_ID = "extra_capsule_id"
    const val EXTRA_CAPSULE_TITLE = "extra_capsule_title"

    private const val CHANNEL_ID = "capsule_unlocks"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.unlock_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.unlock_notification_channel_description)
        }

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun scheduleUnlockNotification(
        context: Context,
        capsuleId: String,
        title: String,
        unlockAt: Long
    ) {
        if (unlockAt <= System.currentTimeMillis()) return

        createNotificationChannel(context)
        val pendingIntent = unlockPendingIntent(
            context = context,
            capsuleId = capsuleId,
            title = title,
            flags = PendingIntent.FLAG_UPDATE_CURRENT
        ) ?: return
        context.getSystemService(AlarmManager::class.java).set(
            AlarmManager.RTC_WAKEUP,
            unlockAt,
            pendingIntent
        )
    }

    fun cancelUnlockNotification(context: Context, capsuleId: String) {
        val pendingIntent = unlockPendingIntent(
            context = context,
            capsuleId = capsuleId,
            title = "",
            flags = PendingIntent.FLAG_NO_CREATE
        ) ?: return

        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent)
        pendingIntent.cancel()
    }

    @SuppressLint("MissingPermission")
    fun showUnlockNotification(context: Context, capsuleId: String, title: String) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannel(context)
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            capsuleId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val body = context.getString(R.string.unlock_notification_body, title)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_echoes_launcher)
            .setContentTitle(context.getString(R.string.unlock_notification_title))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(openAppPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(capsuleId.hashCode(), notification)
    }

    private fun unlockPendingIntent(
        context: Context,
        capsuleId: String,
        title: String,
        flags: Int
    ): PendingIntent? {
        val intent = Intent(context, CapsuleUnlockNotificationReceiver::class.java).apply {
            action = ACTION_UNLOCK_NOTIFICATION
            putExtra(EXTRA_CAPSULE_ID, capsuleId)
            putExtra(EXTRA_CAPSULE_TITLE, title)
        }

        return PendingIntent.getBroadcast(
            context,
            capsuleId.hashCode(),
            intent,
            flags or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
