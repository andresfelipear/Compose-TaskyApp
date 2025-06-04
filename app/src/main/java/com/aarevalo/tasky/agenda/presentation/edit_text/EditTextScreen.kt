package com.aarevalo.tasky.agenda.presentation.edit_text

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.EditTextFieldType
import com.aarevalo.tasky.core.navigation.Destination
import com.aarevalo.tasky.core.presentation.components.AppBar
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun EditTextScreenRoot(
    route: Destination.Route.EditTextRoute,
    navController: NavController,
    viewModel: EditTextViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    EditTextScreen(route = route,
                   state = state,
                   onAction = { action ->
                       when(action) {
                           is EditTextScreenAction.GoBack -> {
                               navController.previousBackStackEntry?.savedStateHandle?.set(
                                   "edit_text_result",
                                   action.result
                               )
                               navController.navigateUp()
                           }
                       }
                   })
}

@Composable
fun EditTextScreen(
    route: Destination.Route.EditTextRoute,
    state: EditTextScreenState,
    onAction: (EditTextScreenAction) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = LocalExtendedColors.current

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
             topBar = {
                 AppBar(
                     modifier = Modifier.border(
                         width = 1.dp,
                         color = colors.surfaceHigher,
                         shape = MaterialTheme.shapes.medium
                     ),
                     backgroundColor = MaterialTheme.colorScheme.surface,
                     contentStart = {
                            Text(
                                modifier = Modifier.clickable {
                                    onAction(
                                        EditTextScreenAction.GoBack(null)
                                    )
                                },
                                text = stringResource(id = R.string.cancel),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 12.sp,
                                    letterSpacing = 0.sp
                                )
                            )
                        },
                        contentMiddle = {
                            Text(
                                text = stringResource(
                                    id = R.string.edit_text_title,
                                    route.title
                                ).uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                ),
                            )
                        },
                        contentEnd = {
                            Text(
                                modifier = Modifier.clickable {
                                    onAction(
                                        EditTextScreenAction.GoBack(
                                            EditTextScreenResult(
                                                type = state.type,
                                                value = state.textFieldContent.text.toString()
                                            )
                                        )
                                    )
                                },
                                text = stringResource(id = R.string.save),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = colors.success,
                                    lineHeight = 12.sp,
                                    letterSpacing = 0.sp
                                )
                            )
                        })
             }) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 24.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 48.dp
                    ),
                state = state.textFieldContent,
                textStyle = if(state.type == EditTextFieldType.TITLE) {
                    MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight(400)
                    )
                } else {
                    MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    apiLevel = 34
)
@Composable
fun EditTextScreenPreview() {
    TaskyTheme {
        EditTextScreen(
            route = Destination.Route.EditTextRoute(
            title = "Title",
            text = "Text"
        ),
            state = EditTextScreenState(
                type = EditTextFieldType.TITLE,
                textFieldContent = TextFieldState(
                    initialText = "Text"
                )
            ),
            onAction = {})
    }
}