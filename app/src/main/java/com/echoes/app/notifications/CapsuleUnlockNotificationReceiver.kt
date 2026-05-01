package com.echoes.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CapsuleUnlockNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != CapsuleUnlockNotifier.ACTION_UNLOCK_NOTIFICATION) return

        val capsuleId = intent.getStringExtra(CapsuleUnlockNotifier.EXTRA_CAPSULE_ID).orEmpty()
        val title = intent.getStringExtra(CapsuleUnlockNotifier.EXTRA_CAPSULE_TITLE).orEmpty()
        if (capsuleId.isBlank() || title.isBlank()) return

        CapsuleUnlockNotifier.showUnlockNotification(context, capsuleId, title)
    }
}
