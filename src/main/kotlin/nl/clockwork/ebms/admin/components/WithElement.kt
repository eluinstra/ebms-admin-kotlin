package nl.clockwork.ebms.admin.components

import com.vaadin.flow.component.Component


interface WithElement {
    fun setColSpan(component: Component, cols: Int) {
        if (cols > 1) component.element.setAttribute("colspan", cols.toString())
    }
}
