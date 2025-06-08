package com.aarevalo.tasky.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenRoute
import com.aarevalo.tasky.agenda.presentation.edit_text.EditTextScreenRoot
import com.aarevalo.tasky.agenda.presentation.photo_preview.PhotoPreviewScreenRoot
import com.aarevalo.tasky.auth.presentation.login.LoginScreenRoot
import com.aarevalo.tasky.auth.presentation.register.RegistrationScreenRoot
import com.aarevalo.tasky.core.navigation.Destination
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
    navController: NavHostController = rememberNavController(),
) {

    TaskyTheme {
        val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
        val isCheckingAuth by viewModel.isCheckingAuth.collectAsStateWithLifecycle()

        if(!isCheckingAuth){
            NavHost(
                navController = navController,
                startDestination = if(isAuthenticated) Destination.Graph.AgendaGraph else Destination.Graph.AuthGraph
            ) {
                navigation<Destination.Graph.AuthGraph>(startDestination = Destination.Route.LoginRoute) {
                    composable<Destination.Route.LoginRoute> {
                        LoginScreenRoot(
                            navController = navController
                        )
                    }

                    composable<Destination.Route.RegisterRoute> {
                        RegistrationScreenRoot(
                            navController = navController
                        )
                    }
                }

                navigation<Destination.Graph.AgendaGraph>(startDestination = Destination.Route.AgendaRoute) {
                    composable<Destination.Route.AgendaRoute> {
                        AgendaScreenRoute(
                            navController = navController
                        )
                    }

                    composable<Destination.Route.EditTextRoute> {
                        EditTextScreenRoot(
                            navController = navController
                        )
                    }

                    composable<Destination.Route.PhotoPreviewRoute> { backStackEntry ->
                        val photoPreviewRoute = backStackEntry.toRoute<Destination.Route.PhotoPreviewRoute>()
                        PhotoPreviewScreenRoot(
                            route = photoPreviewRoute,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
