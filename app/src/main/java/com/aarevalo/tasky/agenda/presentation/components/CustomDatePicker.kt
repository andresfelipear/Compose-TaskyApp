package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    modifier: Modifier = Modifier,
    colors: DatePickerColors = DatePickerDefaults.colors(
        titleContentColor = MaterialTheme.colorScheme.primary,
        headlineContentColor = MaterialTheme.colorScheme.primary,
    ),
    onDateSelected: (LocalDate) -> Unit,
    onChangeDatePickerVisibility: () -> Unit,
    currentDate: LocalDate
){
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = {
            onChangeDatePickerVisibility()
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                val selectedDate = Instant.ofEpochMilli(selectedDateMillis!!).atZone(ZoneOffset.UTC).toLocalDate()
                onDateSelected(selectedDate)
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onChangeDatePickerVisibility()
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