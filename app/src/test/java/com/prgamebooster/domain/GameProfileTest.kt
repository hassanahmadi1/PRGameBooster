package com.prgamebooster.domain

import com.prgamebooster.domain.model.GameId
import com.prgamebooster.domain.model.GameProfile
import org.junit.Assert.*
import org.junit.Test

class GameProfileTest {

    @Test
    fun `default PUBG profile has correct package and fps`() {
        val profile = GameProfile.default(GameId.PUBG_MOBILE)
        assertEquals("com.tencent.ig", profile.packageName)
        assertEquals(90, profile.targetFps)
    }

    @Test
    fun `default Free Fire profile has correct package and fps`() {
        val profile = GameProfile.default(GameId.FREE_FIRE)
        assertEquals("com.dts.freefireth", profile.packageName)
        assertEquals(60, profile.targetFps)
    }

    @Test
    fun `default COD Mobile profile has correct package and fps`() {
        val profile = GameProfile.default(GameId.COD_MOBILE)
        assertEquals("com.activision.callofduty.shooter", profile.packageName)
        assertEquals(120, profile.targetFps)
    }

    @Test
    fun `each game profile is independent`() {
        val pubg = GameProfile.default(GameId.PUBG_MOBILE)
        val codm = GameProfile.default(GameId.COD_MOBILE)
        assertNotEquals(pubg.targetFps, codm.targetFps)
        assertNotEquals(pubg.antiLagEnabled, codm.antiLagEnabled)
    }
}
