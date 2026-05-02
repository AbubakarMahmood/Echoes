package com.echoes.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.echoes.app.notifications.CapsuleUnlockNotifier

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CapsuleUnlockNotifier.createNotificationChannel(this)
        setContentView(R.layout.activity_main)
    }
}
