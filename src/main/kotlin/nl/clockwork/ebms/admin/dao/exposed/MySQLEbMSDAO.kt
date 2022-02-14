package nl.clockwork.ebms.admin.dao.exposed

import nl.clockwork.ebms.admin.EbMSAttachment
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.util.zip.ZipOutputStream

open class MySQLEbMSDAO : AbstractEbMSDAO() {
    override fun findAttachment(messageId: String, messageNr: Int, contentId: String): EbMSAttachment? =
//        EbMSMessagesX.join(EbMSAttachmentsX, JoinType.INNER, additionalConstraint = { EbMSMessagesX.id eq EbMSAttachmentsX.ebMSMessageId })
        (EbMSMessagesX innerJoin EbMSAttachmentsX)
            .slice(EbMSAttachmentsX.columns)
            .select {
                EbMSMessagesX.messageId eq messageId and
                        (EbMSMessagesX.messageNr eq messageNr) and
                        (EbMSMessagesX.id eq EbMSAttachmentsX.ebMSMessageId) and
                        (EbMSAttachmentsX.contentId eq contentId)
            }.map {
                attachment(it)
            }.firstOrNull()

    override fun findAttachments(messageId: String, messageNr: Int) : List<EbMSAttachment> =
        (EbMSMessagesX innerJoin EbMSAttachmentsX)
            .slice(EbMSAttachmentsX.columns)
            .select {
                EbMSMessagesX.messageId eq messageId and
                        (EbMSMessagesX.messageNr eq messageNr) and
                        (EbMSMessagesX.id eq EbMSAttachmentsX.ebMSMessageId)
            }.map {
                attachment(it)
            }

    override fun writeAttachmentsToZip(messageId: String, messageNr: Int, zip: ZipOutputStream) {
        (EbMSMessagesX innerJoin EbMSAttachmentsX)
            .slice(EbMSAttachmentsX.columns)
            .select {
                EbMSMessagesX.messageId eq messageId and
                        (EbMSMessagesX.messageNr eq messageNr) and
                        (EbMSMessagesX.id eq EbMSAttachmentsX.ebMSMessageId)
            }.forEach {
                writeMessageContent(it, zip)
            }
    }
}