package com.aarevalo.tasky.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    contentStart: @Composable () -> Unit,
    contentMiddle: @Composable () -> Unit = {},
    contentEnd: @Composable () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primary
){
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .padding(
                horizontal = spacing.spaceMedium,
                vertical = spacing.spaceSmall
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ){
            contentStart()
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ){
            contentMiddle()
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ){
            contentEnd()
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun AppBarPreview(){
    TaskyTheme {
        AppBar(
            contentStart = {
                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "arrow back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            contentMiddle = {
                Text(
                    text = "Text",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                )
            },
            contentEnd = {
                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "arrow back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        )
    }
}