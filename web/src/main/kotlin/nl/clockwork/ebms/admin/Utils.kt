package nl.clockwork.ebms.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.dialog.Dialog

fun confirmDialog(text: String = "Are you sure?", title: String? = null, yesListener: ()->Unit) {
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

val jvmVersion: String get() = System.getProperty("java.version")
