package com.echoes.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.echoes.app.notifications.CapsuleUnlockNotifier
import com.echoes.app.ui.archive.CapsuleDetailFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CapsuleUnlockNotifier.createNotificationChannel(this)
        setContentView(R.layout.activity_main)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action != CapsuleUnlockNotifier.ACTION_OPEN_CAPSULE) return

        val capsuleId = intent.getStringExtra(CapsuleUnlockNotifier.EXTRA_CAPSULE_ID)
            ?.takeIf { it.isNotBlank() }
            ?: return
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            ?: return
        navHostFragment.navController.navigate(
            R.id.capsuleDetailFragment,
            bundleOf(CapsuleDetailFragment.ARG_CAPSULE_ID to capsuleId)
        )
    }
}
