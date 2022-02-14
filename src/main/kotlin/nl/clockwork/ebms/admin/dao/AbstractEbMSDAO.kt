package nl.clockwork.ebms.admin.dao

import nl.clockwork.ebms.EbMSAction
import nl.clockwork.ebms.EbMSMessageStatus
import nl.clockwork.ebms.admin.*
import nl.clockwork.ebms.admin.views.message.TimeUnit
import nl.clockwork.ebms.service.model.Party
import org.apache.commons.csv.CSVPrinter
import org.apache.cxf.io.CachedOutputStream
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.javatime.day
import org.jetbrains.exposed.sql.javatime.hour
import org.jetbrains.exposed.sql.javatime.minute
import org.jetbrains.exposed.sql.javatime.month
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


abstract class AbstractEbMSDAO : EbMSDAO {
    @Transactional("springTransactionManager")
    override fun findCPA(cpaId: String): Cpa? =
        Cpas.select { Cpas.cpaId eq cpaId }
            .map { cpa(it) }
            .firstOrNull()

    @Transactional("springTransactionManager")
    override fun countCPAs(): Long = Cpas.selectAll().count()

    @Transactional("springTransactionManager")
    override fun selectCPAIds(): List<String> =
        Cpas.selectAll().map { it[Cpas.cpaId] }

    @Transactional("springTransactionManager")
    override fun selectCPAs(first: Long, count: Int): List<Cpa> =
        Cpas.selectAll()
            .limit(count, offset = first)
            .map { cpa(it) }

    private fun cpa(row: ResultRow) =
        Cpa(row[Cpas.cpaId], row[Cpas.cpa])

    @Transactional("springTransactionManager")
    override fun findMessage(messageId: String): EbMSMessage? =
        EbMSMessages.select { EbMSMessages.messageId eq messageId }
            .map { message(it) }
            .firstOrNull()

    @Transactional("springTransactionManager")
    override fun findMessage(messageId: String, messageNr: Int): EbMSMessage? =
        EbMSMessages.select { EbMSMessages.messageId eq messageId and (EbMSMessages.messageNr eq messageNr)}
            .map { message(it) }
            .firstOrNull()

    private fun message(row: ResultRow) =
        with(EbMSMessages) {
            EbMSMessage(
                timestamp = row[timestamp],
                cpaId = row[cpaId],
                conversationId = row[conversationId],
                messageId = row[messageId],
                messageNr = row[messageNr],
                refToMessageId = row[refToMessageId],
                timeToLive = row[timeToLive],
                fromPartyId = row[fromPartyId],
                fromRole = row[fromRole],
                toPartyId = row[toPartyId],
                toRole = row[toRole],
                service = row[service],
                action = row[action],
                content = row[content],
                status = row[status],
                statusTime = row[statusTime],
                attachments = findAttachments(row[messageId].toString(), row[messageNr].toInt()),
                deliveryTask = findDeliveryTask(row[messageId]),
                deliveryLogs = findDeliveryLogs(row[messageId])
            )
        }

    protected open fun findAttachments(messageId: String, messageNr: Int) : List<EbMSAttachment> =
        EbMSAttachments.select { EbMSAttachments.messageId eq messageId and (EbMSAttachments.messageNr eq messageNr) }
            .map { attachment(it) }

    private fun cachedOutputStream(input: ByteArray): CachedOutputStream =
        CachedOutputStream().apply {
            write(input)
            lockOutputStream()
        }

    protected fun attachment(row: ResultRow) =
        with(EbMSAttachments) {
            EbMSAttachment(
                name = row[name],
                contentId = row[contentId],
                contentType = row[contentType],
                content = row[content]?.let { cachedOutputStream(it.bytes) }
            )
        }

    private fun findDeliveryTask(messageId: String) : DeliveryTask? {
        fun deliveryTask(row: ResultRow): DeliveryTask =
            with(DeliveryTasks) {
                DeliveryTask(
                    timeToLive = row[timeToLive],
                    timestamp = row[timestamp],
                    retries = row[retries]
                )
            }

        return DeliveryTasks.select { DeliveryTasks.messageId eq messageId }
            .map { deliveryTask(it) }
            .firstOrNull()
    }

    private fun findDeliveryLogs(messageId: String) : List<DeliveryLog> {
        fun deliveryLog(row: ResultRow) : DeliveryLog =
            with(DeliveryLogs) {
                DeliveryLog(
                    timestamp = row[timestamp],
                    uri = row[uri],
                    status = row[status],
                    errorMessage = row[errorMessage]
                )
            }

        return DeliveryLogs.select { DeliveryLogs.messageId eq messageId }
            .map { deliveryLog(it) }
    }

    @Transactional("springTransactionManager")
    override fun existsResponseMessage(messageId: String): Boolean =
        EbMSMessages.select {
            EbMSMessages.refToMessageId eq messageId and
                    (EbMSMessages.messageNr eq 0) and
                    (EbMSMessages.service eq EbMSAction.EBMS_SERVICE_URI)
        }.count() > 0

    @Transactional("springTransactionManager")
    override fun findResponseMessage(messageId: String): EbMSMessage? =
        EbMSMessages.select {
            EbMSMessages.refToMessageId eq messageId and
                    (EbMSMessages.messageNr eq 0) and
                    (EbMSMessages.service eq EbMSAction.EBMS_SERVICE_URI)
        }.map {
            message(it)
        }.firstOrNull()

    @Transactional("springTransactionManager")
    override fun countMessages(filter: EbMSMessageFilter): Long =
        EbMSMessages.select { messageFilter(filter, Op.nullOp()) }.count()

    @Transactional("springTransactionManager")
    override fun selectMessages(filter: EbMSMessageFilter, first: Long, count: Int): List<EbMSMessage> =
        EbMSMessages.select { messageFilter(filter, Op.nullOp()) }
            .orderBy(EbMSMessages.timestamp to SortOrder.DESC)
            .limit(count, first)
            .map { message(it) }

    @Transactional("springTransactionManager")
    override fun findAttachment(messageId: String, messageNr: Int, contentId: String): EbMSAttachment? =
        EbMSAttachments.select {
            EbMSAttachments.messageId eq messageId and
                    (EbMSAttachments.messageNr eq messageNr) and
                    (EbMSAttachments.contentId eq  contentId)
        }.map {
            attachment(it)
        }.firstOrNull()

    @Transactional("springTransactionManager")
    override fun selectMessageIds(
        cpaId: String,
        fromRole: String,
        toRole: String,
        vararg status: EbMSMessageStatus
    ): List<String> =
        EbMSMessages
            .slice(EbMSMessages.messageId)
            .select {
                EbMSMessages.cpaId eq cpaId and
                        (EbMSMessages.fromRole eq fromRole) and
                        (EbMSMessages.toRole eq toRole) and
                        (EbMSMessages.status inList status.asList())}
            .orderBy(EbMSMessages.timestamp to SortOrder.DESC)
            .map { it.toString() }

    @Transactional("springTransactionManager")
    override fun selectMessageTraffic(
        from: LocalDateTime,
        to: LocalDateTime,
        timeUnit: TimeUnit,
        vararg status: EbMSMessageStatus
    ): Map<Int, Int> {
        fun getTimestamp(timeStamp: Column<Instant>, timeUnit: TimeUnit): Function<Int> =
            when (timeUnit) {
                TimeUnit.HOUR -> timeStamp.minute()
                TimeUnit.DAY -> timeStamp.hour()
                TimeUnit.MONTH -> timeStamp.day()
                TimeUnit.YEAR -> timeStamp.month()
            }

        val condition = when {
            status.isEmpty() -> Op.build {
                EbMSMessages.timestamp greaterEq from.atZone(ZoneId.systemDefault()).toInstant() and
                        (EbMSMessages.timestamp less to.atZone(ZoneId.systemDefault()).toInstant()) and
                        EbMSMessages.status.isNotNull()
            }
            else -> Op.build {
                EbMSMessages.timestamp greaterEq from.atZone(ZoneId.systemDefault()).toInstant() and
                        (EbMSMessages.timestamp less to.atZone(ZoneId.systemDefault()).toInstant()) and
                        (EbMSMessages.status inList status.asList())
            }
        }
        val result = EbMSMessages
            .slice(getTimestamp(EbMSMessages.timestamp, timeUnit).alias("time"), EbMSMessages.messageId.count().alias("nr"))
            .select(condition)
            .groupBy(getTimestamp(EbMSMessages.timestamp, timeUnit).alias("time"))
        return result.associate {
            it[getTimestamp(EbMSMessages.timestamp, timeUnit).alias("time")] to it[EbMSMessages.messageId.count().alias("nr")].toInt()
        }
    }

    @Transactional("springTransactionManager")
    override fun printMessagesToCSV(printer: CSVPrinter, filter: EbMSMessageFilter) {
        EbMSMessages.select { messageFilter(filter, Op.nullOp()) }
            .orderBy(EbMSMessages.timestamp to SortOrder.DESC)
            .forEach {
                with(EbMSMessages) {
                    printer.print(it[messageId])
                    printer.print(it[messageNr])
                    printer.print(it[refToMessageId])
                    printer.print(it[conversationId])
                    printer.print(it[timestamp])
                    printer.print(it[timeToLive])
                    printer.print(it[cpaId])
                    printer.print(it[fromRole])
                    printer.print(it[toRole])
                    printer.print(it[service])
                    printer.print(it[action])
                    printer.print(it[status])
                    printer.print(it[statusTime])
                    printer.println()
                }
            }
    }

    private fun messageFilter(filter: EbMSMessageFilter, condition: Op<Boolean>) : Op<Boolean> =
        with(EbMSMessages) {
            var result = applyFilter(filter, condition)
            result = filter.messageNr?.let { result.and { messageNr eq it } } ?: result
            result = if (filter.statuses.isNotEmpty()) result.and { status inList filter.statuses.toList() } else result
            result = filter.serviceMessage?.let {
                if (it) result.and { service eq EbMSAction.EBMS_SERVICE_URI }
                else result.and { service neq EbMSAction.EBMS_SERVICE_URI }
            } ?: result
            result = filter.from?.let { result.and { timestamp greaterEq it.atZone(ZoneId.systemDefault()).toInstant() } } ?: result
            filter.to?.let { result.and { timestamp less it.atZone(ZoneId.systemDefault()).toInstant() } } ?: result
        }

    @Transactional("springTransactionManager")
    override fun writeMessageToZip(messageId: String, messageNr: Int, zip: ZipOutputStream) {
        return EbMSMessages
            .slice(EbMSMessages.content)
            .select { EbMSMessages.messageId eq messageId and (EbMSMessages.messageNr eq messageNr) }
            .forEach { writeMessageContent(it, zip) }
    }

    protected fun writeMessageContent(it: ResultRow, zip: ZipOutputStream) {
        val entry = ZipEntry("message.xml")
        zip.putNextEntry(entry)
        it[EbMSMessages.content]?.run { zip.write(this.toByteArray()) }
        zip.closeEntry()
    }

    protected open fun writeAttachmentsToZip(messageId: String, messageNr: Int, zip: ZipOutputStream) {
        fun fileExtension(contentType: String) =
            "." + (if (contentType.contains("text")) "txt" else contentType.split("/")[1])

        fun zipEntry(it: ResultRow) =
            "attachments/" + if (it[EbMSAttachments.name]?.isNotEmpty() == true)
                it[EbMSAttachments.name]
            else
                it[EbMSAttachments.contentId] + fileExtension(it[EbMSAttachments.contentType].toString())

        fun writeAttachment(it: ResultRow, zip: ZipOutputStream) {
            val entry = ZipEntry(zipEntry(it))
            entry.comment = "Content-Type: " + it[EbMSAttachments.contentType]
            zip.putNextEntry(entry)
            it[EbMSAttachments.content]?.run { zip.write(this.bytes) }
            zip.closeEntry()
        }

        return EbMSAttachments.select { EbMSAttachments.messageId eq messageId and (EbMSAttachments.messageNr eq messageNr) }
            .forEach { writeAttachment(it, zip) }
    }

    companion object {
        private fun applyFilter(filter: EbMSMessageFilter, condition: Op<Boolean>): Op<Boolean> {
            fun applyPathFilter(partyId: Column<String>, role: Column<String?>, party: Party?, condition: Op<Boolean>) : Op<Boolean> =
                party?.let {
                    val result = condition.and(partyId eq it.partyId)
                    it.role?.run { result.and(role eq this) } ?: result
                } ?: condition

            return with(EbMSMessages) {
                var result = filter.cpaId?.let { condition.and(cpaId eq it) } ?: condition
                result = applyPathFilter(fromPartyId, fromRole, filter.fromParty, result)
                result = applyPathFilter(toPartyId, toRole, filter.toParty, result)
                result = filter.service?.let { result.and(service eq it) } ?: result
                result = filter.action?.let { result.and(action eq it) } ?: result
                result = filter.conversationId?.let { result.and(conversationId eq it) } ?: result
                result = filter.messageId?.let { result.and(messageId eq it) } ?: result
                result = filter.refToMessageId?.let { result.and(refToMessageId eq it) } ?: result
                if (filter.statuses.isNotEmpty()) result.and(status inList filter.statuses.toList()) else result
            }
        }
    }
}