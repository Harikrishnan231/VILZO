package com.drugfreeindia.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.drugfreeindia.app.R
import com.drugfreeindia.app.presentation.components.BottomNavItem
import com.drugfreeindia.app.presentation.components.DfiBottomBar
import com.drugfreeindia.app.presentation.explore.ExploreScreen
import com.drugfreeindia.app.presentation.help.CentresMapScreen
import com.drugfreeindia.app.presentation.help.DiscreetNotesScreen
import com.drugfreeindia.app.presentation.help.EmergencyGuideScreen
import com.drugfreeindia.app.presentation.help.HelpFriendGuideScreen
import com.drugfreeindia.app.presentation.help.HelpScreen
import com.drugfreeindia.app.presentation.help.HelplinesScreen
import com.drugfreeindia.app.presentation.help.TrustedContactsScreen
import com.drugfreeindia.app.presentation.home.HomeScreen
import com.drugfreeindia.app.presentation.impact.ImpactScreen
import com.drugfreeindia.app.presentation.onboarding.OnboardingScreen
import com.drugfreeindia.app.presentation.play.PlayHubScreen
import com.drugfreeindia.app.presentation.play.story.StoryPlayScreen
import com.drugfreeindia.app.presentation.play.story.StoryResultScreen
import com.drugfreeindia.app.presentation.profile.ProfileScreen

@Composable
fun RootNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarItems = remember {
        listOf(
            BottomNavItem(Routes.Home, R.string.nav_home, Icons.Rounded.Home, Icons.Rounded.Home),
            BottomNavItem(Routes.Play, R.string.nav_play, Icons.Rounded.SportsEsports, Icons.Rounded.SportsEsports),
            BottomNavItem(Routes.Help, R.string.nav_help, Icons.Rounded.Security, Icons.Rounded.Security),
            BottomNavItem(Routes.Explore, R.string.nav_explore, Icons.Rounded.Explore, Icons.Rounded.Explore),
            BottomNavItem(Routes.Profile, R.string.nav_profile, Icons.Rounded.Person, Icons.Rounded.Person)
        )
    }

    val showBottomBar = currentRoute in listOf(
        Routes.Home,
        Routes.Play,
        Routes.Help,
        Routes.Explore,
        Routes.Profile,
        Routes.CommunityImpact
    )

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.fillMaxSize()
        ) {
            // Splash
            composable(Routes.Splash) {
                SplashScreen(
                    onTimeout = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    }
                )
            }

            // Onboarding
            composable(Routes.Onboarding) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Onboarding) { inclusive = true }
                        }
                    }
                )
            }

            // Bottom Nav Destination 1: Home
            composable(Routes.Home) {
                HomeScreen(
                    onNavigateToPlayStory = { storyId ->
                        navController.navigate(Routes.storyPlay(storyId))
                    },
                    onNavigateToHelpTab = {
                        navController.navigate(Routes.Help)
                    },
                    onNavigateToTrustedContacts = {
                        navController.navigate(Routes.TrustedContacts)
                    },
                    onNavigateToHelplines = {
                        navController.navigate(Routes.Helplines)
                    },
                    onNavigateToCentres = {
                        navController.navigate(Routes.CentresMap)
                    },
                    onNavigateToExplore = {
                        navController.navigate(Routes.Explore)
                    },
                    onNavigateToImpact = {
                        navController.navigate(Routes.CommunityImpact)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Routes.Profile)
                    }
                )
            }

            // Bottom Nav Destination 2: Play Hub
            composable(Routes.Play) {
                PlayHubScreen(
                    onPlayStory = { storyId ->
                        navController.navigate(Routes.storyPlay(storyId))
                    },
                    onNavigateToLeaderboard = {
                        navController.navigate(Routes.CommunityImpact)
                    }
                )
            }

            // Bottom Nav Destination 3: Get Help
            composable(Routes.Help) {
                HelpScreen(
                    onNavigateToContacts = { navController.navigate(Routes.TrustedContacts) },
                    onNavigateToCentres = { navController.navigate(Routes.CentresMap) },
                    onNavigateToHelplines = { navController.navigate(Routes.Helplines) },
                    onNavigateToEmergency = { navController.navigate(Routes.EmergencyGuide) },
                    onNavigateToHelpFriend = { navController.navigate(Routes.HelpFriendGuide) },
                    onQuickExit = {
                        navController.navigate(Routes.DiscreetNotes) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Bottom Nav Destination 4: Explore
            composable(Routes.Explore) {
                ExploreScreen()
            }

            // Bottom Nav Destination 5: Profile
            composable(Routes.Profile) {
                ProfileScreen(
                    onNavigateToTrustedContacts = { navController.navigate(Routes.TrustedContacts) },
                    onDataWiped = {
                        navController.navigate(Routes.Onboarding) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Sub-screen: Interactive Story Simulation Play
            composable(
                route = Routes.StoryPlay,
                arguments = listOf(navArgument("storyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val storyId = backStackEntry.arguments?.getString("storyId") ?: "farewell_party"
                StoryPlayScreen(
                    onStoryFinished = { id, score, delta, badge ->
                        navController.navigate(Routes.storyResult(id, score, delta, badge ?: "none")) {
                            popUpTo(Routes.storyPlay(storyId)) { inclusive = true }
                        }
                    },
                    onClose = { navController.popBackStack() }
                )
            }

            // Sub-screen: Story Result
            composable(
                route = Routes.StoryResult,
                arguments = listOf(
                    navArgument("storyId") { type = NavType.StringType },
                    navArgument("score") { type = NavType.IntType },
                    navArgument("confidenceDelta") { type = NavType.IntType },
                    navArgument("badgeId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val storyId = backStackEntry.arguments?.getString("storyId") ?: "farewell_party"
                val score = backStackEntry.arguments?.getInt("score") ?: 18
                val delta = backStackEntry.arguments?.getInt("confidenceDelta") ?: 6
                val badgeId = backStackEntry.arguments?.getString("badgeId")

                StoryResultScreen(
                    storyId = storyId,
                    score = score,
                    delta = delta,
                    badgeId = badgeId,
                    onReplay = {
                        navController.navigate(Routes.storyPlay(storyId)) {
                            popUpTo(Routes.storyResult(storyId, score, delta, badgeId ?: "none")) { inclusive = true }
                        }
                    },
                    onNextScenario = {
                        navController.navigate(Routes.storyPlay("group_chat_dare")) {
                            popUpTo(Routes.storyResult(storyId, score, delta, badgeId ?: "none")) { inclusive = true }
                        }
                    },
                    onSaveToProgress = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    }
                )
            }

            // Sub-screen: Trusted Contacts
            composable(Routes.TrustedContacts) {
                TrustedContactsScreen(onBack = { navController.popBackStack() })
            }

            // Sub-screen: Centres Map
            composable(Routes.CentresMap) {
                CentresMapScreen(onBack = { navController.popBackStack() })
            }

            // Sub-screen: Helplines
            composable(Routes.Helplines) {
                HelplinesScreen(onBack = { navController.popBackStack() })
            }

            // Sub-screen: Emergency Guide
            composable(Routes.EmergencyGuide) {
                EmergencyGuideScreen(onBack = { navController.popBackStack() })
            }

            // Sub-screen: Help a Friend Guide
            composable(Routes.HelpFriendGuide) {
                HelpFriendGuideScreen(onBack = { navController.popBackStack() })
            }

            // Sub-screen: Track Impact
            composable(Routes.CommunityImpact) {
                ImpactScreen()
            }

            // Sub-screen: Discreet Notes (Quick Exit)
            composable(Routes.DiscreetNotes) {
                DiscreetNotesScreen(
                    onReturnToApp = {
                        navController.navigate(Routes.Home) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Floating Bottom Navigation Bar
        if (showBottomBar) {
            DfiBottomBar(
                items = bottomBarItems,
                currentRoute = currentRoute,
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
