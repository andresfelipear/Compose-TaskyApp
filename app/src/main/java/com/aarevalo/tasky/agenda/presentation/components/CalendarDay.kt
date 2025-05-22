package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.DayOfWeek

@Composable
fun CalendarDay(
    modifier: Modifier = Modifier,
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    onDayClick: (Int) -> Unit
){
    val colors = LocalExtendedColors.current
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(100.dp))
            .background(color = if(isSelected) colors.supplementary else MaterialTheme.colorScheme.surface)
            .padding(spacing.spaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = DayOfWeek.of(day).toString().take(1),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight(700),
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier,
            text = day.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

}

@Composable
@Preview(showBackground = true, apiLevel = 34)
fun CalendarDayPreview(){
    TaskyTheme {
        CalendarDay(
            day = 1,
            isSelected = true,
            isToday = false,
            onDayClick = {}
        )
    }
}
