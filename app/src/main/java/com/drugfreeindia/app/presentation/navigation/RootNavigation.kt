package com.drugfreeindia.app.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.drugfreeindia.app.R
import com.drugfreeindia.app.presentation.components.BottomBar
import com.drugfreeindia.app.presentation.components.BottomItem
import com.drugfreeindia.app.presentation.components.OfflineBanner
import com.drugfreeindia.app.presentation.explore.ActivityDetailScreen
import com.drugfreeindia.app.presentation.explore.ExploreScreen
import com.drugfreeindia.app.presentation.explore.MySubmissionsScreen
import com.drugfreeindia.app.presentation.explore.SubmitActivityScreen
import com.drugfreeindia.app.presentation.help.CentresScreen
import com.drugfreeindia.app.presentation.help.DocScreen
import com.drugfreeindia.app.presentation.help.HelpScreen
import com.drugfreeindia.app.presentation.help.HelplinesScreen
import com.drugfreeindia.app.presentation.help.LegalScreen
import com.drugfreeindia.app.presentation.help.StudyPlannerScreen
import com.drugfreeindia.app.presentation.help.TrustedContactsScreen
import com.drugfreeindia.app.presentation.home.HomeScreen
import com.drugfreeindia.app.presentation.impact.ImpactScreen
import com.drugfreeindia.app.presentation.impact.ImpactViewModel
import com.drugfreeindia.app.presentation.impact.SurveyScreen
import com.drugfreeindia.app.presentation.onboarding.AuthScreen
import com.drugfreeindia.app.presentation.onboarding.LocationScreen
import com.drugfreeindia.app.presentation.onboarding.WelcomeScreen
import com.drugfreeindia.app.presentation.play.CinematicStoryScreen
import com.drugfreeindia.app.presentation.play.GameScreen
import com.drugfreeindia.app.presentation.play.PlayScreen
import com.drugfreeindia.app.presentation.play.StoryLibraryScreen
import com.drugfreeindia.app.presentation.profile.ProfileScreen

@Composable
fun RootNavigation(
    startDestination: String,
    appVm: AppViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOnline by appVm.online.collectAsState()
    val config by appVm.config.collectAsState()

    val bottomItems = listOf(
        BottomItem(stringResource(R.string.nav_home), Icons.Outlined.Home, Icons.Rounded.Home),
        BottomItem(stringResource(R.string.nav_play), Icons.Outlined.PlayCircleOutline, Icons.Rounded.PlayCircle),
        BottomItem("Chat", Icons.AutoMirrored.Outlined.Chat, Icons.AutoMirrored.Rounded.Chat),
        BottomItem(stringResource(R.string.nav_explore), Icons.Outlined.Explore, Icons.Rounded.Explore),
        BottomItem(stringResource(R.string.nav_help), Icons.Outlined.HelpOutline, Icons.Rounded.Help)
    )

    val currentTabIndex = when (currentRoute) {
        Routes.HOME -> 0
        Routes.PLAY -> 1
        Routes.CHAT, Routes.STUDENT_GRIEVANCE -> 2
        Routes.EXPLORE -> 3
        Routes.HELP -> 4
        else -> -1
    }

    Scaffold(
        bottomBar = {
            if (currentTabIndex >= 0) {
                BottomBar(
                    items = bottomItems,
                    selected = currentTabIndex,
                    onSelect = { idx ->
                        val targetRoute = Routes.tabs[idx]
                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                // Animated Brand Startup Screen
                composable(Routes.SPLASH) {
                    com.drugfreeindia.app.presentation.onboarding.VilzoSplashAnimationScreen(
                        onAnimationComplete = {
                            navController.navigate(startDestination) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                // Onboarding
                composable(Routes.WELCOME) {
                    WelcomeScreen(
                        onGetStarted = { navController.navigate(Routes.auth(signIn = false)) },
                        onSignIn = { navController.navigate(Routes.auth(signIn = true)) },
                        onNeedHelp = {
                            navController.navigate(Routes.HELP) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = Routes.AUTH,
                    arguments = listOf(navArgument("signIn") { type = NavType.BoolType; defaultValue = false })
                ) { entry ->
                    val signIn = entry.arguments?.getBoolean("signIn") ?: false
                    AuthScreen(
                        signIn = signIn,
                        onBack = { navController.popBackStack() },
                        onDone = { needsLocation ->
                            val dest = if (needsLocation) Routes.location(edit = false) else Routes.HOME
                            navController.navigate(dest) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
                        },
                        onGuest = {
                            navController.navigate(Routes.location(edit = false)) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
                        },
                        onLegal = { kind -> navController.navigate(Routes.legal(kind)) }
                    )
                }

                composable(
                    route = Routes.LOCATION,
                    arguments = listOf(navArgument("edit") { type = NavType.BoolType; defaultValue = false })
                ) { entry ->
                    val editing = entry.arguments?.getBoolean("edit") ?: false
                    LocationScreen(
                        editing = editing,
                        onBack = if (editing) { { navController.popBackStack() } } else null,
                        onDone = {
                            if (editing) {
                                navController.popBackStack()
                            } else {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                // Tabs
                composable(Routes.HOME) {
                    ColumnWithOffline(isOnline) {
                        HomeScreen(
                            contentPadding = innerPadding,
                            onStartChallenge = { storyId -> navController.navigate(Routes.story(storyId)) },
                            onProfile = { navController.navigate(Routes.PROFILE) },
                            onExplore = {
                                navController.navigate(Routes.EXPLORE) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onImpact = {
                                navController.navigate(Routes.IMPACT) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onActivity = { actId -> navController.navigate(Routes.activity(actId)) }
                        )
                    }
                }

                composable(Routes.PLAY) {
                    ColumnWithOffline(isOnline) {
                        PlayScreen(
                            contentPadding = innerPadding,
                            onOpenStory = { storyId -> navController.navigate(Routes.story(storyId)) },
                            onExploreStories = { navController.navigate(Routes.STORY_LIBRARY) }
                        )
                    }
                }

                composable(Routes.HELP) {
                    ColumnWithOffline(isOnline) {
                        HelpScreen(
                            contentPadding = innerPadding,
                            onContacts = { navController.navigate(Routes.CONTACTS) },
                            onHelplines = { navController.navigate(Routes.HELPLINES) },
                            onCentres = { navController.navigate(Routes.CENTRES) },
                            onFriend = { navController.navigate(Routes.HELP_FRIEND) },
                            onPrivacy = { navController.navigate(Routes.PRIVACY) },
                            onGrievance = { navController.navigate(Routes.STUDENT_GRIEVANCE) },
                            onQuickExit = {
                                navController.navigate(Routes.QUICK_EXIT) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }


                // Anonymous Student Grievance & Chat (Direct Tab & Help route)
                composable(Routes.CHAT) {
                    ColumnWithOffline(isOnline) {
                        com.drugfreeindia.app.presentation.help.StudentGrievanceRootScreen(
                            onBack = {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }

                composable(Routes.STUDENT_GRIEVANCE) {
                    com.drugfreeindia.app.presentation.help.StudentGrievanceRootScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.EXPLORE) {
                    ColumnWithOffline(isOnline) {
                        ExploreScreen(
                            contentPadding = innerPadding,
                            onActivity = { actId -> navController.navigate(Routes.activity(actId)) },
                            onSubmit = { navController.navigate(Routes.SUBMIT_ACTIVITY) },
                            onMine = { navController.navigate(Routes.MY_SUBMISSIONS) }
                        )
                    }
                }

                composable(Routes.IMPACT) {
                    val impactVm: ImpactViewModel = hiltViewModel()
                    ColumnWithOffline(isOnline) {
                        ImpactScreen(
                            contentPadding = innerPadding,
                            onSurvey = { navController.navigate(Routes.SURVEY) },
                            onSignIn = { navController.navigate(Routes.auth(signIn = true)) },
                            onBack = { navController.popBackStack() },
                            vm = impactVm
                        )
                    }
                }

                // Story gameplay & result
                composable(
                    route = Routes.STORY,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    GameScreen(onClose = { navController.popBackStack() })
                }

                // Cinematic Story Mode
                composable(Routes.STORY_LIBRARY) {
                    StoryLibraryScreen(
                        onBack = { navController.popBackStack() },
                        onOpenStory = { id -> navController.navigate(Routes.cinematicStory(id)) }
                    )
                }

                composable(
                    route = Routes.CINEMATIC_STORY,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    val storyId = entry.arguments?.getString("id") ?: "the_changes_nobody_noticed"
                    CinematicStoryScreen(
                        storyId = storyId,
                        onBack = { navController.popBackStack() },
                        onFinish = { navController.popBackStack() },
                        onOpenHelp = { navController.navigate(Routes.HELP) },
                        onExplore = {
                            navController.navigate(Routes.EXPLORE) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                // Profile
                composable(Routes.PROFILE) {
                    ProfileScreen(
                        onBack = { navController.popBackStack() },
                        onImpact = { navController.navigate(Routes.IMPACT) },
                        onEditLocation = { navController.navigate(Routes.location(edit = true)) },
                        onContacts = { navController.navigate(Routes.CONTACTS) },
                        onPrivacy = { navController.navigate(Routes.PRIVACY) },
                        onSignIn = { navController.navigate(Routes.auth(signIn = true)) },
                        onSignedOut = {
                            navController.navigate(Routes.WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                // Help sub-screens
                composable(Routes.CONTACTS) {
                    TrustedContactsScreen(onBack = { navController.popBackStack() })
                }

                composable(Routes.HELPLINES) {
                    HelplinesScreen(onBack = { navController.popBackStack() })
                }

                composable(Routes.CENTRES) {
                    CentresScreen(onBack = { navController.popBackStack() })
                }

                composable(Routes.HELP_FRIEND) {
                    DocScreen(
                        title = "Help a friend",
                        asset = "help_a_friend.md",
                        imageKey = "help_hero",
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.PRIVACY) {
                    DocScreen(
                        title = "Privacy & safety",
                        asset = "privacy_policy.md",
                        imageKey = null,
                        onBack = { navController.popBackStack() }
                    )
                }

                // Quick exit target screen (Study Planner)
                composable(Routes.QUICK_EXIT) {
                    StudyPlannerScreen()
                }

                // Explore sub-screens
                composable(
                    route = Routes.ACTIVITY,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    val actId = entry.arguments?.getString("id") ?: ""
                    ActivityDetailScreen(id = actId, onBack = { navController.popBackStack() })
                }

                composable(Routes.SUBMIT_ACTIVITY) {
                    SubmitActivityScreen(
                        onBack = { navController.popBackStack() },
                        onDone = { navController.popBackStack() }
                    )
                }

                composable(Routes.MY_SUBMISSIONS) {
                    MySubmissionsScreen(onBack = { navController.popBackStack() })
                }

                // Survey
                composable(Routes.SURVEY) {
                    val impactVm: ImpactViewModel = hiltViewModel()
                    SurveyScreen(
                        onBack = { navController.popBackStack() },
                        vm = impactVm
                    )
                }

                // Legal placeholder
                composable(
                    route = Routes.LEGAL,
                    arguments = listOf(navArgument("kind") { type = NavType.StringType })
                ) { entry ->
                    val kind = entry.arguments?.getString("kind") ?: "privacy"
                    val url = if (kind == "terms") config.termsUrl else config.privacyPolicyUrl
                    LegalScreen(kind = kind, url = url, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
private fun ColumnWithOffline(isOnline: Boolean, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Column(Modifier.fillMaxSize()) {
        OfflineBanner(visible = !isOnline)
        Box(Modifier.weight(1f)) {
            content()
        }
    }
}
