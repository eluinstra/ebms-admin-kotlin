package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import kotlin.reflect.KClass

@Route(value = "service/message/messages", layout = MainLayout::class)
@PageTitle("Unprocessed Messages")
class UnprocessedMessagesView : KComposite(), WithBean {
    private lateinit var grid: Grid<String>

    private val root = ui {
        verticalLayout {
            h1(getTranslation("unprocessedMessages"))
            grid = grid(messageIdDataProvider()) {
                isExpand = true
                setSelectionMode(Grid.SelectionMode.NONE)
            }
        }
    }

    private fun messageIdDataProvider(): DataProvider<String, *> =
        DataProvider.fromStream(ebMSMessageClient.getUnprocessedMessageIds(null, 0).stream())
}