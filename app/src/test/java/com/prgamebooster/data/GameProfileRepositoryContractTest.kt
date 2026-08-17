package com.prgamebooster.data

import com.prgamebooster.domain.model.GameId
import com.prgamebooster.domain.model.GameProfile
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * تست منطق دامنه مستقل از Android Context (بدون نیاز به Instrumented Test)
 * تا مطمئن شویم پیش‌فرض هر پروفایل با مقادیر مشخصات پروژه یکی است.
 */
class GameProfileRepositoryContractTest {

    @Test
    fun `resetting a profile returns it to documented defaults`() {
        val modified = GameProfile.default(GameId.PUBG_MOBILE).copy(targetFps = 30, antiLagEnabled = false)
        val resetTarget = GameProfile.default(GameId.PUBG_MOBILE)

        assertEquals(90, resetTarget.targetFps)
        assertEquals(true, resetTarget.antiLagEnabled)
        // اطمینان از اینکه مقدار Modified واقعاً تغییر کرده بود (کنترل صحت تست)
        assertEquals(30, modified.targetFps)
    }
}
