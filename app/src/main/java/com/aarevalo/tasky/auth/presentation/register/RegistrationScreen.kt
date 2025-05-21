package com.aarevalo.tasky.auth.presentation.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aarevalo.tasky.R
import com.aarevalo.tasky.auth.presentation.components.TaskyActionButton
import com.aarevalo.tasky.auth.presentation.components.TaskyInputTextField
import com.aarevalo.tasky.auth.presentation.components.TaskyPasswordTextField
import com.aarevalo.tasky.auth.presentation.components.TaskySurface
import com.aarevalo.tasky.core.navigation.Destination
import com.aarevalo.tasky.core.presentation.ui.ObserveAsEvents
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun RegistrationScreenRoot(
    viewModel: RegistrationViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is RegistrationEvent.Success -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.registration_successful,
                    Toast.LENGTH_LONG
                )
                    .show()
                navController.navigate(Destination.Route.LoginRoute)
            }

            is RegistrationEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    event.errorMessage.asString(context),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    RegistrationScreen(
        onAction = { action ->
            when(action) {
                is RegistrationAction.OnGoToLogin -> {
                    navController.navigate(Destination.Route.LoginRoute)
                }

                else -> Unit
            }
            viewModel.onAction(action)
        },
        state = state,
    )
}

@Composable
fun RegistrationScreen(
    onAction: (RegistrationAction) -> Unit,
    state: RegistrationScreenState,
) {

    val spacing = LocalSpacing.current
    val snackBarState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.register_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 70.dp)
                )

                Spacer(modifier = Modifier.height(spacing.spaceExtraLarge))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 24.dp,
                                topEnd = 24.dp
                            )
                        )
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(
                            horizontal = spacing.spaceMedium,
                            vertical = spacing.spaceLarge
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)
                    ) {
                        TaskyInputTextField(
                            text = state.name,
                            onValueChange = {
                                onAction(RegistrationAction.OnNameChanged(it))
                            },
                            isValidInput = state.isValidName,
                            hint = stringResource(id = R.string.name_hint),
                        )

                        TaskyInputTextField(
                            text = state.email,
                            onValueChange = {
                                onAction(RegistrationAction.OnEmailChanged(it))
                            },
                            isValidInput = state.isValidEmail,
                            hint = stringResource(id = R.string.email_hint),
                        )

                        TaskyPasswordTextField(passwordState = state.passwordState,
                                               isPasswordVisible = state.isPasswordVisible,
                                               onPasswordVisibilityChange = {
                                                   onAction(RegistrationAction.OnPasswordVisibilityChanged(it))
                                               })
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraMedium)
                    ) {
                        TaskyActionButton(
                            text = stringResource(id = R.string.register_button),
                            onClick = { onAction(RegistrationAction.OnRegister) },
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = state.isLoading,
                            isEnabled = state.isValidName && state.isValidEmail && state.isValidPassword
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = stringResource(id = R.string.already_have_account),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.padding(spacing.spaceExtraSmall))
                            TaskySurface(
                                onClick = { onAction(RegistrationAction.OnGoToLogin) },
                                text = stringResource(id = R.string.login_link),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    apiLevel = 34
)
@Composable
fun RegistrationScreenPreview() {
    TaskyTheme {
        RegistrationScreen(
            onAction = {},
            state = RegistrationScreenState(),
        )
    }
}