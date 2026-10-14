package com.drugfreeindia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.content.Intent
import com.drugfreeindia.app.presentation.navigation.AppViewModel
import com.drugfreeindia.app.presentation.navigation.RootNavigation
import com.drugfreeindia.app.presentation.theme.DrugFreeIndiaTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    @Inject lateinit var supabase: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        supabase.handleDeeplinks(intent = intent)

        // Keep the splash screen on screen until start destination is determined
        splashScreen.setKeepOnScreenCondition {
            appViewModel.start.value == null
        }

        enableEdgeToEdge()

        setContent {
            val prefs by appViewModel.prefs.collectAsState()
            val startDestination by appViewModel.start.collectAsState()

            DrugFreeIndiaTheme(reduceMotion = prefs.reduceMotion) {
                startDestination?.let { start ->
                    RootNavigation(
                        startDestination = start,
                        appVm = appViewModel
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        supabase.handleDeeplinks(intent = intent)
    }
}
