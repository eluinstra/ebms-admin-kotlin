package nl.clockwork.ebms.admin.views.message

import nl.clockwork.ebms.EbMSMessageStatus
import java.awt.Color
import java.time.LocalDateTime


class TrafficChartConfig(
    var timeUnit: TimeUnit,
    var from: LocalDateTime,
    var ebMSMessageTrafficChartOption: EbMSMessageTrafficChartOption
) {
    fun getTimeUnits(): List<TimeUnit> =
        TimeUnit.values().asList()

    val to: LocalDateTime
        get() = from.plus(timeUnit.period)

    fun getEbMSMessageTrafficChartOptions(): List<EbMSMessageTrafficChartOption> {
        return EbMSMessageTrafficChartOption.values().asList()
    }

    fun previousPeriod() {
        from = from.minus(timeUnit.period)
    }

    fun nextPeriod() {
        from = from.plus(timeUnit.period)
    }

    fun resetFrom() {
        from = timeUnit.resetTime.apply(from)
    }

    companion object {
        fun of(timeUnit: TimeUnit, ebMSMessageTrafficChartOption: EbMSMessageTrafficChartOption): TrafficChartConfig {
            val from = timeUnit.getFrom()
            return TrafficChartConfig(timeUnit, from, ebMSMessageTrafficChartOption)
        }

        fun createChartTitle(config: TrafficChartConfig): String {
            return config.ebMSMessageTrafficChartOption
                .title + " " + config.timeUnit.dateFormatter.format(config.from)
        }
    }
}

enum class EbMSMessageTrafficChartOption(
    val title: String,
    val ebMSMessageTrafficChartSeries: Array<EbMSMessageTrafficChartSerie>
) {
    ALL(
        "All Messages",
        arrayOf(
            EbMSMessageTrafficChartSerie.TOTAL_STATUS,
            EbMSMessageTrafficChartSerie.RECEIVE_STATUS,
            EbMSMessageTrafficChartSerie.SEND_STATUS
        )
    ),
    RECEIVED(
        "Received Messages",
        arrayOf(
            EbMSMessageTrafficChartSerie.RECEIVE_STATUS_NOK,
            EbMSMessageTrafficChartSerie.RECEIVE_STATUS_WARN,
            EbMSMessageTrafficChartSerie.RECEIVE_STATUS_OK,
            EbMSMessageTrafficChartSerie.RECEIVE_STATUS
        )
    ),
    CREATED(
        "Created Messages",
        arrayOf(
            EbMSMessageTrafficChartSerie.SEND_STATUS_NOK,
            EbMSMessageTrafficChartSerie.SEND_STATUS_WARN,
            EbMSMessageTrafficChartSerie.SEND_STATUS_OK,
            EbMSMessageTrafficChartSerie.SEND_STATUS
        )
    )

}

enum class EbMSMessageTrafficChartSerie(
    val serie: String,
    var color: String,
    var colorX: Color,
    var ebMSMessageStatuses: Array<EbMSMessageStatus>
) {
    TOTAL_STATUS("Total", "#ffcd56", Color.YELLOW, EbMSMessageStatus.values()),
    RECEIVE_STATUS_OK(
        "Ok",
        "#4bc0c0",
        Color.GREEN,
        arrayOf(EbMSMessageStatus.PROCESSED, EbMSMessageStatus.FORWARDED)
    ),
    RECEIVE_STATUS_WARN(
        "Warn",
        "#ff9f40",
        Color.ORANGE,
        arrayOf(EbMSMessageStatus.RECEIVED)
    ),
    RECEIVE_STATUS_NOK(
        "Failed",
        "#ff6384",
        Color.RED,
        arrayOf(EbMSMessageStatus.UNAUTHORIZED, EbMSMessageStatus.NOT_RECOGNIZED, EbMSMessageStatus.FAILED)
    ),
    RECEIVE_STATUS(
        "Received",
        "#c9cbcf",
        Color.BLACK,
        arrayOf(
            EbMSMessageStatus.UNAUTHORIZED,
            EbMSMessageStatus.NOT_RECOGNIZED,
            EbMSMessageStatus.RECEIVED,
            EbMSMessageStatus.PROCESSED,
            EbMSMessageStatus.FORWARDED,
            EbMSMessageStatus.FAILED
        )
    ),
    SEND_STATUS_OK("Ok", "#4bc0c0", Color.GREEN, arrayOf(EbMSMessageStatus.DELIVERED)), SEND_STATUS_WARN(
        "Warn",
        "#ff9f40",
        Color.ORANGE,
        arrayOf(EbMSMessageStatus.CREATED)
    ),
    SEND_STATUS_NOK(
        "Failed",
        "#ff6384",
        Color.RED,
        arrayOf(EbMSMessageStatus.DELIVERY_FAILED, EbMSMessageStatus.EXPIRED)
    ),
    SEND_STATUS(
        "Sending",
        "#36a2eb",
        Color.BLUE,
        arrayOf(
            EbMSMessageStatus.CREATED,
            EbMSMessageStatus.DELIVERED,
            EbMSMessageStatus.DELIVERY_FAILED,
            EbMSMessageStatus.EXPIRED
        )
    )
}
