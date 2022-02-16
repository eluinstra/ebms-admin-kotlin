package nl.clockwork.ebms.admin.views.cpa

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.h1
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.Cpa
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean


@Route(value = "cpa", layout = MainLayout::class)
@PageTitle("CPAs")
class CpasView : KComposite(), WithBean {
    private val root = ui {
        verticalLayout {
            h1(getTranslation("cpas"))
            grid(cpaDataProvider()) {
                setSelectionMode(SelectionMode.NONE)
                addColumn(CpaView.cpaIdLink()).setHeader(getTranslation("lbl.cpaId"))
            }
        }
    }

    private fun cpaDataProvider(): DataProvider<Cpa, *> =
        DataProvider.fromCallbacks(
            { query: Query<Cpa, Void> ->
                ebMSAdminDAO!!.selectCPAs(
                    query.offset.toLong(),
                    query.limit
                ).stream()
            },
            { ebMSAdminDAO!!.countCPAs().toInt() }
        )
}