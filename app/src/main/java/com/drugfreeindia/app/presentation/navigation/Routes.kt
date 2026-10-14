package com.drugfreeindia.app.presentation.navigation

object Routes {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Main = "main"

    // Bottom bar tabs
    const val Home = "home"
    const val Play = "play"
    const val Help = "help"
    const val Explore = "explore"
    const val Profile = "profile"

    // Sub-screens
    const val StoryPlay = "story_play/{storyId}"
    fun storyPlay(storyId: String) = "story_play/$storyId"

    const val StoryResult = "story_result/{storyId}/{score}/{confidenceDelta}/{badgeId}"
    fun storyResult(storyId: String, score: Int, delta: Int, badgeId: String = "none") =
        "story_result/$storyId/$score/$delta/$badgeId"

    const val TrustedContacts = "trusted_contacts"
    const val CentresMap = "centres_map"
    const val Helplines = "helplines"
    const val EmergencyGuide = "emergency_guide"
    const val HelpFriendGuide = "help_friend_guide"
    const val DiscreetNotes = "discreet_notes" // Quick Exit destination

    const val ActivityDetail = "activity_detail/{activityId}"
    fun activityDetail(activityId: String) = "activity_detail/$activityId"

    const val Survey = "survey"
    const val CommunityImpact = "community_impact"
    const val InstitutionView = "institution_view"
    const val PrivacyCenter = "privacy_center"
    const val About = "about"
}
