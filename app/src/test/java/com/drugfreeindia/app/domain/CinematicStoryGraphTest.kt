package com.drugfreeindia.app.domain

import com.drugfreeindia.app.domain.model.CinematicStory
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CinematicStoryGraphTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun loadStory(): CinematicStory {
        val path = "src/main/assets/seed/the_changes_nobody_noticed.json"
        val file = File(path)
        assertTrue("Seed story file must exist at $path", file.exists())
        return json.decodeFromString(file.readText())
    }

    @Test
    fun testStoryGraphIntegrity() {
        val story = loadStory()
        assertEquals("the_changes_nobody_noticed", story.id)
        assertEquals("S01", story.startScene)

        val sceneMap = story.scenes.associateBy { it.id }
        assertEquals("Expected 17 total nodes (14 scenes + 3 endings)", 17, sceneMap.size)

        val scenes = story.scenes.filter { it.type != "ending" }
        val endings = story.scenes.filter { it.type == "ending" }

        assertEquals(14, scenes.size)
        assertEquals(3, endings.size)

        // 1. Every non-terminal scene has exactly two choices
        for (scene in scenes) {
            assertEquals("Scene ${scene.id} must have exactly 2 choices", 2, scene.choices.size)
            for (choice in scene.choices) {
                assertNotNull("Choice destination in scene ${scene.id} must point to a valid node", sceneMap[choice.next])
            }
        }

        // 2. Each ending is terminal
        for (ending in endings) {
            assertTrue("Ending ${ending.id} choices must be empty", ending.choices.isEmpty())
            assertTrue("Ending ${ending.id} must have reflections", !ending.reflection.isNullOrBlank())
        }

        // 3. Reachability from S01
        val reachable = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(story.startScene)
        reachable.add(story.startScene)

        while (queue.isNotEmpty()) {
            val currId = queue.removeFirst()
            val curr = sceneMap[currId] ?: continue
            for (ch in curr.choices) {
                if (ch.next !in reachable) {
                    reachable.add(ch.next)
                    queue.add(ch.next)
                }
            }
        }

        assertEquals("All 17 nodes must be reachable from start scene", 17, reachable.size)
        assertTrue("E01 must be reachable", "E01" in reachable)
        assertTrue("E02 must be reachable", "E02" in reachable)
        assertTrue("E03 must be reachable", "E03" in reachable)

        // 4. Verify opening variants validity
        for (scene in scenes) {
            for (variant in scene.openingVariants) {
                for (prev in variant.cameFrom) {
                    assertNotNull("Variant cameFrom $prev in scene ${scene.id} must exist", sceneMap[prev])
                }
            }
        }
    }

    @Test
    fun testAllStoryDrawablesExist() {
        val story = loadStory()
        val drawableDir = File("src/main/res/drawable-nodpi")
        assertTrue("drawable-nodpi directory must exist", drawableDir.exists())

        // Check cover image
        val coverFile = File(drawableDir, "story_tcnn_cover.webp")
        assertTrue("Story cover image must exist at ${coverFile.path}", coverFile.exists())

        // Check scene images
        for (node in story.scenes) {
            val imgKey = node.imageKey
            assertNotNull("Scene ${node.id} must specify an imageKey", imgKey)
            val imgFile = File(drawableDir, "$imgKey.webp")
            assertTrue("Scene image asset for ${node.id} ($imgKey.webp) must exist", imgFile.exists())
        }
    }

    @Test
    fun testExploreRealEventsDrawablesExist() {
        val drawableDir = File("src/main/res/drawable-nodpi")
        val expectedKeys = listOf(
            "evt_vbyld_hack",
            "evt_sih_finale",
            "evt_hackverse_ieee",
            "evt_auroville_marathon",
            "evt_yoga_festival_pondy",
            "evt_serendipity_arts",
            "evt_madras_music_season",
            "evt_mamallapuram_dance",
            "evt_sadhana_forest",
            "evt_avag_volunteer",
            "evt_foss_pondicherry"
        )
        for (key in expectedKeys) {
            val file = File(drawableDir, "$key.webp")
            assertTrue("Explore event image $key.webp must exist", file.exists())
        }
    }
}
