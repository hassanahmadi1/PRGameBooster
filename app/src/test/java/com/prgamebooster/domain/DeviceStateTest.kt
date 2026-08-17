package com.prgamebooster.domain

import com.prgamebooster.domain.model.BatteryState
import com.prgamebooster.domain.model.LatencyQuality
import com.prgamebooster.domain.model.LatencyResult
import com.prgamebooster.domain.model.NetworkState
import com.prgamebooster.domain.model.NetworkType
import org.junit.Assert.*
import org.junit.Test

class DeviceStateTest {

    @Test
    fun `battery available state carries real percentage and charging flag`() {
        val state = BatteryState.Available(percentage = 87, isCharging = true)
        assertEquals(87, state.percentage)
        assertTrue(state.isCharging)
    }

    @Test
    fun `battery unavailable state carries no fake numbers`() {
        val state: BatteryState = BatteryState.Unavailable
        assertTrue(state is BatteryState.Unavailable)
    }

    @Test
    fun `network connected state exposes real type`() {
        val state = NetworkState.Connected(type = NetworkType.WIFI, isValidated = true)
        assertEquals(NetworkType.WIFI, state.type)
    }

    @Test
    fun `latency measured result never negative`() {
        val result = LatencyResult.Measured(millis = 32, quality = LatencyQuality.EXCELLENT)
        assertTrue(result.millis >= 0)
    }
}
