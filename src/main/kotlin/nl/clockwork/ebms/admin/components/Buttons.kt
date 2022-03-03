package nl.clockwork.ebms.admin.components

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.data.binder.BeanValidationBinder


fun (@VaadinDsl HasComponents).backButton(caption: String) : Button =
    button(caption) {
        addClickListener { _ ->
            UI.getCurrent().element.executeJs("window.history.back()")
        }
    }

fun @VaadinDsl HasComponents.resetButton(text: String?, beanValidationBinder: BeanValidationBinder<*>) =
    button(text) {
        onLeftClick {
            beanValidationBinder.fields.forEach { it.clear() }
        }
    }

