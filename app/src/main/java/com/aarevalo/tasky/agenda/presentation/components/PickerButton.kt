package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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

    Button(
        onClick = {
            if(isEditable){
                onClick()
            }
        },
        modifier = modifier.height(32.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.surfaceHigher,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    top = 4.dp,
                    bottom = 4.dp,
                    start = 12.dp,
                    end = 20.dp
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = textContent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier.size(20.dp),
                ){
                    if(isEditable){
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(id = R.string.dropdown),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun PickerButtonPreview(){
    TaskyTheme {
        PickerButton(
            modifier = Modifier.width(200.dp),
            textContent = "08:00",
            isEditable = true,
            onClick = {}
        )
    }
}