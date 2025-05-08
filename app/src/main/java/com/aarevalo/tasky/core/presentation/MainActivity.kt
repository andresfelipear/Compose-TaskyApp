package com.aarevalo.tasky.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aarevalo.tasky.auth.presentation.login.LoginScreenRoot
import com.aarevalo.tasky.ui.theme.TaskyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isCheckingAuth.value
            }
        }
        setContent {
            TaskyRoot(viewModel)
        }
    }
}

@Composable
fun TaskyRoot(
    viewModel: MainViewModel,
    navController: NavController = rememberNavController(),
) {

    TaskyTheme {
        val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
        val isCheckingAuth by viewModel.isCheckingAuth.collectAsStateWithLifecycle()

        LoginScreenRoot(
            navController = navController
        )

    }

}
