package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.refresh
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.components.downloadButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.cxf.io.CachedOutputStream
import java.io.OutputStreamWriter

@Route(value = "service/message/messages", layout = MainLayout::class)
@PageTitle("Unprocessed Messages")
class UnprocessedMessagesView : KComposite(), WithBean {
    private lateinit var grid: Grid<String>

    private val root = ui {
        verticalLayout {
            h2(getTranslation("unprocessedMessages"))
            grid = grid(messageIdDataProvider()) {
                gridContextMenu() {
                    item(getTranslation("cmd.details")) {
                        addMenuItemClickListener { e ->
                            val message = ebMSMessageClient.getMessage(e.item.get(), false)
                            messageDialog(message) {
                                ebMSMessageClient.processMessage(message.properties.messageId)
                                grid.refresh()
                            }.open()
                        }
                    }
                }
                isExpand = true
                setSelectionMode(Grid.SelectionMode.NONE)
                addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES)
                addColumn(messageId()).setHeader(getTranslation("lbl.messageId"))
                setItemDetailsRenderer(messageDetailsRenderer(ebMSMessageClient))
            }
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                downloadButton(getTranslation("cmd.download"), StreamResource("messages.csv", createCsv()))
            }
        }
    }

    private fun messageId(): ComponentRenderer<Text, String> =
        ComponentRenderer { messageId ->
            Text(messageId)
        }

    private fun createCsv(): InputStreamFactory =
        InputStreamFactory {
            CachedOutputStream().use { output ->
                CSVPrinter(OutputStreamWriter(output), CSVFormat.DEFAULT).use { printer ->
                    ebMSMessageClient.getUnprocessedMessageIds(null, 0)
                        .stream()
                        .forEach {
                            printer.print(it)
                            printer.println()
                        }
                    printer.flush()
                    output.inputStream
                }
            }
        }

    private fun messageIdDataProvider(): DataProvider<String, *> =
        DataProvider.fromCallbacks(
            { query ->
                ebMSMessageClient.getUnprocessedMessageIds(null, 0).drop(query.offset).take(query.limit).stream()
            },
            { ebMSMessageClient.getUnprocessedMessageIds(null, 0).size }
        )
}
