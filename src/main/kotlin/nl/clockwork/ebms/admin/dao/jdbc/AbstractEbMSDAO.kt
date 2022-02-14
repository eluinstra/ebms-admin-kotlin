package nl.clockwork.ebms.admin.dao.jdbc

import nl.clockwork.ebms.EbMSMessageStatus
import nl.clockwork.ebms.admin.Cpa
import nl.clockwork.ebms.admin.EbMSAttachment
import nl.clockwork.ebms.admin.EbMSMessage
import nl.clockwork.ebms.admin.EbMSMessageFilter
import nl.clockwork.ebms.admin.dao.EbMSDAO
import nl.clockwork.ebms.admin.views.message.TimeUnit
import org.apache.commons.csv.CSVPrinter
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.Repository
import java.time.LocalDateTime
import java.util.zip.ZipOutputStream

interface AbstractCpaDAO : EbMSDAO, Repository<Cpa, String> {
    @Query("select * from cpa where cpaId = :cpaId")
    override fun findCPA(cpaId: String): Cpa?

    @Query("select count(*) from cpa")
    override fun countCPAs(): Long

    @Query("select cpaId from cpa")
    override fun selectCPAIds(): List<String>

    @Query("select * from cpa")
    override fun selectCPAs(first: Long, count: Long): List<Cpa>
}

interface AbstractEbMSMessageDAO : EbMSDAO, Repository<EbMSMessage, String> {
    override fun findMessage(messageId: String): EbMSMessage?

    override fun findMessage(messageId: String, messageNr: Int): EbMSMessage?

    override fun existsResponseMessage(messageId: String): Boolean

    override fun findResponseMessage(messageId: String): EbMSMessage?

    override fun countMessages(filter: EbMSMessageFilter): Long

    override fun selectMessages(filter: EbMSMessageFilter, first: Long, count: Int): List<EbMSMessage>

    override fun selectMessageIds(
        cpaId: String,
        fromRole: String,
        toRole: String,
        vararg status: EbMSMessageStatus
    ): List<String>

    override fun selectMessageTraffic(
        from: LocalDateTime,
        to: LocalDateTime,
        timeUnit: TimeUnit,
        vararg status: EbMSMessageStatus
    ): Map<Int, Int>

    override fun printMessagesToCSV(printer: CSVPrinter, filter: EbMSMessageFilter)

    override fun writeMessageToZip(messageId: String, messageNr: Int, zip: ZipOutputStream)
}

interface AbstractEbMSAttachmentDAO : EbMSDAO, Repository<EbMSAttachment, String> {
    override fun findAttachment(messageId: String, messageNr: Int, contentId: String): EbMSAttachment?
}
