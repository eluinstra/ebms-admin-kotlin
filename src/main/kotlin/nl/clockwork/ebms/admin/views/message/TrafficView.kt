package nl.clockwork.ebms.admin.views.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid.SelectionMode.NONE
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer
import com.vaadin.flow.function.SerializableFunction
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.EbMSMessage
import nl.clockwork.ebms.admin.EbMSMessageFilter
import nl.clockwork.ebms.admin.components.WithDate
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.Utils
import nl.clockwork.ebms.admin.views.WithBean
import nl.clockwork.ebms.admin.views.message.SearchFilter.searchFilter


@Route(value = "traffic", layout = MainLayout::class)
@PageTitle("Traffic")
@CssImport(themeFor = "vaadin-grid", value = "./styles/dynamic-message-status-color.css")
class TrafficView : KComposite(), WithBean, WithDate {
    private val root = ui {
        verticalLayout {
            setSizeFull()
            h2(getTranslation("traffic"))
            val messageFilter = EbMSMessageFilter(
                messageNr = 0,
                serviceMessage = false
            )
            val dataProvider = messageDataProvider(messageFilter)
            searchFilterDetails(getTranslation("messageFilter"),messageFilter,dataProvider)
            messageGrid(dataProvider)
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun messageDataProvider(messageFilter: EbMSMessageFilter): DataProvider<EbMSMessage, *> {
        return DataProvider.fromCallbacks(
            { query: Query<EbMSMessage, Void> ->
                ebMSAdminDAO.selectMessages(
                    messageFilter,
                    query.offset.toLong(),
                    query.limit
                ).stream()
            },
            { ebMSAdminDAO.countMessages(messageFilter).toInt()}
        )
    }

    private fun HasComponents.searchFilterDetails(
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

    private fun HasComponents.messageGrid(dataProvider: DataProvider<EbMSMessage, *>): Component =
        grid(dataProvider) {
            setSelectionMode(NONE)
            addItemClickListener {
                MessageView.navigateTo(it.item.messageId)
            }
            addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER)
            addColumn("messageId").apply {
                setHeader(getTranslation("lbl.messageId"))
                isAutoWidth = true
                isFrozen = true
            }
            addColumn("conversationId").apply {
                setHeader(getTranslation("lbl.conversationId"))
                isAutoWidth = true
            }
            addColumn(LocalDateTimeRenderer({ m -> toLocalDateTime(m.timestamp) }, WithDate.DISPLAY_DATE_TIME_FORMATTER)).apply {
                setHeader(getTranslation("lbl.timestamp"))
                isAutoWidth = true
            }
            addColumn("cpaId").apply {
                setHeader(getTranslation("lbl.cpaId"))
                isAutoWidth = true
            }
            addColumn("fromRole").apply {
                setHeader(getTranslation("lbl.fromRole"))
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
                classNameGenerator = SerializableFunction { Utils.getTableCellCssClass(it.status) }
            }
            addColumn(LocalDateTimeRenderer(
                { m -> m.statusTime?.let { toLocalDateTime(m.statusTime) } },
                WithDate.DISPLAY_DATE_TIME_FORMATTER)
            ).apply {
                setHeader(getTranslation("lbl.statusTime"))
                isAutoWidth = true
            }
            setClassNameGenerator { Utils.getTableRowCssClass(it.status) }
        }

}