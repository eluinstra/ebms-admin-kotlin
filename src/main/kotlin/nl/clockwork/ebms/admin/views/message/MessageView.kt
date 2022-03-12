package nl.clockwork.ebms.admin.views.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.data.renderer.ComponentRenderer
import nl.clockwork.ebms.admin.DeliveryLog
import nl.clockwork.ebms.admin.DeliveryTask
import nl.clockwork.ebms.admin.EbMSAttachment
import nl.clockwork.ebms.admin.EbMSMessage
import nl.clockwork.ebms.admin.components.aLabel
import nl.clockwork.ebms.admin.components.closeButton


fun messageDetailsRenderer(): ComponentRenderer<MessageForm, EbMSMessage> =
    ComponentRenderer {
            message -> MessageForm(message)
    }

class MessageForm(message: EbMSMessage) : FormLayout() {
    init {
        setResponsiveSteps(ResponsiveStep("0", 2))
        messageFields(message)
    }
}

private fun FormLayout.messageFields(message: EbMSMessage) {
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
        addFormItem(span(statusTime?.toString()), getTranslation("lbl.statusTime"))
    }
}

fun messageDialog(message: EbMSMessage) =
    Dialog().apply {
        width = "80%"
        height = "80%"
        formLayout {
            setResponsiveSteps(FormLayout.ResponsiveStep("0", 2))
            messageFields(message)
            message.deliveryTask?.let { deliveryTask(it) }
            deliveryLogs(message.deliveryLogs)
            attachments(message.attachments)
        }
        closeButton(getTranslation("cmd.close")) {
            addClickListener{ _ -> this@apply.close() }
        }
    }

private fun FormLayout.deliveryTask(deliveryTask: DeliveryTask): Component =
    verticalLayout {
        setColspan(this, 2) //
        add(aLabel(getTranslation("lbl.deliveryTask")))
        add(deliveryTaskTable(listOf(deliveryTask)))
    }

private fun deliveryTaskTable(deliveryTasks: List<DeliveryTask>): Component =
    Grid(DeliveryTask::class.java, false).apply {
        isAllRowsVisible = true
        setItems(deliveryTasks)
        addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER)
        addColumn("timeToLive").setHeader(getTranslation("lbl.timeToLive"))
        addColumn("timestamp").setHeader(getTranslation("lbl.timestamp"))
        addColumn("retries").setHeader(getTranslation("lbl.retries"))
    }

private fun FormLayout.deliveryLogs(deliveryLogs: List<DeliveryLog>): Component =
    verticalLayout {
        setColspan(this, 2) //
        add(aLabel(getTranslation("lbl.deliveryLog")))
        add(deliveryLogTable(deliveryLogs))
    }

private fun deliveryLogTable(deliveryLogs: List<DeliveryLog>): Component =
    Grid(DeliveryLog::class.java, false).apply {
        isAllRowsVisible = true
        setItems(deliveryLogs)
        addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER)
        addColumn("timestamp").setHeader(getTranslation("lbl.timestamp"))
        addColumn("uri").setHeader(getTranslation("lbl.uri"))
        addColumn("status").setHeader(getTranslation("lbl.status"))
        //addColumn("errorMessage");
    }

private fun FormLayout.attachments(attachments: List<EbMSAttachment>): Component =
    verticalLayout {
        setColspan(this, 2) //
        add(aLabel(getTranslation("lbl.attachments")))
        add(attachmentsTable(attachments))
    }

private fun attachmentsTable(attachments: List<EbMSAttachment>): Component =
    Grid(EbMSAttachment::class.java, false).apply {
        isAllRowsVisible = true
        setItems(attachments)
        addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER)
        addColumn("name").setHeader(getTranslation("lbl.name"))
        addColumn("contentId").setHeader(getTranslation("lbl.contentId"))
        addColumn("contentType").setHeader(getTranslation("lbl.contentType"))
        //addColumn("content");
    }
