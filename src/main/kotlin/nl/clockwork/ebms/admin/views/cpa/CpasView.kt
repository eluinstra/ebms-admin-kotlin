package nl.clockwork.ebms.admin.views.cpa

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.h2
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.Cpa
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean


@Route(value = "cpa", layout = MainLayout::class)
@PageTitle("CPAs")
class CpasView : KComposite(), WithBean {
    private val root = ui {
        verticalLayout {
            h2(getTranslation("cpas"))
            grid(cpaDataProvider()) {
                setSelectionMode(SelectionMode.NONE)
                addItemClickListener {
                    CpaView.navigateTo(it.item.cpaId)
                }
                addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES)
                addColumn("cpaId").setHeader(getTranslation("lbl.cpaId"))
            }
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun cpaDataProvider(): DataProvider<Cpa, *> =
        DataProvider.fromCallbacks(
            { query: Query<Cpa, Void> ->
                ebMSAdminDAO.selectCPAs(
                    query.offset.toLong(),
                    query.limit
                ).stream()
            },
            { ebMSAdminDAO.countCPAs().toInt() }
        )
}