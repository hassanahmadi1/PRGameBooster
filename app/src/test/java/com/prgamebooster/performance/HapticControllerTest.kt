package com.prgamebooster.performance

import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * تست‌های واحد این کلاس به دلیل وابستگی به android.os.Vibrator (نیازمند Robolectric
 * یا Instrumented Test) در محیط Unit Test خالص محدود هستند؛ این فایل ساختار پایه
 * برای گسترش تست با Robolectric را نشان می‌دهد.
 */
class HapticControllerTest {

    @Test
    fun `haptic controller class exists and is loadable`() {
        assertNotNull(HapticController::class.java)
    }
}
