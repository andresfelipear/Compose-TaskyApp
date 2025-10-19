package com.aarevalo.tasky.core.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenRoute
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaDetailScreenRoot
import com.aarevalo.tasky.agenda.presentation.edit_text.EditTextScreenRoot
import com.aarevalo.tasky.agenda.presentation.photo_preview.PhotoPreviewScreenRoot
import com.aarevalo.tasky.auth.presentation.login.LoginScreenRoot
import com.aarevalo.tasky.auth.presentation.register.RegistrationScreenRoot
import com.aarevalo.tasky.core.navigation.Destination
import com.aarevalo.tasky.ui.theme.TaskyTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavHostController

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted! You can now show notifications.
                Timber.d("POST_NOTIFICATIONS permission granted by user.")
            } else {
                Timber.w("POST_NOTIFICATIONS permission denied by user. Notifications may be suppressed.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isCheckingAuth.value
            }
        }

        // Call the permission check method after splash screen setup and super.onCreate()
        checkAndRequestNotificationPermission()

        setContent {
            navController = rememberNavController()
            TaskyRoot(viewModel, navController)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Timber.d("POST_NOTIFICATIONS permission already granted.")
                }
                // This is useful if the user previously denied the permission and you want to explain why it's needed.
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Timber.i("Displaying rationale for POST_NOTIFICATIONS permission to user.")
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    Timber.d("Requesting POST_NOTIFICATIONS permission from user.")
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

@Composable
fun TaskyRoot(
    viewModel: MainViewModel,
    navController: NavHostController,
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
                            onNavigate = { event ->
                                handleNavigationEvent(event, navController)
                            }
                        )
                    }

                    composable<Destination.Route.RegisterRoute> {
                        RegistrationScreenRoot(
                            onNavigate = { event ->
                                handleNavigationEvent(event, navController)
                            }
                        )
                    }
                }

                navigation<Destination.Graph.AgendaGraph>(startDestination = Destination.Route.AgendaRoute) {
                    composable<Destination.Route.AgendaRoute> {
                        AgendaScreenRoute(
                            onNavigate = { event ->
                                handleNavigationEvent(event, navController)
                            }
                        )
                    }

                    composable<Destination.Route.AgendaDetailRoute> {
                        AgendaDetailScreenRoot(
                            onNavigate = { event ->
                                handleNavigationEvent(event, navController)
                            },
                            navController = navController  // For savedStateHandle
                        )
                    }

                    composable<Destination.Route.EditTextRoute> {
                        EditTextScreenRoot(
                            onNavigate = { event ->
                                handleNavigationEvent(event, navController)
                            },
                            navController = navController  // For savedStateHandle
                        )
                    }

                    composable<Destination.Route.PhotoPreviewRoute> { backStackEntry ->
                        val photoPreviewRoute = backStackEntry.toRoute<Destination.Route.PhotoPreviewRoute>()
                        PhotoPreviewScreenRoot(
                            route = photoPreviewRoute,
                            onNavigate = { event ->
                                handleNavigationEvent(event, navController)
                            },
                            navController = navController  // For savedStateHandle
                        )
                    }
                }
            }
        }
    }
}

/**
 * Centralized navigation handler - all navigation logic lives here.
 * Screens emit NavigationEvents, this function performs the actual navigation.
 */
private fun handleNavigationEvent(
    event: com.aarevalo.tasky.core.navigation.NavigationEvent,
    navController: NavHostController
) {
    when(event) {
        is com.aarevalo.tasky.core.navigation.NavigationEvent.NavigateToLogin -> {
            navController.navigate(Destination.Route.LoginRoute) {
                // Clear back stack when navigating to login
                popUpTo(Destination.Graph.AuthGraph) {
                    inclusive = true
                }
            }
        }
        is com.aarevalo.tasky.core.navigation.NavigationEvent.NavigateToRegister -> {
            navController.navigate(Destination.Route.RegisterRoute)
        }
        is com.aarevalo.tasky.core.navigation.NavigationEvent.NavigateToAgenda -> {
            navController.navigate(Destination.Route.AgendaRoute) {
                // Clear auth graph when navigating to agenda
                popUpTo(Destination.Graph.AuthGraph) {
                    inclusive = true
                }
            }
        }
        is com.aarevalo.tasky.core.navigation.NavigationEvent.NavigateToAgendaDetail -> {
            navController.navigate(
                Destination.Route.AgendaDetailRoute(
                    agendaItemId = event.agendaItemId,
                    isEditable = event.isEditable,
                    type = event.type
                )
            )
        }
        is com.aarevalo.tasky.core.navigation.NavigationEvent.NavigateToEditText -> {
            navController.navigate(
                Destination.Route.EditTextRoute(
                    type = event.type,
                    text = event.text
                )
            )
        }
        is com.aarevalo.tasky.core.navigation.NavigationEvent.NavigateToPhotoPreview -> {
            navController.navigate(
                Destination.Route.PhotoPreviewRoute(
                    photoUri = event.photoUri,
                    photoKey = event.photoKey,
                    isEditable = event.isEditable
                )
            )
        }
        is com.aarevalo.tasky.core.navigation.NavigationEvent.NavigateBack -> {
            navController.popBackStack()
        }
        is com.aarevalo.tasky.core.navigation.NavigationEvent.NavigateUp -> {
            navController.navigateUp()
        }
    }
}
