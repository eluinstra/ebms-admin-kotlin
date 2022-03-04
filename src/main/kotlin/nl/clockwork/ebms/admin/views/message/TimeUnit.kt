package nl.clockwork.ebms.admin.views.message

import lombok.AccessLevel
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.experimental.FieldDefaults
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit
import java.util.function.Function


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
enum class TimeUnit(
    val units: String,
    val timeUnit: TemporalUnit,
    val period: TemporalAmount,
    val dateFormatter: DateTimeFormatter,
    val timeUnitDateFormat: DateTimeFormatter,
    val resetTime: Function<LocalDateTime, LocalDateTime>,
) {
    HOUR("Minutes",
        ChronoUnit.MINUTES,
        Duration.ofHours(1),
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"),
        DateTimeFormatter.ofPattern("mm"),
        Function { t: LocalDateTime ->
            t.withSecond(1)
        }),
    DAY("Hours",
        ChronoUnit.HOURS,
        Duration.ofDays(1),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("HH"),
        Function { t: LocalDateTime ->
            t.withMinute(1)
        }),
    /*WEEK("Days",ChronoUnit.DAYS,Period.ofWeeks(1),DateTimeFormatter.ofPattern(Constants.DATE_FORMAT),DateTimeFormatter.ofPattern("dd")),
	MONTH("Weeks",ChronoUnit.WEEKS,Period.ofMonths(1),DateTimeFormatter.ofPattern(Constants.DATE_FORMAT),DateTimeFormatter.ofPattern("ww")),*/
    MONTH("Days",
        ChronoUnit.DAYS,
        Period.ofMonths(1),
        DateTimeFormatter.ofPattern("MM-yyyy"),
        DateTimeFormatter.ofPattern("dd"),
        Function { t: LocalDateTime ->
            t.withDayOfMonth(1)
        }),
    YEAR("Months",
        ChronoUnit.MONTHS,
        Period.ofYears(1),
        DateTimeFormatter.ofPattern("yyyy"),
        DateTimeFormatter.ofPattern("MM"),
        Function { t: LocalDateTime ->
            t.withMonth(1)
        });

    fun getFrom(): LocalDateTime =
        getFrom(LocalDateTime.now())

    private fun getFrom(dateTime: LocalDateTime): LocalDateTime =
        when (this) {
            HOUR -> dateTime.truncatedTo(ChronoUnit.HOURS).plusHours(1).minus(period)
            DAY -> dateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).minus(period)
            MONTH -> dateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).plusMonths(1).minus(period)
            YEAR -> dateTime.truncatedTo(ChronoUnit.DAYS).withDayOfYear(1).plusYears(1).minus(period)
        }
}