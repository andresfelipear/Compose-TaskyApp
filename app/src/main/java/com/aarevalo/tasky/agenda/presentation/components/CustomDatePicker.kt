package com.aarevalo.tasky.agenda.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    datePickerState: DatePickerState,
    colors: DatePickerColors = DatePickerDefaults.colors(
        titleContentColor = MaterialTheme.colorScheme.primary,
        headlineContentColor = MaterialTheme.colorScheme.primary,
    ),
    modifier: Modifier = Modifier,
    onDateChanged: (LocalDate) -> Unit,
    onShowDatePicker: (Boolean) -> Unit
){
    DatePickerDialog(
        onDismissRequest = {
            onShowDatePicker(false)
        },
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    onDateChanged(selectedDate)
                }
                onShowDatePicker(false)
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onShowDatePicker(false)
            }) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }

    ) {
        DatePicker(
            modifier = modifier,
            state = datePickerState,
            colors = colors
        )
    }
}