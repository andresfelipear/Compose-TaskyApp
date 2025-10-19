package com.aarevalo.tasky.core.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.aarevalo.tasky.ui.theme.TaskyTheme
import com.example.ui.theme.LocalExtendedTypography

@Composable
fun TaskyHelperLabel(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalExtendedTypography.current.labelExtraSmall.copy(
        color = MaterialTheme.colorScheme.error,
    )
){
    Text(
        text = text,
        style = textStyle,
        modifier = modifier
    )
}

@Composable
@Preview(showBackground = true, apiLevel = 34)
fun TaskyHelperLabelPreview(){
    TaskyTheme {
        TaskyHelperLabel(text = "This is a helper label")
    }
}