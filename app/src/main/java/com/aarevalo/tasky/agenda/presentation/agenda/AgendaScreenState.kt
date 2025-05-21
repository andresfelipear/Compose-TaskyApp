package com.aarevalo.tasky.agenda.presentation.agenda

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class AgendaScreenState (
    val date: LocalDate = LocalDate.now(),
    val initials: String = "TT",
)
