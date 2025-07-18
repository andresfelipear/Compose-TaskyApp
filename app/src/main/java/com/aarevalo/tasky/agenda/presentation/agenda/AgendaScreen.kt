package com.aarevalo.tasky.agenda.presentation.agenda

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.agenda.presentation.components.AddAgendaItemButton
import com.aarevalo.tasky.agenda.presentation.components.AgendaList
import com.aarevalo.tasky.agenda.presentation.components.AgendaScreenHeader
import com.aarevalo.tasky.agenda.presentation.components.CalendarDaysSelector
import com.aarevalo.tasky.agenda.presentation.components.CustomDatePicker
import com.aarevalo.tasky.agenda.presentation.components.DeleteAgendaItemDialog
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem
import com.aarevalo.tasky.core.navigation.Destination
import com.aarevalo.tasky.core.presentation.ui.ObserveAsEvents
import com.aarevalo.tasky.core.util.toTitleCase
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreenRoute(
    viewModel: AgendaViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is AgendaScreenEvent.SuccessLogout -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.logout_successful,
                    Toast.LENGTH_LONG
                )
                    .show()
                navController.navigate(Destination.Route.LoginRoute)
            }

            is AgendaScreenEvent.Success -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.task_updated_successfully,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            is AgendaScreenEvent.SuccessDeleteAgendaItem -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.agenda_item_deleted_successfully,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            is AgendaScreenEvent.GoingBackToLoginScreen -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.back_to_login_screen,
                    Toast.LENGTH_LONG
                )
                    .show()
                navController.navigate(Destination.Route.LoginRoute)
            }

            is AgendaScreenEvent.Error -> {
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

    if(state.showDatePicker) {
        CustomDatePicker(
            currentDate = state.selectedDate,
            onDateSelected = {
                viewModel.onAction(AgendaScreenAction.OnDateSelectedCalendar(it))
            },
            onChangeDatePickerVisibility = {
                viewModel.onAction(AgendaScreenAction.OnChangeDatePickerVisibility)
            }
        )
    }

    AgendaScreen(
        state = state,
        onAction = {
            when(it) {
                is AgendaScreenAction.OnEditAgendaItemClick -> {
                    navController.navigate(
                        Destination.Route.AgendaDetailRoute(
                            agendaItemId = it.agendaItemId,
                            isEditable = true,
                            type = it.type.toStringType()
                        )
                    )
                }
                is AgendaScreenAction.OnOpenAgendaItemClick -> {
                    navController.navigate(
                        Destination.Route.AgendaDetailRoute(
                            agendaItemId = it.agendaItemId,
                            isEditable = false,
                            type = it.type.toStringType()
                        )
                    )
                }
                is AgendaScreenAction.OnCreateAgendaItemClick -> {
                    navController.navigate(
                        Destination.Route.AgendaDetailRoute(
                            agendaItemId = null,
                            isEditable = true,
                            type = it.type.toStringType()
                        )
                    )
                }
                else -> viewModel.onAction(it)
            }
        }
    )

}

@Composable
fun AgendaScreen(
    state: AgendaScreenState,
    onAction: (AgendaScreenAction) -> Unit
) {

    val spacing = LocalSpacing.current
    val snackBarState = remember { SnackbarHostState() }

    DeleteAgendaItemDialog(
        showConfirmationDialog = state.showDeleteConfirmationDialog,
        onDismissConfirmationDialog = {
            onAction(AgendaScreenAction.OnChangeDeleteDialogVisibility)
        },
        onConfirmDeleteAgendaItem = {
            onAction(AgendaScreenAction.OnDeleteAgendaItem)
        },
        elementName = state.agendaItemTypeToDelete,
        isDeletingItem = state.isDeletingItem
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
        topBar = {
            AgendaScreenHeader(
                month = state.selectedDate.month.toString().uppercase(),
                initials = state.initials,
                onOpenCalendar =
                {
                    onAction(AgendaScreenAction.OnChangeDatePickerVisibility)
                },
                dropDownMenuItems = listOf(
                    TaskyDropDownMenuItem(
                        text = stringResource(id = R.string.logout),
                        onClick = {
                            onAction(
                                AgendaScreenAction.OnLogout
                            )
                        }
                    )
                )
            )
        },
        floatingActionButton = {
            AddAgendaItemButton(
                dropDownMenuItems = listOf(
                    TaskyDropDownMenuItem(
                        text = stringResource(id = R.string.add_event),
                        onClick = {
                            onAction(
                                AgendaScreenAction.OnCreateAgendaItemClick(
                                    type = AgendaItemDetails.Event()
                                )
                            )
                        }),
                    TaskyDropDownMenuItem(
                        text = stringResource(id = R.string.add_task),
                        onClick = {
                            onAction(
                                AgendaScreenAction.OnCreateAgendaItemClick(
                                    type = AgendaItemDetails.Task()
                                )
                            )
                        }),
                    TaskyDropDownMenuItem(
                        text = stringResource(id = R.string.add_reminder),
                        onClick = {
                            onAction(
                                AgendaScreenAction.OnCreateAgendaItemClick(
                                    type = AgendaItemDetails.Reminder
                                )
                            )
                        }),
                )
            )
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                CalendarDaysSelector(
                    selectedDate = state.selectedDate,
                    onDaySelected = {
                        onAction(AgendaScreenAction.OnDateChanged(it))
                    },
                    days = state.relatedDates,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if(state.selectedDate == LocalDate.now()) stringResource(id = R.string.today) else state.selectedDate.format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy")
                    )
                        .toTitleCase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )

                AgendaList(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    apiLevel = 34
)
fun AgendaScreenPreview() {
    TaskyTheme {
        AgendaScreen(
            state = AgendaScreenState(
                selectedDate = LocalDate.now(),
            ),
            onAction = {})
    }
}
