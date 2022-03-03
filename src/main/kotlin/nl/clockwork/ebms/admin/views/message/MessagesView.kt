package nl.clockwork.ebms.admin.views.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid.SelectionMode.NONE
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import nl.clockwork.ebms.admin.EbMSMessage
import nl.clockwork.ebms.admin.EbMSMessageFilter
import nl.clockwork.ebms.admin.components.WithDate
import nl.clockwork.ebms.admin.components.WithDate.Companion.DISPLAY_DATE_TIME_FORMATTER
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.components.downloadButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import nl.clockwork.ebms.admin.views.message.SearchFilter.searchFilter
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.cxf.io.CachedOutputStream
import java.io.OutputStreamWriter


@Route(value = "message", layout = MainLayout::class)
@PageTitle("Messages")
class MessagesView : KComposite(), WithBean, WithDate {
    val root = ui {
        verticalLayout {
            setSizeFull()
            h2(getTranslation("messages"))
            val messageFilter = EbMSMessageFilter()
            val dataProvider = createMessageDataProvider(messageFilter)
            createSearchFilterDetails(getTranslation("messageFilter"), messageFilter, dataProvider)
            createMessageGrid(dataProvider)
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                downloadButton(getTranslation("cmd.download"), StreamResource("messages.csv", createCsv()))
            }
        }
    }

    private fun createMessageDataProvider(messageFilter: EbMSMessageFilter): DataProvider<EbMSMessage, *> =
        DataProvider.fromCallbacks(
            { query: Query<EbMSMessage, Void> ->
                ebMSAdminDAO.selectMessages(
                    messageFilter,
                    query.offset.toLong(),
                    query.limit
                ).stream()
            },
            { ebMSAdminDAO.countMessages(messageFilter).toInt() }
        )

    private fun HasComponents.createSearchFilterDetails(
        label: String,
        messageFilter: EbMSMessageFilter,
        dataProvider: DataProvider<EbMSMessage, *>
    ): Component =
        details(label) {
            isOpened = false
            content {
                searchFilter(messageFilter, dataProvider) { }
            }
        }

    private fun HasComponents.createMessageGrid(dataProvider: DataProvider<EbMSMessage, *>): Component =
        grid(EbMSMessage::class.java) {
            setDataProvider(dataProvider)
            setSelectionMode(NONE)
            addColumn(MessageView.messageIdLink())
                .setHeader(getTranslation("lbl.messageId")).setAutoWidth(true).isFrozen = true
            addColumn("messageNr")
                .setHeader(getTranslation("lbl.messageNr")).setAutoWidth(true).textAlign = ColumnTextAlign.END
            addColumn("conversationId")
                .setHeader(getTranslation("lbl.conversationId")).isAutoWidth = true
            addColumn("refToMessageId")
                .setHeader(getTranslation("lbl.refToMessageId")).isAutoWidth = true
            addColumn(LocalDateTimeRenderer({ m -> toLocalDateTime(m.timestamp) }, DISPLAY_DATE_TIME_FORMATTER))
                .setHeader(getTranslation("lbl.timestamp")).isAutoWidth = true
            addColumn("cpaId")
                .setHeader(getTranslation("lbl.cpaId")).isAutoWidth = true
            addColumn("fromPartyId")
                .setHeader(getTranslation("lbl.fromPartyId")).isAutoWidth = true
            addColumn("fromRole")
                .setHeader(getTranslation("lbl.fromRole")).isAutoWidth = true
            addColumn("toPartyId")
                .setHeader(getTranslation("lbl.toPartyId")).isAutoWidth = true
            addColumn("toRole")
                .setHeader(getTranslation("lbl.toRole")).isAutoWidth = true
            addColumn("service")
                .setHeader(getTranslation("lbl.service")).isAutoWidth = true
            addColumn("action")
                .setHeader(getTranslation("lbl.action")).isAutoWidth = true
            addColumn("status")
                .setHeader(getTranslation("lbl.status")).isAutoWidth = true
            addColumn(LocalDateTimeRenderer({ m -> m.statusTime?.let { toLocalDateTime(m.statusTime) } }, DISPLAY_DATE_TIME_FORMATTER))
                .setHeader(getTranslation("lbl.statusTime"))
                .setAutoWidth(true)
        }

    private fun createCsv(): InputStreamFactory =
        InputStreamFactory {
            CachedOutputStream().use { output ->
                CSVPrinter(OutputStreamWriter(output), CSVFormat.DEFAULT).use { printer ->
                    ebMSAdminDAO.printMessagesToCSV(printer, EbMSMessageFilter())
                    printer.flush()
                    output.inputStream
                }
            }
        }
}