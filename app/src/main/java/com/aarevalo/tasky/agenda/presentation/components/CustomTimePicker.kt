package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.aarevalo.tasky.R
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePicker(
    modifier: Modifier = Modifier,
    currentTime: LocalTime,
    title: String = stringResource(id = R.string.time_picker_start),
    colors: TimePickerColors = TimePickerDefaults.colors(
        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary
    ),
    onTimeSelected: (LocalTime) -> Unit,
    onChangeTimePickerVisibility: () -> Unit
){
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = false
    )

    TimePickerDialog(
        title = title,
        onDismiss = {
            onChangeTimePickerVisibility()
        },
        onConfirm = {
            onTimeSelected(
                LocalTime.of(
                    timePickerState.hour,
                    timePickerState.minute
                )
            )
        }
    ){
        TimePicker(
            modifier = modifier,
            state = timePickerState,
            colors = colors
        )
    }
}

@Composable
private fun TimePickerDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        title = {
            Text(
                text = stringResource(id = R.string.time_picker_title, title),
                color = MaterialTheme.colorScheme.primary
            )
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    stringResource(id = android.R.string.cancel),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(
                    stringResource(id = android.R.string.ok),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = { content() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, apiLevel = 33)
@Composable
fun CustomTimePickerPreview(){
    TaskyTheme {
        CustomTimePicker(
            currentTime = LocalTime.now(),
            onTimeSelected = {},
            onChangeTimePickerVisibility = {}
        )
    }
}