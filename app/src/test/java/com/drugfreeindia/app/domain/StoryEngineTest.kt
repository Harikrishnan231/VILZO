package com.drugfreeindia.app.domain

import com.drugfreeindia.app.domain.model.Choice
import com.drugfreeindia.app.domain.model.Scene
import com.drugfreeindia.app.domain.model.Story
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class StoryEngineTest {

    @Test
    fun testFarewellPartyBranchTransitions() {
        val scene1 = Scene(
            id = "s1",
            speaker = "Rohan",
            lines = listOf("Come on, try it once!"),
            timerSeconds = 10,
            pressure = 0.6f,
            choices = listOf(
                Choice("a", "Okay, just once…", 0, "gave_in", "Peer pressure note", "s2a"),
                Choice("b", "No thanks, I'm good.", 6, "calm_no", "Calm refusal note", "s2b"),
                Choice("c", "Let me get some fresh air.", 5, "exit_plan", "Exit plan note", "s2c")
            )
        )

        val story = Story(
            id = "farewell_party",
            title = "The Farewell Party",
            pack = "Campus",
            estimatedSeconds = 35,
            scenes = listOf(scene1)
        )

        assertEquals("farewell_party", story.id)
        assertEquals(3, story.scenes.first().choices.size)

        val choiceB = story.scenes.first().choices.find { it.id == "b" }
        assertNotNull(choiceB)
        assertEquals("calm_no", choiceB?.tag)
        assertEquals("s2b", choiceB?.next)
        assertEquals(6, choiceB?.points)
    }
}
