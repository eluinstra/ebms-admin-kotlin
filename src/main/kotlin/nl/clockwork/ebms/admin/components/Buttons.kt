package nl.clockwork.ebms.admin.components

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.anchor
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.server.StreamResource


fun @VaadinDsl HasComponents.backButton(text: String) : Button =
    button(text, Icon("lumo", "arrow-left")) {
        addClickListener { _ ->
            UI.getCurrent().element.executeJs("window.history.back()")
        }
    }

fun @VaadinDsl HasComponents.closeButton(text: String, block: (@VaadinDsl Button).() -> Unit = {}) : Button =
    button(text, Icon("lumo", "cross")) {
        block()
    }

fun @VaadinDsl HasComponents.resetButton(text: String?, block: () -> Unit = {}) =
    button(text, Icon("lumo", "reload")) {
        onLeftClick {
            block()
        }
    }

fun HasComponents.downloadButton(text: String, resource: StreamResource) : Anchor =
    anchor(resource, "") {
        element.setAttribute("download", true)
        button(text, Icon("lumo", "download"))
    }

fun createDownloadButton(text: String, resource: StreamResource, block: (@VaadinDsl Button).() -> Unit = {}) : Anchor =
    Anchor("").apply {
        setHref(resource)
        element.setAttribute("download", true)
        add(Button(text, Icon("lumo", "download")).apply {
            block()
        })
    }
