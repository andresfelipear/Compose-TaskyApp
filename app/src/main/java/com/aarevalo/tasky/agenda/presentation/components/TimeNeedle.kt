package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun TimeNeedle(
    modifier: Modifier = Modifier,
    needleColor: Color = MaterialTheme.colorScheme.primary,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    dotRadius: Float = 10f,
    lineStrokeWidth: Float = 2f

){
    Canvas(
        modifier = modifier.fillMaxWidth()
    ) {
        val centerY = size.height / 2f

        val dotCenter = Offset(x = dotRadius, y = centerY)
        drawCircle(
            color = dotColor,
            radius = dotRadius,
            center = dotCenter
        )

        val lineEndX = size.width

        drawLine(
            color = needleColor,
            start = Offset(x = dotRadius, y = centerY),
            end = Offset(x = lineEndX, y = centerY),
            strokeWidth = lineStrokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 34)
fun TimeNeedlePreview() {
    TaskyTheme {
        TimeNeedle()
    }
}