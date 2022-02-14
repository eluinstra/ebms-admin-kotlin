package nl.clockwork.ebms.admin.views

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.RouterLayout
import nl.clockwork.ebms.admin.views.cpa.CpasView


@CssImport("./styles/custom-styles.css")
@PageTitle("Main")
class MainLayout : KComposite(), RouterLayout {
    private val root = ui {
        appLayout {
            navbar {
                image("images/ebms_admin.gif") {
                    getTranslation("home")
                    onLeftClick { navigateTo(HomeView::class) }
                } //, getTranslation("home"))
                menuBar {
                    item(getTranslation("home"), { _ -> navigateTo(HomeView::class) })
                    item(getTranslation("cpaService")) {
                        item(getTranslation("cpas"))
                        item(getTranslation("urlMappings"))
                        item(getTranslation("certificateMappings"))
                    }
                    item(getTranslation("messageService")) {
                        item(getTranslation("ping"))
                        item(getTranslation("unprocessedMessages"))
                        item(getTranslation("messageEvents"))
                        item(getTranslation("messageSend"))
                        item(getTranslation("messageResend"))
                        item(getTranslation("messageStatus"))
                    }
                    item(getTranslation("advanced")) {
                        item(getTranslation("traffic"))
                        item(getTranslation("trafficChart"))
                        item(getTranslation("cpas"), { _ -> navigateTo(CpasView::class) })
                        item(getTranslation("messages"))
                    }
                    item(getTranslation("about"), { _ -> navigateTo(AboutView::class) })
                }
            }
        }
    }
}
