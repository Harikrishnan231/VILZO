package com.drugfreeindia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.drugfreeindia.app.data.seed.DatabaseInitializer
import com.drugfreeindia.app.presentation.navigation.RootNavigation
import com.drugfreeindia.app.presentation.theme.DrugFreeIndiaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Asynchronously bootstrap seed data offline
        lifecycleScope.launch {
            databaseInitializer.seedIfNeeded()
        }

        setContent {
            DrugFreeIndiaTheme {
                RootNavigation()
            }
        }
    }
}
