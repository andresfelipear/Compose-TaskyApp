package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun PickerButton(
    modifier: Modifier = Modifier,
    textContent: String,
    isEditable: Boolean = false,
    onClick: () -> Unit
){
    val colors = LocalExtendedColors.current

    Row(
        modifier = modifier
            .clip(
                shape = RoundedCornerShape(4.dp)
            )
            .background(
                color = colors.surfaceHigher
            )
            .padding(
                top = 4.dp,
                bottom = 4.dp,
                start = 12.dp,
                end = 20.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = textContent,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        if(isEditable){
            IconButton(
                modifier = Modifier.size(20.dp),
                onClick = {
                    onClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(id = R.string.dropdown),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun PickerButtonPreview(){
    TaskyTheme {
        PickerButton(
            textContent = "08:00",
            isEditable = true,
            onClick = {}
        )
    }
}