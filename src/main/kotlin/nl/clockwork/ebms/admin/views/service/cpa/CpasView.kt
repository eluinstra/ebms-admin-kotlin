package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.refresh
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.NativeButtonRenderer
import com.vaadin.flow.router.AfterNavigationEvent
import com.vaadin.flow.router.AfterNavigationObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import nl.clockwork.ebms.admin.components.downloadButton
import nl.clockwork.ebms.admin.components.downloadButton1
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import java.io.ByteArrayInputStream

@Route(value = "service/cpa", layout = MainLayout::class)
@PageTitle("CPAs")
class CpasView : KComposite(), AfterNavigationObserver, WithBean {
    private lateinit var grid: Grid<String>
    private val root = ui {
        verticalLayout {
            h1(getTranslation("cpas"))
            grid = grid(cpaDataProvider()) {
                isExpand = true
                setSelectionMode(Grid.SelectionMode.NONE)
                addColumn(CpaView.cpaIdLink()).setHeader(getTranslation("lbl.cpaId"))
//                addColumn(NativeButtonRenderer(getTranslation("cmd.download")) {
//                    cpaClient!!.getCPA(it)
//                })
                addColumn(download())
                addColumn(NativeButtonRenderer(getTranslation("cmd.delete")) {
                    confirmDialog {
                        cpaClient!!.deleteCPA(it)
                        //TODO: fix
                        this@grid.refresh()
                    }
                })
            }
        }
    }

    private fun download() : ComponentRenderer<Anchor, String> =
        ComponentRenderer { cpaId -> downloadButton1(getTranslation("cmd.download"), createResource("$cpaId.xml", cpaClient!!.getCPA(cpaId))) }

    private fun createResource(resourceName: String, cpa: String): StreamResource =
        StreamResource(resourceName, InputStreamFactory {
            ByteArrayInputStream(cpa.toByteArray())
        })

    private fun cpaDataProvider(): DataProvider<String, *> =
        DataProvider.fromStream(cpaClient!!.getCPAIds().stream())

    override fun afterNavigation(event: AfterNavigationEvent?) {
        grid.refresh()
    }

    private fun confirmDialog(text: String = "Are you sure?", title: String? = null, yesListener: ()->Unit) {
        val window = Dialog()
        window.apply {
            setSizeUndefined()
            if (title != null) h2(title)
            text(text)
            horizontalLayout {
                button("Yes") {
                    onLeftClick { yesListener(); window.close() }
                    setPrimary()
                }
                button("No") {
                    onLeftClick { window.close() }
                }
            }
        }
        window.open()
    }
}