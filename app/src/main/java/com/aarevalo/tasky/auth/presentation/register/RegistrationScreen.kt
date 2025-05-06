package com.aarevalo.tasky.auth.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aarevalo.tasky.R
import com.aarevalo.tasky.auth.presentation.components.TaskyActionButton
import com.aarevalo.tasky.auth.presentation.components.TaskyInputTextField
import com.aarevalo.tasky.auth.presentation.components.TaskyPasswordTextField
import com.aarevalo.tasky.auth.presentation.components.TaskySurface
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun RegistrationScreenRoot(
    viewModel: RegistrationViewModel = hiltViewModel(),
    onRegistrationSuccess: () -> Unit,
) {
}

@Composable
fun RegistrationScreen(
    onAction: (RegistrationAction) -> Unit,
    state: RegistrationScreenState,
) {

    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
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
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)
            ){
                TaskyInputTextField(
                    text = state.name,
                    onValueChange = {
                        onAction(RegistrationAction.NameChanged(it))
                    },
                    isValidInput = state.isValidName,
                    hint = stringResource(id = R.string.name_hint),
                )

                TaskyInputTextField(
                    text = state.email,
                    onValueChange = {
                        onAction(RegistrationAction.EmailChanged(it))
                    },
                    isValidInput = state.isValidPassword,
                    hint = stringResource(id = R.string.email_hint),
                )

                TaskyPasswordTextField(
                    passwordState = state.passwordState.value,
                    isPasswordVisible = state.isPasswordVisible,
                    onPasswordVisibilityChange = {
                        onAction(RegistrationAction.PasswordVisibilityChanged(it))
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraMedium)
            ) {
                TaskyActionButton(
                    text = stringResource(id = R.string.register_button),
                    onClick = { onAction(RegistrationAction.Register) },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = state.isLoading,
                    isEnabled = state.isValidName && state.isValidEmail && state.isValidPassword
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ){
                    Text(
                        text = stringResource(id = R.string.already_have_account),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.padding(spacing.spaceExtraSmall))
                    TaskySurface(
                        onClick = { onAction(RegistrationAction.Login) },
                        text = stringResource(id = R.string.login_link),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun RegistrationScreenPreview() {
    TaskyTheme {
        RegistrationScreen(
            onAction = {},
            state = RegistrationScreenState(),
        )
    }
}