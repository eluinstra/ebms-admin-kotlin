package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.*
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.components.confirmDialog
import nl.clockwork.ebms.admin.components.downloadButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import nl.clockwork.ebms.jaxb.JAXBParser
import nl.ordina.ebms._2.DataSource
import nl.ordina.ebms._2.Message
import nl.ordina.ebms._2.MessageProperties
import org.apache.cxf.io.CachedOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.xml.bind.JAXBElement
import javax.xml.namespace.QName


@Route(value = "service/message/message/:messageId", layout = MainLayout::class)
@PageTitle("Message")
class MessageView : KComposite(), BeforeEnterObserver, WithBean {
    private val root = ui {
        verticalLayout {
            h2(getTranslation("message"))
        }
    }

    override fun beforeEnter(event: BeforeEnterEvent?) {
        val messageId = event?.routeParameters?.get("messageId")?.orElse(null)
        val message = messageId?.let { ebMSMessageClient.getMessage(messageId, null) }
        with(root) {
            message?.let {
                messageForm(it)
                horizontalLayout {
                    backButton(getTranslation("cmd.back"))
                    downloadButton(getTranslation("cmd.download"), StreamResource("message.${message.properties.messageId}.zip", createZip(message)))
                    button(getTranslation("cmd.process")) {
                        onLeftClick {
                            confirmDialog {
                                ebMSMessageClient.processMessage(messageId)
                                navigateTo(UnprocessedMessagesView::class)
                            }
                        }
                    }
                }
            } ?: text("Message not found")}
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
            setColspan(this, 2)
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
//            addColumn("attachment.contentId").setHeader(getTranslation("lbl.contentId"))
            addColumn("attachment.contentType").setHeader(getTranslation("lbl.contentType"))
            //addColumn("content");
        }

    private fun writeMessageToZip(message: Message, zip: ZipOutputStream) {
        val entry = ZipEntry("messageProperties.xml")
        zip.putNextEntry(entry)
        zip.write(
            JAXBParser.getInstance(MessageProperties::class.java).handle(
                JAXBElement(
                    QName("http://www.ordina.nl/ebms/2.18", "messageProperties"),
                    MessageProperties::class.java, message.properties
                )
            ).toByteArray()
        )
        zip.closeEntry()
        for (dataSource in message.dataSource) {
            val e = ZipEntry(
                "datasources/" + if (dataSource.attachment.name.isNullOrEmpty())
                    UUID.randomUUID().toString() + nl.clockwork.ebms.admin.views.Utils.getFileExtension(dataSource.attachment.contentType)
                else
                    dataSource.attachment.name
            )
            entry.comment = "Content-Type: " + dataSource.attachment.contentType
            zip.putNextEntry(e)
            zip.write(dataSource.attachment.dataSource.inputStream.readAllBytes())
            zip.closeEntry()
        }
        zip.finish()
    }

    private fun createZip(message: Message) : InputStreamFactory =
        InputStreamFactory {
            CachedOutputStream().use { output ->
                ZipOutputStream(output).use { zip ->
                    writeMessageToZip(message, zip)
                }
                output.inputStream
            }
        }

    companion object {
        fun messageIdLink(): ComponentRenderer<RouterLink, String> =
            ComponentRenderer { messageId -> messageRouterLink(messageId) }

        private fun messageRouterLink(messageId: String): RouterLink =
            RouterLink(messageId, MessageView::class.java, RouteParameters("messageId", messageId))
    }
}