package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.refresh
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.components.backButton
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
            grid = urlMappingGrid()
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                button {
                    text = getTranslation("cmd.new")
                    icon = Icon("lumo", "edit")
                    onLeftClick {
                        urlMappingDialog(urlMappingClient){
                            grid.refresh()
                        }.open()
                    }
                }
            }
        }
    }

    private fun @VaadinDsl VerticalLayout.urlMappingGrid() : Grid<UrlMapping> {
        val binder = beanValidationBinder<UrlMapping>()
        val result = grid(urlMappingDataProvider()) {
            gridContextMenu() {
                item(getTranslation("cmd.edit")) {
                    addMenuItemClickListener {
                        urlMappingDialog(urlMappingClient, it.item.get()) {
                            this@grid.refresh()
                        }.open()
                    }
                }
                hr()
                item(getTranslation("cmd.delete")) {
                    addMenuItemClickListener {
                        confirmDialog {
                            urlMappingClient.deleteURLMapping(it.item.get().source)
                            this@grid.refresh()
                        }
                    }
                }
            }
            isExpand = true
            setSelectionMode(Grid.SelectionMode.NONE)
            addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES)
            addColumn("source").setHeader(getTranslation("lbl.source"))
                .editorComponent = TextField().apply {
                setWidthFull()
//                  addCloseHandler(this, editor)
                bind(binder).bind(UrlMapping::getSource, UrlMapping::setSource)
            }
            addColumn("destination").setHeader(getTranslation("lbl.destination"))
                .editorComponent = TextField().apply {
                setWidthFull()
//                  addCloseHandler(this, editor)
                bind(binder).bind(UrlMapping::getDestination, UrlMapping::setDestination)
            }
        }
        val editor = result.editor
        editor.binder = binder
        result.addItemDoubleClickListener {
            editor.editItem(it.item)
            val editorComponent = it.column.editorComponent
            if (editorComponent is Focusable<*>) {
                (editorComponent as Focusable<*>).focus()
            }
        }
        return result
    }

    private fun urlMappingDataProvider() : DataProvider<UrlMapping, *> =
        DataProvider.fromCallbacks(
            { query -> urlMappingClient.urlMappings.drop(query.offset).take(query.limit).stream() },
            { urlMappingClient.urlMappings.size }
        )
}