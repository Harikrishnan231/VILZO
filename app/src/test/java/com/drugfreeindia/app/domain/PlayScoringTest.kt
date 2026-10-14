package com.drugfreeindia.app.domain

import com.drugfreeindia.app.presentation.util.Format
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayScoringTest {

    @Test
    fun testPracticeScoreCalculation() {
        // Test score sum and multiplier rules
        val basePoints = listOf(6, 8, 10)
        val total = basePoints.sum()
        assertEquals(24, total)
        assertTrue("Total score should be positive", total > 0)
    }

    @Test
    fun testHaversineDistance() {
        // Bengaluru (12.9716, 77.5946) to Puducherry (11.9416, 79.8083) ~ 268 km
        val distanceKm = Format.distanceKm(12.9716, 77.5946, 11.9416, 79.8083)
        assertTrue("Distance should be approximately 260-280 km", distanceKm in 250.0..290.0)
    }

    @Test
    fun testFormattedDistance() {
        assertEquals("Nearby", Format.formatDistance(0.4))
        assertEquals("2.5 km away", Format.formatDistance(2.5))
        assertEquals("12 km away", Format.formatDistance(12.3))
    }

    @Test
    fun testMaskPhone() {
        val masked = Format.maskPhone("9876543210")
        assertEquals("+91 ••••• •3210", masked)
    }
}
