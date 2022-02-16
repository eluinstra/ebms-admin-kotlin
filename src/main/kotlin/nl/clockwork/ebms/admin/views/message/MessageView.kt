package nl.clockwork.ebms.admin.views.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.*
import nl.clockwork.ebms.admin.DeliveryLog
import nl.clockwork.ebms.admin.DeliveryTask
import nl.clockwork.ebms.admin.EbMSAttachment
import nl.clockwork.ebms.admin.EbMSMessage
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import java.time.Instant


@Route(value = "message/:messageId", layout = MainLayout::class)
@PageTitle("Message")
class MessageView : KComposite(), BeforeEnterObserver, WithBean {
    private val root = ui {
        verticalLayout {
            h1(getTranslation("message"))
        }
    }

    override fun beforeEnter(event: BeforeEnterEvent?) {
        val messageId = event?.routeParameters?.get("messageId")?.orElse(null)
        val message = messageId?.let { ebMSAdminDAO!!.findMessage(messageId) }
        with(root) {
            message?.let { messageForm(it) } ?: text("Message not found")
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun VerticalLayout.messageForm(message: EbMSMessage) =
        formLayout {
            setSizeFull()
            with(message) {
                createField(getTranslation("lbl.messageId"), messageId)
                createField(getTranslation("lbl.messageNr"), messageNr.toString())
                createField(getTranslation("lbl.conversationId"), conversationId)
                createField(getTranslation("lbl.refToMessageId"), refToMessageId)
                createField(getTranslation("lbl.timestamp"), timestamp)
                createField(getTranslation("lbl.cpaId"), cpaId)
                createField(getTranslation("lbl.fromPartyId"), fromPartyId)
                createField(getTranslation("lbl.fromRole"), fromRole)
                createField(getTranslation("lbl.toPartyId"), toPartyId)
                createField(getTranslation("lbl.toRole"), toRole)
                createField(getTranslation("lbl.service"), service)
                createField(getTranslation("lbl.action"), action)
                createField(getTranslation("lbl.status"), status?.name)
                createField(getTranslation("lbl.statusTime"), statusTime)
                message.deliveryTask?.let { createDeliveryTask(it) }
                createDeliveryLogs(deliveryLogs)
                createAttachments(attachments)
            }
        }

    private fun FormLayout.createField(label: String, value: Instant?): Component =
        createField(label, value?.toString())

    private fun FormLayout.createField(label: String, value: String?): Component =
        horizontalLayout() {
            setColspan(this, 2)
            createLabel(label)
            span(value)
        }

    private fun FormLayout.createLabel(label: String): Label =
        label(label) {
            element.style.set("font-weight", "bold")
        }

    private fun FormLayout.createDeliveryTask(deliveryTask: DeliveryTask): Component =
        verticalLayout() {
            createLabel(getTranslation("lbl.deliveryTasks"))
            createDeliveryTaskTable(listOf(deliveryTask))
        }

    private fun FormLayout.createDeliveryTaskTable(deliveryTasks: List<DeliveryTask>): Component =
        Grid(DeliveryTask::class.java, false).apply {
            isAllRowsVisible = true
            setItems(deliveryTasks)
            addColumn("timeToLive").setHeader(getTranslation("lbl.timeToLive"))
            addColumn("timestamp").setHeader(getTranslation("lbl.timestamp"))
            addColumn("retries").setHeader(getTranslation("lbl.retries"))
        }

    private fun FormLayout.createDeliveryLogs(deliveryLogs: List<DeliveryLog>): Component =
        verticalLayout() {
            setColspan(this, 2) //
            createLabel(getTranslation("lbl.deliveryLog"))
            createDeliveryLogTable(deliveryLogs)
        }

    private fun FormLayout.createDeliveryLogTable(deliveryLogs: List<DeliveryLog>): Component =
        Grid(DeliveryLog::class.java, false).apply {
            isAllRowsVisible = true
            setItems(deliveryLogs)
            addColumn("timestamp").setHeader(getTranslation("lbl.timestamp"))
            addColumn("uri").setHeader(getTranslation("lbl.uri"))
            addColumn("status").setHeader(getTranslation("lbl.status"))
            //addColumn("errorMessage");
        }

    private fun FormLayout.createAttachments(attachments: List<EbMSAttachment>): Component =
        verticalLayout() {
            setColspan(this, 2) //
            add(createLabel(getTranslation("lbl.attachments")))
            add(createAttachmentsTable(attachments))
        }

    private fun FormLayout.createAttachmentsTable(attachments: List<EbMSAttachment>): Component =
        Grid<EbMSAttachment>(EbMSAttachment::class.java, false).apply {
            isAllRowsVisible = true
            setItems(attachments)
            addColumn("name").setHeader(getTranslation("lbl.name"))
            addColumn("contentId").setHeader(getTranslation("lbl.contentId"))
            addColumn("contentType").setHeader(getTranslation("lbl.contentType"))
            //addColumn("content");
        }
    companion object {
        fun messageIdLink(): ComponentRenderer<RouterLink, EbMSMessage> =
            ComponentRenderer { message -> messageRouterLink(MessageView::class.java, message.messageId) }

        private fun messageRouterLink(messageView: Class<MessageView>, messageId: String): RouterLink =
            RouterLink(messageId, messageView, RouteParameters("messageId", messageId))
    }
}