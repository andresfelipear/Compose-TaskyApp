package com.aarevalo.tasky.auth.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.aarevalo.tasky.ui.theme.LocalExtendedColors

@Composable
fun LinkButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(
        color = LocalExtendedColors.current.link
    ),
){
    Text(
        text = text,
        style = textStyle,
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun LinkButtonPreview() {
    LinkButton(
        onClick = {},
        text = "LOG IN",
    )
}