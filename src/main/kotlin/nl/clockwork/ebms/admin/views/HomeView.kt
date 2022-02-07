package nl.clockwork.ebms.admin.views

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias


@Route(value = "home", layout = MainLayout::class)
@RouteAlias(value = "", layout = MainLayout::class)
@PageTitle("Home")
class HomeView : KComposite() {
    private val root = ui {
        verticalLayout {
            h1(getTranslation("home"))
            div {
                text(getTranslation("home.message"))
            }
        }
    }
}