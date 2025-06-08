package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DateTimeSelector(
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    onSelectDateClicked: () -> Unit,
    onSelectTimeClicked: () -> Unit
){
    val colors = LocalExtendedColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = colors.surfaceHigher,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(
                vertical = 20.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(32.dp),
            text = stringResource(id = R.string.date_time_at),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PickerButton(
                modifier = Modifier.width(120.dp),
                textContent = LocalTime.now().format(
                    DateTimeFormatter.ofPattern("hh:mm")
                ).toString(),
                isEditable = isEditable,
                onClick = {
                    onSelectTimeClicked()
                }
            )

            PickerButton(
                modifier = Modifier.fillMaxWidth(),
                textContent = LocalDate.now().format(
                    DateTimeFormatter.ofPattern("MMM dd, yyyy")
                ).toString(),
                isEditable = isEditable,
                onClick = {
                    onSelectDateClicked()
                }
            )
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun DateTimeSelectorPreview(){
    TaskyTheme {
        DateTimeSelector(
            isEditable = true,
            onSelectDateClicked = {},
            onSelectTimeClicked = {}
        )
    }
}