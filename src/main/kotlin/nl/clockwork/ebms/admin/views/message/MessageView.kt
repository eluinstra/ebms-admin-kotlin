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


@Route(value = "message/:messageId", layout = MainLayout::class)
@PageTitle("Message")
class MessageView : KComposite(), BeforeEnterObserver, WithBean {
    private val root = ui {
        verticalLayout {
            h2(getTranslation("message"))
        }
    }

    override fun beforeEnter(event: BeforeEnterEvent?) {
        val messageId = event?.routeParameters?.get("messageId")?.orElse(null)
        val message = messageId?.let { ebMSAdminDAO.findMessage(messageId) }
        with(root) {
            message?.let { messageForm(it) } ?: text("Message not found")
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun VerticalLayout.messageForm(message: EbMSMessage) =
        formLayout {
            setSizeFull()
            with(message) {
                addFormItem(span(messageId), getTranslation("lbl.messageId"))
                addFormItem(span(messageNr.toString()), getTranslation("lbl.messageNr"))
                addFormItem(span(conversationId), getTranslation("lbl.conversationId"))
                addFormItem(span(refToMessageId), getTranslation("lbl.refToMessageId"))
                addFormItem(span(timestamp.toString()), getTranslation("lbl.timestamp"))
                addFormItem(span(cpaId), getTranslation("lbl.cpaId"))
                addFormItem(span(fromPartyId), getTranslation("lbl.fromPartyId"))
                addFormItem(span(fromRole), getTranslation("lbl.fromRole"))
                addFormItem(span(toPartyId), getTranslation("lbl.toPartyId"))
                addFormItem(span(toRole), getTranslation("lbl.toRole"))
                addFormItem(span(service), getTranslation("lbl.service"))
                addFormItem(span(action), getTranslation("lbl.action"))
                addFormItem(span(status?.name), getTranslation("lbl.status"))
                addFormItem(span(statusTime.toString()), getTranslation("lbl.statusTime"))
                message.deliveryTask?.let { deliveryTasks(it) }
                deliveryLogs(deliveryLogs)
                attachments(attachments)
            }
        }

    private fun FormLayout.formLabel(label: String): Label =
        label(label) {
            element.style.set("font-size", "14px")
            element.style.set("font-weight", "400")
            element.style.set("color", "#bbb")
        }

    private fun FormLayout.deliveryTasks(deliveryTask: DeliveryTask): Component =
        verticalLayout() {
            formLabel(getTranslation("lbl.deliveryTasks"))
            deliveryTaskTable(listOf(deliveryTask))
        }

    private fun FormLayout.deliveryTaskTable(deliveryTasks: List<DeliveryTask>): Component =
        Grid(DeliveryTask::class.java, false).apply {
            isAllRowsVisible = true
            setItems(deliveryTasks)
            addColumn("timeToLive").setHeader(getTranslation("lbl.timeToLive"))
            addColumn("timestamp").setHeader(getTranslation("lbl.timestamp"))
            addColumn("retries").setHeader(getTranslation("lbl.retries"))
        }

    private fun FormLayout.deliveryLogs(deliveryLogs: List<DeliveryLog>): Component =
        verticalLayout() {
            setColspan(this, 2) //
            formLabel(getTranslation("lbl.deliveryLog"))
            deliveryLogTable(deliveryLogs)
        }

    private fun FormLayout.deliveryLogTable(deliveryLogs: List<DeliveryLog>): Component =
        Grid(DeliveryLog::class.java, false).apply {
            isAllRowsVisible = true
            setItems(deliveryLogs)
            addColumn("timestamp").setHeader(getTranslation("lbl.timestamp"))
            addColumn("uri").setHeader(getTranslation("lbl.uri"))
            addColumn("status").setHeader(getTranslation("lbl.status"))
            //addColumn("errorMessage");
        }

    private fun FormLayout.attachments(attachments: List<EbMSAttachment>): Component =
        verticalLayout() {
            setColspan(this, 2) //
            add(formLabel(getTranslation("lbl.attachments")))
            add(attachmentsTable(attachments))
        }

    private fun FormLayout.attachmentsTable(attachments: List<EbMSAttachment>): Component =
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