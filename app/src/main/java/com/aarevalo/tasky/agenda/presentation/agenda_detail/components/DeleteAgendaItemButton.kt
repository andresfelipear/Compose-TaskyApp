package com.aarevalo.tasky.agenda.presentation.agenda_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun DeleteAgendaItemButton(
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    onClick: () -> Unit,
    type: String,
){
    val colors = LocalExtendedColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
    ){
        Button(
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
                },
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                disabledContentColor = colors.onSurfaceVariant70
            ),
            onClick = onClick,
            enabled = isEditable,
        ){
            Text(
                modifier = Modifier.padding(
                    top = 16.dp
                ),
                text = stringResource(id = R.string.delete_item, type).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun DeleteAgendaItemButtonPreview(){
    TaskyTheme {
        DeleteAgendaItemButton(
            isEditable = true,
            onClick = {},
            type = "Reminder"
        )
    }
}