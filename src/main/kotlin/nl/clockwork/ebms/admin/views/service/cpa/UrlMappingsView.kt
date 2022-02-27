package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.github.mvysny.kaributools.refresh
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.renderer.NativeButtonRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink
import nl.clockwork.ebms.admin.components.confirmDialog
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import nl.ordina.cpa.urlmapping._2.UrlMapping

@Route(value = "service/urlMapping", layout = MainLayout::class)
@PageTitle("UrlMappings")
class UrlMappingsView : KComposite(), WithBean {
    private val root = ui {
        verticalLayout {
            h1(getTranslation("urlMappings"))
            grid(urlMappingDataProvider()) {
                isExpand = true
                setSelectionMode(Grid.SelectionMode.NONE)
                addColumn(UpdateUrlMappingView.urlMappingLink())
                addColumn("destination")
//                addColumn(NativeButtonRenderer(getTranslation("cmd.editUrl")) {
//                    navigateTo(UpdateUrlMappingView::class)
//                })
                addColumn(NativeButtonRenderer(getTranslation("cmd.delete")) {
                    confirmDialog {
                        urlMappingClient.deleteURLMapping(it.source)
                        this@grid.refresh()
                    }
                })
            }
            button {
                text = getTranslation("cmd.new")
                onLeftClick {
                    navigateTo(CreateUrlMappingView::class)
                }
            }
        }
    }

    private fun urlMappingDataProvider() : DataProvider<UrlMapping, *> =
        DataProvider.fromStream(urlMappingClient.urlMappings.stream())


}