package nl.clockwork.ebms.admin

import nl.clockwork.ebms.EbMSMessageStatus
import nl.clockwork.ebms.delivery.task.DeliveryTaskStatus
import nl.clockwork.ebms.service.model.MessageFilter
import nl.clockwork.ebms.service.model.Party
import org.apache.cxf.io.CachedOutputStream
import java.time.Instant
import java.time.LocalDateTime

data class Cpa(val cpaId: String, val cpa: String)

data class EbMSMessage(
    val timestamp: Instant,
    val cpaId: String,
    val conversationId: String,
    val messageId: String,
    val messageNr: Int,
    val refToMessageId: String?,
    val timeToLive: Instant?,
    val fromPartyId: String,
    val fromRole: String?,
    val toPartyId: String,
    val toRole: String?,
    val service: String,
    val action: String,
    val content: String?,
    val status: EbMSMessageStatus?,
    val statusTime: Instant?,
    val attachments: List<EbMSAttachment>,
    val deliveryTask: DeliveryTask?,
    val deliveryLogs: List<DeliveryLog>
)

data class EbMSAttachment(
    val name: String?,
    val contentId: String,
    val contentType: String,
    val content: CachedOutputStream?
)

data class DeliveryTask(
    val timeToLive: Instant,
    val timestamp: Instant,
    val retries: Int?
)

data class DeliveryLog(
    val timestamp: Instant,
    val uri: String,
    val status: DeliveryTaskStatus,
    val errorMessage: String?
)

class EbMSMessageFilter(
    val messageNr: Int? = null,
    val serviceMessage: Boolean? = null,
    val statuses: Set<EbMSMessageStatus> = emptySet(),
    val from: LocalDateTime? = null,
    val to: LocalDateTime? = null,
    cpaId: String? = null,
    fromParty: Party? = null,
    toParty: Party? = null,
    service: String? = null,
    action: String? = null,
    conversationId: String? = null,
    messageId: String? = null,
    refToMessageId: String? = null

) : MessageFilter(
    cpaId,
    fromParty,
    toParty,
    service,
    action,
    conversationId,
    messageId,
    refToMessageId
)