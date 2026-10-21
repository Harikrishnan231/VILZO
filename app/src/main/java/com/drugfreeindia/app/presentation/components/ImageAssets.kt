package com.drugfreeindia.app.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.theme.LocalReduceMotion

/**
 * Image system (master prompt §5). Every illustration is referenced by a string key that
 * matches the `imageKey` / `cover_image_key` in story content and the file name in
 * res/drawable-nodpi. Missing keys render a calm vector fallback, never a broken image.
 */
object ImageAssets {
    /** Short alt text for TalkBack. Decorative images pass null instead. */
    val altText = mapOf(
        "welcome_hero" to "Illustration of college friends walking together on campus",
        "hero_challenge" to "Illustration of a student thinking about a choice",
        "help_hero" to "Illustration of open hands holding a small green plant",
        "farewell_s1" to "Farewell party in a hall with friends talking",
        "farewell_s2a" to "Student stepping aside to talk with a friend",
        "farewell_s2b" to "Group of friends around a student who looks unsure",
        "farewell_s2r" to "Student calmly saying no and smiling",
        "farewell_s2c" to "Student messaging a friend for support",
        "farewell_s3" to "Friends leaving the party together, relaxed",
        "story_farewell_cover" to "Farewell party scenario",
        "story_group_chat_cover" to "Group chat dare scenario",
        "story_hostel_cover" to "Hostel night scenario",
        "story_after_match_cover" to "After the match scenario",
        "story_wedding_cover" to "Cousin's wedding scenario",
        "story_senior_cover" to "The cool senior scenario",
        "story_exam_cover" to "Stressed before exams scenario",

        // Story Mode: The Changes Nobody Noticed
        "story_tcnn_cover" to "Arjun walking on a college campus with subtle moody to hopeful light",
        "char_arjun" to "Portrait of Arjun, a 20-year-old Indian college student",
        "story_tcnn_s01" to "Arjun waking late in his bedroom with missed alarms and morning sunlight",
        "story_tcnn_s02" to "Arjun struggling to focus during a college lecture",
        "story_tcnn_s03" to "Arjun and his mother having a quiet breakfast at home",
        "story_tcnn_s04" to "Arjun sitting alone on a campus bench checking his phone",
        "story_tcnn_s05" to "Arjun speaking honestly with his friend Karthik on library steps",
        "story_tcnn_s06" to "Arjun in his dimly lit room at night looking at unanswered calls",
        "story_tcnn_s07" to "Arjun speaking with counsellor Dr. Nandhini in a calm room",
        "story_tcnn_s08" to "Arjun outside the canteen looking at an empty chair at the team table",
        "story_tcnn_s09" to "Arjun having an honest conversation with his mother at the kitchen table",
        "story_tcnn_s10" to "Arjun discussing support options with Dr. Nandhini",
        "story_tcnn_s11" to "Arjun at his desk in the morning organising college assignments",
        "story_tcnn_s12" to "Arjun visiting a calm and welcoming residential recovery centre courtyard",
        "story_tcnn_s13" to "Arjun and Karthik talking openly on a campus park bench in the late afternoon",
        "story_tcnn_s14" to "Arjun reviewing a structured support plan with a counsellor",
        "story_tcnn_e01" to "Arjun sitting in his quiet room at night, with unread messages",
        "story_tcnn_e02" to "Arjun sitting by a morning window with his planner and notebook",
        "story_tcnn_e03" to "Arjun and Karthik walking across campus in gentle morning sunlight",

        // Explore Activities & Events
        "evt_vbyld_hack" to "Viksit Bharat Young Leaders Dialogue Hack for Social Cause",
        "evt_sih_finale" to "Smart India Hackathon Grand Finale at a college innovation lab",
        "evt_hackverse_ieee" to "IEEE HackVerse intercollegiate hackathon with student teams",
        "evt_auroville_marathon" to "Runners at the scenic Auroville Marathon surrounded by green trails",
        "evt_yoga_festival_pondy" to "International Yoga Festival Puducherry session on the promenade beach",
        "evt_serendipity_arts" to "Serendipity Arts Festival in Panaji with outdoor art installations",
        "evt_madras_music_season" to "Carnatic classical music concert at the Madras Music Academy",
        "evt_mamallapuram_dance" to "Classical Indian dance performance against the Shore Temple Mamallapuram",
        "evt_sadhana_forest" to "Volunteers planting indigenous trees at Sadhana Forest Auroville",
        "evt_avag_volunteer" to "Community volunteering with Auroville Village Action Group in rural Tamil Nadu",
        "evt_foss_pondicherry" to "FOSS United Pondicherry open source developer meetup",
        "act_robin_hood_army" to "Robin Hood Army youth volunteer food distribution and teaching drive",
        "act_khelo_india" to "Khelo India campus athletic competitions and sports training club"
    )

    @SuppressLint("DiscouragedApi")
    fun resId(context: android.content.Context, key: String?): Int {
        if (key.isNullOrBlank()) return 0
        val clean = key.lowercase().replace(Regex("[^a-z0-9_]"), "_")
        return context.resources.getIdentifier(clean, "drawable", context.packageName)
    }
}

enum class FallbackArt { HILLS, LEAVES, SUNRISE }

@Composable
fun AppImage(
    key: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallback: FallbackArt = FallbackArt.HILLS
) {
    val context = LocalContext.current
    val id = remember(key) { ImageAssets.resId(context, key) }
    val reduce = LocalReduceMotion.current
    if (id == 0) {
        FallbackIllustration(fallback, modifier.semantics { if (contentDescription != null) this.contentDescription = contentDescription })
        return
    }
    val muted = DfiTheme.colors.surfaceMuted
    AsyncImage(
        model = remember(id, reduce) { ImageRequest.Builder(context).data(id).crossfade(!reduce).build() },
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.background(muted)
    )
}

/** Soft, on-brand vector art used when an illustration is missing. */
@Composable
fun FallbackIllustration(art: FallbackArt, modifier: Modifier = Modifier) {
    val c = DfiTheme.colors
    Box(modifier.background(Brush.verticalGradient(listOf(c.primarySoft, c.surface)))) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            when (art) {
                FallbackArt.HILLS, FallbackArt.SUNRISE -> {
                    drawCircle(Color(0xFFFFE2B8), radius = h * 0.16f, center = Offset(w * 0.72f, h * 0.38f))
                    val back = Path().apply {
                        moveTo(0f, h * 0.72f)
                        cubicTo(w * 0.25f, h * 0.52f, w * 0.5f, h * 0.62f, w * 0.7f, h * 0.58f)
                        cubicTo(w * 0.85f, h * 0.55f, w, h * 0.6f, w, h * 0.6f)
                        lineTo(w, h); lineTo(0f, h); close()
                    }
                    drawPath(back, c.primary.copy(alpha = 0.25f))
                    val front = Path().apply {
                        moveTo(0f, h * 0.85f)
                        cubicTo(w * 0.3f, h * 0.7f, w * 0.6f, h * 0.9f, w, h * 0.78f)
                        lineTo(w, h); lineTo(0f, h); close()
                    }
                    drawPath(front, c.primary.copy(alpha = 0.45f))
                }
                FallbackArt.LEAVES -> {
                    val leaf = Size(w * 0.18f, h * 0.36f)
                    listOf(Triple(0.35f, 0.55f, -30f), Triple(0.55f, 0.45f, 20f), Triple(0.7f, 0.62f, 45f)).forEach { (x, y, r) ->
                        rotate(r, Offset(w * x, h * y)) {
                            drawOval(c.primary.copy(alpha = 0.35f), topLeft = Offset(w * x - leaf.width / 2, h * y - leaf.height / 2), size = leaf)
                        }
                    }
                }
            }
        }
    }
}

