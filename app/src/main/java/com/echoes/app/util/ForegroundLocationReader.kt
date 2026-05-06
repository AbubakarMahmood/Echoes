package com.echoes.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

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

    fun isLocationServiceEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            @Suppress("DEPRECATION")
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    suspend fun currentBestLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null
        if (!isLocationServiceEnabled(context)) return null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null
        val appContext = context.applicationContext
        val providers = locationManager.getProviders(true).sortedByProviderQuality()

        val lastKnownLocation = providers
            .mapNotNull { provider -> locationManager.safeLastKnownLocation(provider) }
            .maxByOrNull { it.time }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return lastKnownLocation
        }

        for (provider in providers) {
            val freshLocation = withTimeoutOrNull(FRESH_LOCATION_TIMEOUT_MS) {
                locationManager.safeCurrentLocation(appContext, provider)
            }
            if (freshLocation != null) return freshLocation
        }

        return lastKnownLocation
    }

    @Suppress("MissingPermission")
    private fun LocationManager.safeLastKnownLocation(provider: String): Location? {
        return runCatching {
            getLastKnownLocation(provider)
        }.getOrNull()
    }

    @Suppress("MissingPermission")
    private suspend fun LocationManager.safeCurrentLocation(
        context: Context,
        provider: String
    ): Location? {
        return suspendCancellableCoroutine { continuation ->
            val cancellationSignal = CancellationSignal()
            continuation.invokeOnCancellation {
                cancellationSignal.cancel()
            }

            runCatching {
                getCurrentLocation(
                    provider,
                    cancellationSignal,
                    ContextCompat.getMainExecutor(context)
                ) { location ->
                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                }
            }.onFailure {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }

    private fun List<String>.sortedByProviderQuality(): List<String> {
        return sortedBy { provider ->
            when (provider) {
                LocationManager.GPS_PROVIDER -> 0
                LocationManager.NETWORK_PROVIDER -> 1
                else -> 2
            }
        }
    }

    private const val FRESH_LOCATION_TIMEOUT_MS = 7_000L
}
