package com.aarevalo.tasky.agenda.presentation.agenda_detail

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.ReminderType
import com.aarevalo.tasky.agenda.presentation.components.CustomDatePicker
import com.aarevalo.tasky.agenda.presentation.components.CustomTimePicker
import com.aarevalo.tasky.agenda.presentation.components.DateTimeSelector
import com.aarevalo.tasky.agenda.presentation.components.DeleteAgendaItemButton
import com.aarevalo.tasky.agenda.presentation.components.EventType
import com.aarevalo.tasky.agenda.presentation.components.ReminderButton
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem
import com.aarevalo.tasky.core.presentation.components.AppBar
import com.aarevalo.tasky.core.util.toHumanReadableString
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaDetailScreenRoot(
    navController: NavController,
    viewModel: AgendaDetailViewModel = hiltViewModel(),
){
    val state by viewModel.state.collectAsStateWithLifecycle()

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

    AgendaDetailScreen(
        state = state,
        onAction = viewModel::onAction
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

    val type = when(state.details){
        is AgendaItemDetails.Event -> "event"
        is AgendaItemDetails.Reminder -> "reminder"
        is AgendaItemDetails.Task -> "task"
    }

    DeleteAgendaItemButton(
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
                            /* TODO */
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
                        text = stringResource(
                            id = R.string.detail_screen_title, type
                        ).uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                },
                contentEnd = {
                    Text(
                        modifier = Modifier.clickable {
                            /* TODO */
                        },
                        text = stringResource(id = R.string.save),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colors.success,
                            lineHeight = 12.sp,
                            letterSpacing = 0.sp
                        )
                    )
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
                            onClick = { /*TODO*/ }) {
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
                                        /* TODO */
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
                                       onClick = {/* TODO */
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
                        }
                    )

                    ReminderButton(
                        isEditable = state.isEditable,
                        dropDownMenuItems =
                            ReminderType.entries.map{ reminderType ->
                                TaskyDropDownMenuItem(
                                    text = stringResource(R.string.reminder_type, reminderType.duration.toHumanReadableString()),
                                    onClick = {
                                        onAction(AgendaDetailScreenAction.OnReminderTypeChanged(reminderType))
                                    }
                                )
                            },
                        reminderType = state.reminderType
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
                details = AgendaItemDetails.Task()
            ),
            onAction = {}
        )
    }
}