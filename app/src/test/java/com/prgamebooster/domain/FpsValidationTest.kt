package com.prgamebooster.domain

import com.prgamebooster.domain.model.GameProfile
import org.junit.Assert.*
import org.junit.Test

class FpsValidationTest {

    @Test
    fun `allowed fps values are accepted`() {
        listOf(30, 40, 45, 60, 90, 120, 144).forEach { fps ->
            assertTrue("$fps باید مجاز باشد", GameProfile.isValidFps(fps))
        }
    }

    @Test
    fun `arbitrary fps values are rejected`() {
        listOf(0, 15, 61, 100, 200, -30).forEach { fps ->
            assertFalse("$fps نباید مجاز باشد", GameProfile.isValidFps(fps))
        }
    }
}
