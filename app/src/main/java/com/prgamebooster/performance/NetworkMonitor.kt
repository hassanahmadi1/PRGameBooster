package com.prgamebooster.performance

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.TelephonyNetworkSpecifier
import android.os.Build
import android.telephony.TelephonyManager
import com.prgamebooster.domain.model.NetworkState
import com.prgamebooster.domain.model.NetworkType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * منبع واقعی وضعیت شبکه با ConnectivityManager.NetworkCallback رسمی اندروید.
 * نوع اتصال از NetworkCapabilities استخراج می‌شود، نه حدس یا مقدار ثابت.
 */
@Singleton
class NetworkMonitor @Inject constructor(
    private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observeState(): Flow<NetworkState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(mapCapabilitiesToState(capabilities))
            }

            override fun onLost(network: Network) {
                trySend(NetworkState.Disconnected)
            }

            override fun onUnavailable() {
                trySend(NetworkState.Disconnected)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        // مقدار اولیه واقعی از شبکه فعلی
        val activeNetwork = connectivityManager.activeNetwork
        val activeCaps = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        trySend(if (activeCaps != null) mapCapabilitiesToState(activeCaps) else NetworkState.Disconnected)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }

    /**
     * سرعت لینک واقعی (Mbps) طبق NetworkCapabilities.getLinkDownstreamBandwidthKbps().
     * این مقدار توسط سیستم‌عامل تخمین زده می‌شود و مستقیماً از رادیو خوانده می‌شود؛
     * اگر سیستم مقداری ندهد null برمی‌گردد (هرگز عدد جعلی نه).
     */
    fun currentDownstreamMbps(): Double? {
        val network = connectivityManager.activeNetwork ?: return null
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return null
        val kbps = caps.linkDownstreamBandwidthKbps
        if (kbps <= 0) return null
        return kbps / 1000.0
    }

    private fun mapCapabilitiesToState(capabilities: NetworkCapabilities): NetworkState {
        val validated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        val type = when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> resolveCellularGeneration()
            else -> NetworkType.UNKNOWN
        }

        return NetworkState.Connected(type = type, isValidated = validated)
    }

    /**
     * تشخیص نسل شبکه سلولار (4G/5G) با TelephonyManager.getDataNetworkType().
     * روی اندروید 12+ برای 5G نیازمند بررسی NetworkRegistrationInfo هم هست؛
     * در صورت عدم دسترسی به نتیجه دقیق، CELLULAR_OTHER برگردانده می‌شود، نه حدس.
     */
    private fun resolveCellularGeneration(): NetworkType {
        return try {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                    ?: return NetworkType.CELLULAR_OTHER

            @Suppress("MissingPermission")
            val dataNetworkType = telephonyManager.dataNetworkType
            when (dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_NR -> NetworkType.CELLULAR_5G
                TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.CELLULAR_4G
                else -> NetworkType.CELLULAR_OTHER
            }
        } catch (securityException: SecurityException) {
            // بدون READ_PHONE_STATE فقط می‌گوییم سلولار است؛ چیزی جعل نمی‌کنیم
            NetworkType.CELLULAR_OTHER
        }
    }
}
