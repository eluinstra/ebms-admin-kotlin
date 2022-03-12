package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import nl.clockwork.ebms.admin.components.*
import nl.clockwork.ebms.jaxb.JAXBParser
import nl.ordina.ebms._2.DataSource
import nl.ordina.ebms._2.Message
import nl.ordina.ebms._2.MessageProperties
import nl.ordina.ebms._2_18.EbMSMessageService
import org.apache.cxf.io.CachedOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.xml.bind.JAXBElement
import javax.xml.namespace.QName


fun messageDetailsRenderer(ebMSMessageClient: EbMSMessageService): ComponentRenderer<MessageForm, String> =
    ComponentRenderer {
        messageId -> MessageForm(ebMSMessageClient.getMessage(messageId, false))
    }

class MessageForm(message: Message) : FormLayout() {
    init {
        setResponsiveSteps(ResponsiveStep("0", 2))
        messageProperties(message.properties)
    }
}

private fun FormLayout.messageProperties(properties: MessageProperties) =
    with(properties) {
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

fun messageDialog(message: Message, onProcess: () -> Unit = {}) =
    Dialog().apply {
        width = "80%"
        height = "80%"
        formLayout {
            setResponsiveSteps(FormLayout.ResponsiveStep("0", 2))
            messageProperties(message.properties)
            dataSources(message.dataSource)
        }
        horizontalLayout {
            closeButton(getTranslation("cmd.close")) {
                addClickListener{ _ -> this@apply.close() }
            }
            downloadButton(getTranslation("cmd.download"), StreamResource("message.${message.properties.messageId}.zip", createZip(message)))
            button(getTranslation("cmd.process"), Icon("lumo", "checkmark")) {
                onLeftClick {
                    confirmDialog {
                        onProcess()
                        this@apply.close()
                    }
                }
            }
        }
    }

private fun FormLayout.dataSources(attachments: List<DataSource>): Component =
    verticalLayout {
        setColspan(this, 2)
        add(aLabel(getTranslation("lbl.attachments")))
        add(dataSourceTable(attachments))
    }

private fun dataSourceTable(attachments: List<DataSource>): Component =
    Grid(DataSource::class.java, false).apply {
        isAllRowsVisible = true
        setItems(attachments)
        addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER)
        addColumn("attachment.name").setHeader(getTranslation("lbl.name"))
//        addColumn("attachment.contentId").setHeader(getTranslation("lbl.contentId"))
        addColumn("attachment.contentType").setHeader(getTranslation("lbl.contentType"))
//        addColumn("content");
    }

private fun writeMessageToZip(message: Message, zip: ZipOutputStream) {
    val entry = ZipEntry("messageProperties.xml")
    zip.putNextEntry(entry)
    zip.write(
        JAXBParser.getInstance(MessageProperties::class.java).handle(
            JAXBElement(
                QName("http://www.ordina.nl/ebms/2.18", "messageProperties"),
                MessageProperties::class.java,
                message.properties
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

