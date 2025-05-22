package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalDate

@Composable
fun CalendarDay(
    modifier: Modifier = Modifier,
    date: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    isSelected: Boolean,
){
    val colors = LocalExtendedColors.current

    Column(
        modifier = modifier
            .width(40.dp)
            .height(61.dp)
            .clip(shape = RoundedCornerShape(100.dp))
            .background(color = if(isSelected) colors.supplementary else MaterialTheme.colorScheme.surface)
            .clickable {
                onDayClick(date)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier,
            text = date.dayOfWeek.toString().take(1),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight(700),
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier,
            text = date.dayOfMonth.toString(),
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
            date = LocalDate.now(),
            isSelected = true,
            onDayClick = {}
        )
    }
}
