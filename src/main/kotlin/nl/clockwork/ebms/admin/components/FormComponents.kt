package nl.clockwork.ebms.admin.components

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.comboBox
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.TextField

fun FormLayout.aTextField(label: String, colspan: Int, block: (@VaadinDsl TextField).() -> Unit = {}): TextField =
    textField(label) {
        setColspan(this, colspan)
        isClearButtonVisible = true
        block()
    }

fun FormLayout.aComboBox(label: String, items: List<String>, colspan: Int, block: (@VaadinDsl ComboBox<String>).() -> Unit = {}): ComboBox<String> =
    comboBox(label) {
        setColspan(this, colspan)
        setItems(items)
        isClearButtonVisible = true
        isEnabled = items != emptyList<Any>()
        block()
    }

