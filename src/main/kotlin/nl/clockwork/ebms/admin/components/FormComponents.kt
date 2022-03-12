package nl.clockwork.ebms.admin.components

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.comboBox
import com.github.mvysny.karibudsl.v10.label
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.textfield.TextField

fun FormLayout.aLabel(label: String, colspan: Int = 2, block: (@VaadinDsl Label).() -> Unit = {}): Label =
    label(label) {
        element.style.set("font-size", "13px")
        element.style.set("font-weight", "400")
        element.style.set("color", "#666")
        block()
    }

fun FormLayout.aTextField(label: String, colspan: Int = 2, block: (@VaadinDsl TextField).() -> Unit = {}): TextField =
    textField(label) {
        setColspan(this, colspan)
        isClearButtonVisible = true
        block()
    }

fun FormLayout.aComboBox(label: String, items: List<String>, colspan: Int = 2, block: (@VaadinDsl ComboBox<String>).() -> Unit = {}): ComboBox<String> =
    comboBox(label) {
        setColspan(this, colspan)
        setItems(items)
        isClearButtonVisible = true
        isEnabled = items != emptyList<Any>()
        block()
    }

private fun List<String>.defaultValue() : String? = if (size == 1) get(0) else null

fun ComboBox<String>.enable(items: List<String>) {
    isEnabled = true
    setItems(items)
    value = items.defaultValue()
}

fun ComboBox<*>.disable() {
    value = null
    setItems(emptyList())
    isEnabled = false
}
