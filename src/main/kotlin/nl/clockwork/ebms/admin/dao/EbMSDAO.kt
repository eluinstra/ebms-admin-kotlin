package nl.clockwork.ebms.admin.dao

import nl.clockwork.ebms.EbMSMessageStatus
import nl.clockwork.ebms.admin.Cpa
import nl.clockwork.ebms.admin.EbMSAttachment
import nl.clockwork.ebms.admin.EbMSMessage
import nl.clockwork.ebms.admin.EbMSMessageFilter
import nl.clockwork.ebms.admin.views.message.TimeUnit
import org.apache.commons.csv.CSVPrinter
import java.time.LocalDateTime
import java.util.zip.ZipOutputStream


interface EbMSDAO {
    fun findCPA(cpaId: String): Cpa?
    fun countCPAs(): Long
    fun selectCPAIds(): List<String>
    fun selectCPAs(first: Long, count: Int): List<Cpa>

    fun findMessage(messageId: String): EbMSMessage?
    fun existsResponseMessage(messageId: String): Boolean
    fun findResponseMessage(messageId: String): EbMSMessage?
    fun countMessages(filter: EbMSMessageFilter): Long
    fun selectMessages(filter: EbMSMessageFilter, first: Long, count: Int): List<EbMSMessage>
    fun findAttachment(messageId: String, contentId: String): EbMSAttachment?
    fun selectMessageIds(
        cpaId: String,
        fromRole: String,
        toRole: String,
        vararg status: EbMSMessageStatus
    ): List<String>

    fun selectMessageTraffic(
        from: LocalDateTime,
        to: LocalDateTime,
        timeUnit: TimeUnit,
        vararg status: EbMSMessageStatus
    ): Map<Int, Int>

    fun writeMessageToZip(messageId: String, zip: ZipOutputStream)
    fun printMessagesToCSV(printer: CSVPrinter, filter: EbMSMessageFilter)
}
