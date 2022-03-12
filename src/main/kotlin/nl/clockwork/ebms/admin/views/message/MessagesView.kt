package nl.clockwork.ebms.admin.views.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid.SelectionMode.NONE
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.data.provider.DataProvider
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
            { query ->
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
                add(SearchFilter(messageFilter) {
                    dataProvider.refreshAll()
                    //TODO grid refresh
                })
            }
        }

    private fun HasComponents.createMessageGrid(dataProvider: DataProvider<EbMSMessage, *>): Component =
        grid(dataProvider) {
            gridContextMenu() {
                item(getTranslation("cmd.details")) {
                    addMenuItemClickListener {
                            e -> messageDialog(e.item.get()).open()
                    }
                }
            }
            setSelectionMode(NONE)
            addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES)
            addColumn("messageId").apply {
                setHeader(getTranslation("lbl.messageId"))
                isAutoWidth = true
                isFrozen = true
            }
            addColumn("messageNr").apply {
                setHeader(getTranslation("lbl.messageNr"))
                isAutoWidth = true
                textAlign = ColumnTextAlign.END
            }
            addColumn("conversationId").apply {
                setHeader(getTranslation("lbl.conversationId"))
                isAutoWidth = true
            }
            addColumn("refToMessageId").apply {
                setHeader(getTranslation("lbl.refToMessageId"))
                isAutoWidth = true
            }
            addColumn(LocalDateTimeRenderer({ m -> toLocalDateTime(m.timestamp) }, DISPLAY_DATE_TIME_FORMATTER)).apply {
                setHeader(getTranslation("lbl.timestamp"))
                isAutoWidth = true
            }
            addColumn("cpaId").apply {
                setHeader(getTranslation("lbl.cpaId"))
                isAutoWidth = true
            }
            addColumn("fromPartyId").apply {
                setHeader(getTranslation("lbl.fromPartyId"))
                isAutoWidth = true
            }
            addColumn("fromRole").apply {
                setHeader(getTranslation("lbl.fromRole"))
                isAutoWidth = true
            }
            addColumn("toPartyId").apply {
                setHeader(getTranslation("lbl.toPartyId"))
                isAutoWidth = true
            }
            addColumn("toRole").apply {
                setHeader(getTranslation("lbl.toRole"))
                isAutoWidth = true
            }
            addColumn("service").apply {
                setHeader(getTranslation("lbl.service"))
                isAutoWidth = true
            }
            addColumn("action").apply {
                setHeader(getTranslation("lbl.action"))
                isAutoWidth = true
            }
            addColumn("status").apply {
                setHeader(getTranslation("lbl.status"))
                isAutoWidth = true
            }
            addColumn(LocalDateTimeRenderer({ m -> m.statusTime?.let { toLocalDateTime(m.statusTime) } }, DISPLAY_DATE_TIME_FORMATTER)).apply {
                setHeader(getTranslation("lbl.statusTime"))
                isAutoWidth = true
            }
            setItemDetailsRenderer(messageDetailsRenderer())
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
