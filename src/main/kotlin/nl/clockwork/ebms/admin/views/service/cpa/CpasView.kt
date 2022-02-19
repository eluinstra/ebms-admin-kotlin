package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.h1
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.github.mvysny.kaributools.refresh
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.NativeButtonRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean

@Route(value = "service/cpa", layout = MainLayout::class)
@PageTitle("CPAs")
class CpasView : KComposite(), WithBean {
    private val root = ui {
        verticalLayout {
            h1(getTranslation("cpas"))
            grid(cpaDataProvider()) {
                setSelectionMode(Grid.SelectionMode.NONE)
                addColumn(cpaId()).setHeader(getTranslation("lbl.cpaId"))
                addColumn(NativeButtonRenderer(getTranslation("cmd.download")) {
                    cpaClient!!.getCPA(it)
                })
                addColumn(NativeButtonRenderer(getTranslation("cmd.delete")) {
                    cpaClient!!.deleteCPA(it)
                    //TODO: fix
                    refresh()
                })
            }
        }
    }

    private fun cpaDataProvider(): DataProvider<String, *> =
        DataProvider.fromStream(cpaClient!!.getCPAIds().stream())

    fun cpaId(): ComponentRenderer<Text, String> =
        ComponentRenderer { s -> Text(s) }
}