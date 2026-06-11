package com.yogesh.auraai.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsTest {

    @Test
    fun truncate_shortensLongText() {
        assertEquals("Hello…", "Hello world".truncate(6))
    }

    @Test
    fun truncate_leavesShortTextUnchanged() {
        assertEquals("Hi", "Hi".truncate(10))
    }
}
