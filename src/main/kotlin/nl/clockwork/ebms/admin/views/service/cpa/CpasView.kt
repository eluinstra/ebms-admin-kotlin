package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.refresh
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.AfterNavigationEvent
import com.vaadin.flow.router.AfterNavigationObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.components.confirmDialog
import nl.clockwork.ebms.admin.components.createDownloadButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import java.io.ByteArrayInputStream

@Route(value = "service/cpa", layout = MainLayout::class)
@PageTitle("CPAs")
class CpasView : KComposite(), WithBean {
    private lateinit var grid: Grid<String>

    private val root = ui {
        verticalLayout {
            h2(getTranslation("cpas"))
            grid = grid(cpaDataProvider()) {
                gridContextMenu() {
                    item(getTranslation("cmd.details")) {
                        addMenuItemClickListener {
                            e -> cpaDialog(cpaClient.getCPA(e.item.get())).open()
                        }
                    }
                    hr()
                    item(getTranslation("cmd.delete")) {
                        addMenuItemClickListener {
                            confirmDialog {
                                cpaClient.deleteCPA(it.item.get())
                                this@grid.refresh()
                            }
                        }
                    }
                }
                isExpand = true
                setSelectionMode(Grid.SelectionMode.NONE)
                addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES)
                addColumn(cpaId()).setHeader(getTranslation("lbl.cpaId"))
                addColumn(download(getTranslation("cmd.download"))).apply {
                    isAutoWidth = true
                    flexGrow = 0
                }
            }
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                button {
                    text = getTranslation("cmd.new")
                    icon = Icon("lumo", "edit")
                    onLeftClick {
                        newCpaDialog(cpaClient){
                            grid.refresh()
                        }.open()
                    }
                }
            }
        }
    }

    private fun cpaId(): ComponentRenderer<Text, String> =
        ComponentRenderer {
            cpaId -> Text(cpaId)
        }

    private fun download(text: String): ComponentRenderer<Anchor, String> =
        ComponentRenderer {
            cpaId -> createDownloadButton(text, resource("${cpaId}.xml", cpaClient.getCPA(cpaId) ?: "")) {
                addThemeVariants(ButtonVariant.LUMO_SMALL)
            }
        }

    private fun resource(resourceName: String, cpa: String): StreamResource =
        StreamResource(resourceName, InputStreamFactory {
            ByteArrayInputStream(cpa.toByteArray())
        })

    private fun cpaDataProvider() : DataProvider<String, *> =
        DataProvider.fromCallbacks(
            { query -> cpaClient.cpaIds.drop(query.offset).take(query.limit).stream() },
            { cpaClient.cpaIds.size }
        )
}