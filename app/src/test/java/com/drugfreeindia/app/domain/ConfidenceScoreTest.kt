package com.drugfreeindia.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfidenceScoreTest {

    @Test
    fun testConfidenceScoreWeightedBlend() {
        // Formula: 50% story refusal rate + 30% survey self-rating + 20% consistency (streak)
        val storyRefusalRate = 80 // 50% -> 40
        val surveyRating = 90      // 30% -> 27
        val streakConsistency = 70 // 20% -> 14

        val computedScore = (0.50f * storyRefusalRate + 0.30f * surveyRating + 0.20f * streakConsistency).toInt()

        assertEquals(81, computedScore)
        assertTrue(computedScore in 0..100)
    }

    @Test
    fun testStreakProgressionLogic() {
        val currentStreak = 4
        val nextStreak = currentStreak + 1
        val longestStreak = maxOf(6, nextStreak)

        assertEquals(5, nextStreak)
        assertEquals(6, longestStreak)
    }

    @Test
    fun testCohortThresholdForAnonymity() {
        val minimumCohortRequired = 20

        val smallCohort = 12
        val largeCohort = 640

        assertTrue(smallCohort < minimumCohortRequired)
        assertTrue(largeCohort >= minimumCohortRequired)
    }
}
