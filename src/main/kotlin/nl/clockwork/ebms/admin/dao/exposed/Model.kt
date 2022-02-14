package nl.clockwork.ebms.admin.dao.exposed

import nl.clockwork.ebms.EbMSMessageStatus
import nl.clockwork.ebms.delivery.task.DeliveryTaskStatus
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.time.Instant

object Cpas : Table("cpa") {
    val cpaId: Column<String> = varchar("cpa_id", 256)
    val cpa: Column<String> = text("cpa")
}

object UrlMappings : Table("url_mapping") {
    val src: Column<String> = varchar("source", 256)
    val dest: Column<String> = varchar("destination", 256)
}

object CertificateMappings : Table("certificate_mapping") {
    val id: Column<String> = varchar("id", 256)
    val src: Column<ExposedBlob> = blob("source")
    val dest: Column<ExposedBlob> = blob("destination")
    val cpaId: Column<String?> = varchar("cpa_id", 256).nullable()
}

object EbMSMessages : Table("ebms_message") {
    val timestamp: Column<Instant> = timestamp("time_stamp")
    val cpaId: Column<String> = varchar("cpa_id", 256)
    val conversationId: Column<String> = varchar("conversation_id", 256)
    val messageId: Column<String> = varchar("message_id", 256)
    val messageNr: Column<Int> = integer("message_nr")
    val refToMessageId: Column<String?> = varchar("ref_to_message_id", 256).nullable()
    val timeToLive: Column<Instant?> = timestamp("time_to_live").nullable()
    val persistTime: Column<Instant> = timestamp("persist_time")
    val fromPartyId: Column<String> = varchar("from_party_id", 256)
    val fromRole: Column<String?> = varchar("from_role", 256).nullable()
    val toPartyId: Column<String> = varchar("to_party_id", 256)
    val toRole: Column<String?> = varchar("to_role", 256).nullable()
    val service: Column<String> = varchar("service", 256)
    val action: Column<String> = varchar("action", 256)
    val content: Column<String?> = text("content").nullable()
    val status: Column<EbMSMessageStatus?> = enumeration("status", EbMSMessageStatus::class).nullable()
    val statusTime: Column<Instant?> = timestamp("status_time").nullable()
    override val primaryKey = PrimaryKey(messageId, messageNr)
}

object EbMSMessagesX : Table("ebms_message") {
    val id: Column<Int> = integer("id").uniqueIndex()
    val timestamp: Column<Instant> = timestamp("time_stamp")
    val cpaId: Column<String> = varchar("cpa_id", 256)
    val conversationId: Column<String> = varchar("conversation_id", 256)
    val messageId: Column<String> = varchar("message_id", 256)
    val messageNr: Column<Int> = integer("message_nr")
    val refToMessageId: Column<String?> = varchar("ref_to_message_id", 256).nullable()
    val timeToLive: Column<Instant?> = timestamp("time_to_live").nullable()
    val persistTime: Column<Instant> = timestamp("persist_time")
    val fromPartyId: Column<String> = varchar("from_party_id", 256)
    val fromRole: Column<String?> = varchar("from_role", 256).nullable()
    val toPartyId: Column<String> = varchar("to_party_id", 256)
    val toRole: Column<String?> = varchar("to_role", 256).nullable()
    val service: Column<String> = varchar("service", 256)
    val action: Column<String> = varchar("action", 256)
    val content: Column<String?> = text("content").nullable()
    val status: Column<EbMSMessageStatus?> = enumeration("status", EbMSMessageStatus::class).nullable()
    val statusTime: Column<Instant?> = timestamp("status_time").nullable()
    override val primaryKey = PrimaryKey(id)
}

object EbMSAttachments : Table("ebms_attachment") {
    val messageId: Column<String> = varchar("message_id", 256)
    val messageNr: Column<Int> = integer("message_nr")
    val orderNr: Column<Int> = integer("order_nr")
    val name: Column<String?> = varchar("name", 256).nullable()
    val contentId: Column<String> = varchar("content_id", 256)
    val contentType: Column<String> = varchar("content_type", 255)
    val content: Column<ExposedBlob?> = blob("content").nullable()
}

object EbMSAttachmentsX : Table("ebms_attachment") {
    val ebMSMessageId: Column<Int> = integer("id").uniqueIndex()
    val orderNr: Column<Int> = integer("order_nr")
    val name: Column<String?> = varchar("name", 256).nullable()
    val contentId: Column<String> = varchar("content_id", 256)
    val contentType: Column<String> = varchar("content_type", 255)
    val content: Column<ExposedBlob?> = blob("content").nullable()
    //ForeignKeyConstraint(ebMSMessageId to EbMSMessagesX.id)
}

object DeliveryTasks : Table("delivery_task") {
    val cpaId: Column<String> = varchar("cpa_id", 256)
    val sendChannelId: Column<String> = varchar("send_channel_id", 256)
    val receiveChannelId: Column<String> = varchar("receive_channel_id", 256)
    val messageId: Column<String> = varchar("message_id", 256)
    val timeToLive: Column<Instant> = timestamp("time_to_live")
    val timestamp: Column<Instant> = timestamp("time_stamp")
    val isConfidential: Column<Boolean> = bool("is_confidential")
    val retries: Column<Int> = integer("retries")
    val serverId: Column<String> = varchar("server_id", 256)
}

object DeliveryLogs : Table("delivery_log") {
    val messageId: Column<String> = varchar("message_id", 256)
    val timestamp: Column<Instant> = timestamp("time_stamp")
    val uri: Column<String> = varchar("uri", 256)
    val status: Column<DeliveryTaskStatus> = enumeration("status", DeliveryTaskStatus::class)
    val errorMessage: Column<String?> = text("error_message").nullable()
}
