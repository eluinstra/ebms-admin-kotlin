package nl.clockwork.ebms.admin.components

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

class DateProgression(override val start: LocalDateTime,
                      override val endInclusive: LocalDateTime,
                      private val stepPeriod: TemporalAmount = Duration.ofDays(1)) :
    Iterable<LocalDateTime>, ClosedRange<LocalDateTime> {
    override fun iterator(): Iterator<LocalDateTime> =
        DateIterator(start, endInclusive, stepPeriod)
    infix fun step(period: TemporalAmount) = DateProgression(start, endInclusive, stepPeriod)
}

class DateIterator(
    private val startTime: LocalDateTime,
    private val endTime: LocalDateTime,
    private val period: TemporalAmount
) : Iterator<LocalDateTime> {
    private var currentTime = startTime

    override fun hasNext(): Boolean = currentTime < endTime

    override fun next(): LocalDateTime {
        val result = currentTime
        currentTime = currentTime.plus(period)
        return result
    }

}