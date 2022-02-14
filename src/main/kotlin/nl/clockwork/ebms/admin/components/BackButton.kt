package nl.clockwork.ebms.admin.components

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.button
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button


fun (@VaadinDsl HasComponents).backButton(caption: String) : Button =
    button(caption) {
        addClickListener { _ ->
            UI.getCurrent().element.executeJs("window.history.back()")
        }
    }
