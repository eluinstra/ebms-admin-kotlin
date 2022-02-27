package nl.clockwork.ebms.admin.components

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dialog.Dialog

fun Component.confirmDialog(text: String = getTranslation("confirm"), title: String? = null, yesListener: ()->Unit) {
    val window = Dialog()
    window.apply {
        setSizeUndefined()
        if (title != null) h2(title)
        text(text)
        horizontalLayout {
            button(getTranslation("yes")) {
                onLeftClick {
                    yesListener()
                    window.close()
                }
                setPrimary()
            }
            button(getTranslation("no")) {
                onLeftClick { window.close() }
            }
        }
    }
    window.open()
}
