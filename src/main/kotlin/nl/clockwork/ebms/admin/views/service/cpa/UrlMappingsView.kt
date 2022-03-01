package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.github.mvysny.kaributools.refresh
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.components.confirmDialog
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import nl.ordina.cpa.urlmapping._2.UrlMapping

@Route(value = "service/urlMapping", layout = MainLayout::class)
@PageTitle("UrlMappings")
class UrlMappingsView : KComposite(), WithBean {
    private lateinit var grid : Grid<UrlMapping>

    private val root = ui {
        verticalLayout {
            h2(getTranslation("urlMappings"))
            grid = grid(urlMappingDataProvider()) {
                isExpand = true
                setSelectionMode(Grid.SelectionMode.NONE)
                addColumn(UpdateUrlMappingView.urlMappingLink())
                addColumn("destination")
                addColumn(delete(getTranslation("cmd.delete")))
            }
            button {
                text = getTranslation("cmd.new")
                onLeftClick {
                    navigateTo(CreateUrlMappingView::class)
                }
            }
        }
    }

    private fun delete(text: String) : ComponentRenderer<Button, UrlMapping> =
        ComponentRenderer {
                urlMapping -> Button(text).apply {
            addClickListener {
                confirmDialog {
                    urlMappingClient.deleteURLMapping(urlMapping.source)
                    grid.refresh()
                }
            }
        }
        }

    private fun urlMappingDataProvider() : DataProvider<UrlMapping, *> =
        DataProvider.fromStream(urlMappingClient.urlMappings.stream())


}