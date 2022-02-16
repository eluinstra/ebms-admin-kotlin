package nl.clockwork.ebms.admin.components

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


interface WithDate {
    fun toLocalDateTime(time: Instant): LocalDateTime =
        LocalDateTime.ofInstant(time, ZoneId.systemDefault())

    companion object {
        val DISPLAY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}
