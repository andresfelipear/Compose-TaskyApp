package com.aarevalo.tasky.agenda.presentation.agenda_detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.EditTextFieldType
import com.aarevalo.tasky.agenda.domain.model.ReminderType
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenEvent
import com.aarevalo.tasky.agenda.presentation.agenda_detail.components.AddAttendeeDialog
import com.aarevalo.tasky.agenda.presentation.components.CustomDatePicker
import com.aarevalo.tasky.agenda.presentation.agenda_detail.components.CustomTimePicker
import com.aarevalo.tasky.agenda.presentation.agenda_detail.components.DateTimeSelector
import com.aarevalo.tasky.agenda.presentation.components.DeleteAgendaItemDialog
import com.aarevalo.tasky.agenda.presentation.agenda_detail.components.EventType
import com.aarevalo.tasky.agenda.presentation.agenda_detail.components.ReminderButton
import com.aarevalo.tasky.agenda.presentation.agenda_detail.components.VisitorsSection
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem
import com.aarevalo.tasky.core.navigation.Destination
import com.aarevalo.tasky.core.presentation.components.AppBar
import com.aarevalo.tasky.core.presentation.ui.ObserveAsEvents
import com.aarevalo.tasky.core.util.UiText
import com.aarevalo.tasky.core.util.formattedDateToString
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaDetailScreenRoot(
    navController: NavController,
    viewModel: AgendaDetailViewModel = hiltViewModel(),
){
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val editedTitle: String? = backStackEntry
        ?.savedStateHandle
        ?.get(EditTextFieldType.TITLE.key)

    val editedDescription: String? = backStackEntry
        ?.savedStateHandle
        ?.get(EditTextFieldType.DESCRIPTION.key)

    ObserveAsEvents(viewModel.event){
        event ->
        when(event) {
            is AgendaDetailScreenEvent.ItemSaved -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.event_edited_successfully,
                    Toast.LENGTH_LONG
                ).show()
                navController.navigate(Destination.Route.AgendaRoute)
            }
            is AgendaDetailScreenEvent.ItemCreated -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.event_saved_successfully,
                    Toast.LENGTH_LONG
                ).show()
                navController.navigate(Destination.Route.AgendaRoute)
            }
            is AgendaDetailScreenEvent.GoingBackToLoginScreen -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.back_to_login_screen,
                    Toast.LENGTH_LONG
                )
                    .show()
                navController.navigate(Destination.Route.LoginRoute)
            }
            is AgendaDetailScreenEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    event.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> Unit
        }
    }

    LaunchedEffect(key1 = editedTitle, key2 = editedDescription){
        if(editedTitle != null){
            viewModel.onAction(AgendaDetailScreenAction.OnEditTitle(editedTitle))
            backStackEntry?.savedStateHandle?.remove<String>(EditTextFieldType.TITLE.key)
        }
        if(editedDescription != null){
            viewModel.onAction(AgendaDetailScreenAction.OnEditDescription(editedDescription))
            backStackEntry?.savedStateHandle?.remove<String>(EditTextFieldType.DESCRIPTION.key)
        }
    }

    if(state.isFromDateDialogVisible) {
        CustomDatePicker(
            currentDate = state.fromDate,
            onDateSelected = {
                viewModel.onAction(AgendaDetailScreenAction.OnFromDateChanged(it))
            },
            onChangeDatePickerVisibility = {
                viewModel.onAction(AgendaDetailScreenAction.OnChangeFromDateDialogVisibility)
            })
    }

    if(state.isFromTimeDialogVisible) {
        CustomTimePicker(
            currentTime = state.fromTime,
            onTimeSelected = {
                viewModel.onAction(AgendaDetailScreenAction.OnFromTimeChanged(it))
            },
            onChangeTimePickerVisibility = {
                viewModel.onAction(AgendaDetailScreenAction.OnChangeFromTimeDialogVisibility)
            }
        )
    }

    if(state.details is AgendaItemDetails.Event){
        val details = state.details as AgendaItemDetails.Event
        if(state.isToDateDialogVisible) {
            CustomDatePicker(
                currentDate = details.toDate,
                onDateSelected = {
                    viewModel.onAction(AgendaDetailScreenAction.OnToDateChanged(it))
                },
                onChangeDatePickerVisibility = {
                    viewModel.onAction(AgendaDetailScreenAction.OnChangeToDateDialogVisibility)
                }
            )
        }

        if(state.isToTimeDialogVisible) {
            CustomTimePicker(
                currentTime = details.toTime,
                onTimeSelected = {
                    viewModel.onAction(AgendaDetailScreenAction.OnToTimeChanged(it))
                },
                onChangeTimePickerVisibility = {
                    viewModel.onAction(AgendaDetailScreenAction.OnChangeToTimeDialogVisibility)
                }
            )
        }

        AddAttendeeDialog(
            showConfirmationDialog = details.isAddAttendeeDialogVisible,
            onDismissConfirmationDialog = {
                viewModel.onAction(AgendaDetailScreenAction.OnChangeIsAddAttendeeDialogVisibility)
            },
            onAddAttendee = {
                viewModel.onAction(AgendaDetailScreenAction.OnAddAttendee(it))
            },
            email = state.attendeesState.email,
            onEmailChange = {
                viewModel.onAction(AgendaDetailScreenAction.OnNewAttendeeEmailChanged(it))
            },
            isValidEmail = state.attendeesState.isEmailValid,
            isAddingAttendee = state.attendeesState.isAdding,
        )

    }

    AgendaDetailScreen(
        state = state,
        onAction = {
            when(it){
                is AgendaDetailScreenAction.OnNavigateToEditTextScreen -> {
                    navController.navigate(Destination.Route.EditTextRoute(
                        type = it.type.toString(),
                        text = it.text
                    ))
                }
                is AgendaDetailScreenAction.OnGoBack -> {
                    navController.navigateUp()
                }
                else -> {
                    viewModel.onAction(it)
                }
            }
        }
    )
}

@Composable
fun AgendaDetailScreen(
    state: AgendaDetailScreenState,
    onAction: (AgendaDetailScreenAction) -> Unit
){
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = LocalExtendedColors.current
    val spacing = LocalSpacing.current

    val type = state.details.toStringType()

    DeleteAgendaItemDialog(
        showConfirmationDialog = state.isConfirmingToDeleteItem,
        onDismissConfirmationDialog = {
            onAction(AgendaDetailScreenAction.OnChangeDeleteDialogVisibility)
        },
        onConfirmDeleteAgendaItem = {
            onAction(AgendaDetailScreenAction.OnDeleteItem)
        },
        elementName = type,
        isDeletingItem = state.isDeletingItem
    )

    Scaffold(
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        topBar = {
            AppBar(
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentStart = {
                    Text(
                        modifier = Modifier.clickable {
                           onAction(AgendaDetailScreenAction.OnGoBack)
                        },
                        text = stringResource(id = R.string.cancel),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            lineHeight = 12.sp,
                            letterSpacing = 0.sp
                        )
                    )
                },
                contentMiddle = {
                    Text(
                        text =  if(state.isEditable)stringResource(
                            id = R.string.detail_screen_title, type
                        ).uppercase() else formattedDateToString(state.fromDate).uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                },
                contentEnd = {
                    if(state.isEditable){
                        Text(
                            modifier = Modifier.clickable {
                                onAction(
                                    AgendaDetailScreenAction.OnSaveChanges
                                )
                            },
                            text = stringResource(id = R.string.save),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = colors.success,
                                lineHeight = 12.sp,
                                letterSpacing = 0.sp
                            )
                        )
                    } else {
                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                onAction(AgendaDetailScreenAction.OnChangeIsEditable)
                            },
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                EventType(
                    type = state.details
                )
                Column {
                    Row(
                        modifier = Modifier
                            .drawBehind {
                                drawLine(
                                    color = colors.surfaceHigher,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            .padding(
                                bottom = 24.dp,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        IconButton(
                            modifier = Modifier.size(20.dp),
                            onClick = {
                                if(state.details is AgendaItemDetails.Task){
                                    onAction(AgendaDetailScreenAction.OnChangeTaskStatus)                                }
                            }) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = if(state.details is AgendaItemDetails.Task && state.details.isDone) Icons.Default.CheckCircleOutline else Icons.Default.RadioButtonUnchecked,
                                contentDescription = stringResource(id = R.string.done),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.title,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    textDecoration = if(state.details is AgendaItemDetails.Task && state.details.isDone) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )

                            if(state.isEditable){
                                IconButton(
                                    modifier = Modifier.size(20.dp),
                                    onClick = {
                                        onAction(AgendaDetailScreenAction.OnNavigateToEditTextScreen(
                                            EditTextFieldType.TITLE,
                                            state.title
                                        ))
                                    }
                                ){
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = stringResource(id = R.string.edit),
                                        tint = colors.onSurfaceVariant70
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .drawBehind {
                                drawLine(
                                    color = colors.surfaceHigher,
                                    start = Offset(
                                        0f,
                                        size.height
                                    ),
                                    end = Offset(
                                        size.width,
                                        size.height
                                    ),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            .padding(
                                bottom = 20.dp,
                                top = 20.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 3
                        )

                        if(state.isEditable) {
                            IconButton(modifier = Modifier.size(20.dp),
                                       onClick = {
                                           onAction(AgendaDetailScreenAction.OnNavigateToEditTextScreen(
                                               EditTextFieldType.DESCRIPTION,
                                               state.description
                                           ))
                                       }) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = stringResource(id = R.string.edit),
                                    tint = colors.onSurfaceVariant70
                                )
                            }
                        }
                    }

                    DateTimeSelector(
                        isEditable = state.isEditable,
                        onSelectDateClicked = {
                            onAction(AgendaDetailScreenAction.OnChangeFromDateDialogVisibility)
                        },
                        onSelectTimeClicked = {
                            onAction(AgendaDetailScreenAction.OnChangeFromTimeDialogVisibility)
                        },
                        date = state.fromDate,
                        time = state.fromTime,
                        title = if(state.details is AgendaItemDetails.Event){
                            stringResource(id = R.string.date_time_from)
                        } else {
                            stringResource(id = R.string.date_time_at)
                        }
                    )

                    if(state.details is AgendaItemDetails.Event){
                        DateTimeSelector(
                            isEditable = state.isEditable,
                            onSelectDateClicked = {
                                onAction(AgendaDetailScreenAction.OnChangeToDateDialogVisibility)
                            },
                            onSelectTimeClicked = {
                                onAction(AgendaDetailScreenAction.OnChangeToTimeDialogVisibility)
                            },
                            date = state.details.toDate,
                            time = state.details.toTime,
                            title = stringResource(id = R.string.date_time_to)
                        )
                    }

                    ReminderButton(
                        isEditable = state.isEditable,
                        dropDownMenuItems =
                            ReminderType.entries.map{ reminderType ->
                                TaskyDropDownMenuItem(
                                    text = stringResource(R.string.reminder_type, reminderType.duration.UiText().asString()),
                                    onClick = {
                                        onAction(AgendaDetailScreenAction.OnReminderTypeChanged(reminderType))
                                    }
                                )
                            },
                        reminderType = state.reminderType
                    )
                }

                if(state.details is AgendaItemDetails.Event){
                    VisitorsSection(
                        isEditing = state.isEditable,
                        eventDetails = state.details,
                        onFilterTypeChanged = {
                            onAction(AgendaDetailScreenAction.OnFilterTypeChanged(it))
                        },
                        onDeleteAttendee = {
                            onAction(AgendaDetailScreenAction.OnDeleteAttendee(it))
                        },
                        onAddNewAttendee = {
                            onAction(AgendaDetailScreenAction.OnChangeIsAddAttendeeDialogVisibility)
                        },
                        attendeesState = state.attendeesState
                    )
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = colors.surfaceHigher,
                            start = Offset(
                                0f,
                                0f,
                            ),
                            end = Offset(
                                size.width,
                                0f,
                            ),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(
                        top = 16.dp,
                    )
                    .clickable {
                        onAction(AgendaDetailScreenAction.OnChangeDeleteDialogVisibility)
                    },
                text = stringResource(id = R.string.delete_item, type).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview(showBackground = true, apiLevel = 34)
@Composable
fun AgendaDetailScreenPreview(){
    TaskyTheme {
        AgendaDetailScreen(
            state = AgendaDetailScreenState(
                title = "Event title",
                description = "Event description",
                details = AgendaItemDetails.Task(),
                isEditable = false,
            ),
            onAction = {}
        )
    }
}