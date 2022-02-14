package nl.clockwork.ebms.admin.views.cpa

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.h1
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteConfiguration
import com.vaadin.flow.router.RouterLink
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
                addColumn(cpaLink()).setHeader(getTranslation("lbl.cpaId"))
//                addItemClickListener { cpa -> navigateTo(CpaView::class, cpa.item.cpaId) }
//                addColumn("cpaId").setHeader(getTranslation("lbl.cpaId"))
//                addColumn(NativeButtonRenderer("Show") {
//                    CpaView.navigateTo(it.cpaId)
//                })
            }
        }
    }

    private fun cpaDataProvider(): DataProvider<Cpa, *> =
        DataProvider.fromCallbacks(
            { query: Query<Cpa, Void> ->
                ebMSAdminDAO?.selectCPAs(
                    query.offset.toLong(),
                    query.limit
                )?.stream()
            },
            { ebMSAdminDAO?.countCPAs()?.toInt() ?: 0 }
        )

    private fun cpaLink(): ComponentRenderer<RouterLink, Cpa> =
        ComponentRenderer { cpa -> cpaRouterLink(cpa.cpaId, CpaView::class.java) }

    private fun cpaRouterLink(cpaId: String, cpaView: Class<CpaView>): RouterLink =
        RouterLink(cpaId, cpaView, cpaId)

    private fun cpa(): ComponentRenderer<Text, Cpa> =
        ComponentRenderer { cpa -> Text(cpa.cpaId) }

    private fun cpaAnchor(): ComponentRenderer<Anchor, Cpa> =
        ComponentRenderer { cpa ->
            val route = RouteConfiguration.forSessionScope().getUrl(CpaView::class.java, cpa.cpaId)
            Anchor(route, cpa.cpaId)
        }

}