package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalDate

private const val OFFSET_TO_REVEAL_FROM_PREVIOUS_ITEM_DP = 30

@Composable
fun CalendarDaysSelector(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDaySelected: (LocalDate) -> Unit,
    days: List<LocalDate>,
){
    val density = LocalDensity.current
    val offsetToRevealPreviousItemDp = OFFSET_TO_REVEAL_FROM_PREVIOUS_ITEM_DP.dp
    val lazyListState = rememberLazyListState()

    LaunchedEffect(selectedDate, days) {
        val selectedIndex = days.indexOf(selectedDate)
        if (selectedIndex != -1) {
            val offsetPx = with(density) { offsetToRevealPreviousItemDp.toPx().toInt() }

            lazyListState.animateScrollToItem(
                index = selectedIndex,
                scrollOffset = -offsetPx
            )
        }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues( end = 8.dp, start = 8.dp, bottom = 12.dp, top = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        state = lazyListState
    ) {
        items(days.size) {
            CalendarDay(
                date = days[it],
                isSelected = selectedDate == days[it],
                onDayClick = { dateSelected ->
                    onDaySelected(dateSelected)
                },
            )
        }
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 34)
fun CalendarDaysSelectorPreview(){
    TaskyTheme {
        CalendarDaysSelector(
            selectedDate = LocalDate.now(),
            onDaySelected = {},
            days = generateSequence(LocalDate.now().minusDays(15)) {
                it.plusDays(1)
            }.takeWhile({ !it.isAfter(LocalDate.now().plusDays(15))})
                .toList()
        )
    }

}