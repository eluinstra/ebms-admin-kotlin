package nl.clockwork.ebms.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.VaadinVersion
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.router.Route

@Route("")
class WelcomeView: KComposite() {
    private val root = ui {
        verticalLayout {
            button {
                text = "Click Me"
                onLeftClick {
                    Notification.show("Hello, Vaadin-on-Kotlin!")
                }
            }
        }
    }
}
