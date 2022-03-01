package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.EbMSAttachment
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import nl.ordina.ebms._2.DataSource
import nl.ordina.ebms._2.Message


@Route(value = "service/message/message/:messageId", layout = MainLayout::class)
@PageTitle("Message")
class MessageView : KComposite(), BeforeEnterObserver, WithBean {
    private val root = ui {
        verticalLayout {
            h1(getTranslation("message"))
        }
    }

    override fun beforeEnter(event: BeforeEnterEvent?) {
        val messageId = event?.routeParameters?.get("messageId")?.orElse(null)
        val message = messageId?.let { ebMSMessageClient.getMessage(messageId, null) }
        with(root) {
            message?.let { messageForm(it) } ?: text("Message not found")
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun VerticalLayout.messageForm(message: Message) =
        formLayout {
            setSizeFull()
            with(message.properties) {
                addFormItem(span(messageId), getTranslation("lbl.messageId"))
                addFormItem(span(conversationId), getTranslation("lbl.conversationId"))
                addFormItem(span(refToMessageId), getTranslation("lbl.refToMessageId"))
                addFormItem(span(timestamp.toString()), getTranslation("lbl.timestamp"))
                addFormItem(span(cpaId), getTranslation("lbl.cpaId"))
                addFormItem(span(fromParty.partyId), getTranslation("lbl.fromPartyId"))
                addFormItem(span(fromParty.role), getTranslation("lbl.fromRole"))
                addFormItem(span(toParty.partyId), getTranslation("lbl.toPartyId"))
                addFormItem(span(toParty.role), getTranslation("lbl.toRole"))
                addFormItem(span(service), getTranslation("lbl.service"))
                addFormItem(span(action), getTranslation("lbl.action"))
                addFormItem(span(messageStatus?.name), getTranslation("lbl.status"))
            }
            dataSources(message.dataSource)
        }

    private fun FormLayout.dataSources(attachments: List<DataSource>): Component =
        verticalLayout() {
            setColspan(this, 2) //
            add(formLabel(getTranslation("lbl.attachments")))
            add(dataSourceTable(attachments))
        }

    private fun FormLayout.formLabel(label: String): Label =
        label(label) {
            element.style.set("font-size", "14px")
            element.style.set("font-weight", "400")
            element.style.set("color", "#bbb")
        }

    private fun FormLayout.dataSourceTable(attachments: List<DataSource>): Component =
        Grid<DataSource>(DataSource::class.java, false).apply {
            isAllRowsVisible = true
            setItems(attachments)
            addColumn("attachment.name").setHeader(getTranslation("lbl.name"))
            addColumn("attachment.contentId").setHeader(getTranslation("lbl.contentId"))
            addColumn("attachment.contentType").setHeader(getTranslation("lbl.contentType"))
            //addColumn("content");
        }

}