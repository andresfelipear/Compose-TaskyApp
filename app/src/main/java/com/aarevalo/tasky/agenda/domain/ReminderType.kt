package com.aarevalo.tasky.agenda.domain

enum class ReminderType(val displayName: String, val minutesBefore: Long) {
    OneHourBefore("1 hour before", 60),
    TenMinutesBefore("10 minutes before", 10),
    ThirtyMinutesBefore("30 minutes before", 30),
    SixHoursBefore("6 hours before", 360),
    OneDayBefore("1 day before", 1440),
}