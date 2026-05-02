package com.echoes.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat

object ForegroundLocationReader {

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun currentBestLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        return locationManager.getProviders(true)
            .mapNotNull { provider -> locationManager.safeLastKnownLocation(provider) }
            .maxByOrNull { it.time }
    }

    @Suppress("MissingPermission")
    private fun LocationManager.safeLastKnownLocation(provider: String): Location? {
        return runCatching {
            getLastKnownLocation(provider)
        }.getOrNull()
    }
}
