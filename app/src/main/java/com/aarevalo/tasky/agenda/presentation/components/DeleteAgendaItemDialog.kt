package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.core.presentation.components.TaskyActionButton
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun DeleteAgendaItemDialog(
    modifier: Modifier = Modifier,
    showConfirmationDialog: Boolean,
    onDismissConfirmationDialog: () -> Unit,
    onConfirmDeleteAgendaItem: () -> Unit,
    elementName: String,
    isDeletingItem: Boolean,
){
    val colors = LocalExtendedColors.current
    val spacing = LocalSpacing.current

    if(showConfirmationDialog){
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissConfirmationDialog,
            title = {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.delete_item_confirmation_title, elementName),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.delete_item_confirmation_description),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    TaskyActionButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.cancel).uppercase(),
                        onClick = { onDismissConfirmationDialog()},
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = colors.onSurfaceVariant70,
                            disabledContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = colors.onSurfaceVariant70
                        )
                    )

                    TaskyActionButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.delete).uppercase(),
                        onClick = { onConfirmDeleteAgendaItem()},
                        isLoading = isDeletingItem,
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            disabledContainerColor = colors.onSurfaceVariant70,
                            disabledContentColor = MaterialTheme.colorScheme.primary,
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun DeleteAgendaItemButtonPreview(){
    TaskyTheme {
        DeleteAgendaItemDialog(
            showConfirmationDialog = true,
            onDismissConfirmationDialog = {},
            onConfirmDeleteAgendaItem = {},
            elementName = "event",
            isDeletingItem = false,
        )
    }
}