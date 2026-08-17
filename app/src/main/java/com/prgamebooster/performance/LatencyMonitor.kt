package com.prgamebooster.performance

import com.prgamebooster.domain.model.LatencyQuality
import com.prgamebooster.domain.model.LatencyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

/**
 * تست تأخیر واقعی: اتصال TCP به یک سرور DNS عمومی و اندازه‌گیری زمان واقعی
 * handshake با System.nanoTime(). هیچ مقدار Random یا Math.random() استفاده نمی‌شود.
 *
 * چون ICMP Ping در اندروید بدون دسترسی root/native عملاً در دسترس اپ‌های معمولی
 * نیست، معادل صادقانه‌ی آن اندازه‌گیری واقعی TCP connect-time است که کاملاً واقعی
 * و قابل اندازه‌گیری است، نه شبیه‌سازی.
 */
@Singleton
class LatencyMonitor @Inject constructor() {

    suspend fun measureOnce(
        host: String = "8.8.8.8",
        port: Int = 53,
        timeoutMillis: Int = 3000
    ): LatencyResult = withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            val started = System.nanoTime()
            socket.connect(InetSocketAddress(host, port), timeoutMillis)
            val elapsedMillis = (System.nanoTime() - started) / 1_000_000
            socket.close()

            LatencyResult.Measured(
                millis = elapsedMillis,
                quality = classify(elapsedMillis)
            )
        } catch (exception: Exception) {
            LatencyResult.Unavailable
        }
    }

    private fun classify(millis: Long): LatencyQuality = when {
        millis < 40 -> LatencyQuality.EXCELLENT
        millis < 90 -> LatencyQuality.GOOD
        millis < 180 -> LatencyQuality.MEDIUM
        else -> LatencyQuality.POOR
    }
}
