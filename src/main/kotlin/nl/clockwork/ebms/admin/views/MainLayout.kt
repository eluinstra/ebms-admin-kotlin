package nl.clockwork.ebms.admin.views

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.RouterLayout
import nl.clockwork.ebms.admin.views.cpa.CpasView
import nl.clockwork.ebms.admin.views.message.MessagesView
import nl.clockwork.ebms.admin.views.message.TrafficChartView
import nl.clockwork.ebms.admin.views.message.TrafficView
import nl.clockwork.ebms.admin.views.service.cpa.UrlMappingsView
import nl.clockwork.ebms.admin.views.service.message.PingView
import nl.clockwork.ebms.admin.views.service.message.SendMessageView
import nl.clockwork.ebms.admin.views.service.message.UnprocessedMessagesView
import nl.clockwork.ebms.admin.views.service.cpa.CpasView as CpaServiceView

//@Viewport(Viewport.DEVICE_DIMENSIONS)
@CssImport("./styles/custom-styles.css")
class MainLayout : KComposite(), RouterLayout {
    private val root = ui {
        appLayout {
            navbar {
                image("images/ebms_admin.gif") {
                    getTranslation("home")
                    onLeftClick { navigateTo(HomeView::class) }
                } //, getTranslation("home"))
                menuBar {
                    item(getTranslation("home"), { navigateTo(HomeView::class) })
                    item(getTranslation("cpaService")) {
                        item(getTranslation("cpas"), { navigateTo(CpaServiceView::class) })
                        separator()
                        item(getTranslation("urlMappings"), { navigateTo(UrlMappingsView::class) })
                        separator()
                        item(getTranslation("certificateMappings"))
                    }
                    item(getTranslation("messageService")) {
                        item(getTranslation("ping"), { navigateTo(PingView::class) })
                        separator()
                        item(getTranslation("unprocessedMessages"), { navigateTo(UnprocessedMessagesView::class) })
                        separator()
//                        item(getTranslation("messageEvents"))
                        item(getTranslation("messageSend"), { navigateTo(SendMessageView::class) })
                        item(getTranslation("messageResend"))
                        separator()
                        item(getTranslation("messageStatus"))
                    }
                    item(getTranslation("advanced")) {
                        item(getTranslation("traffic"), { navigateTo(TrafficView::class) })
                        item(getTranslation("trafficChart"), { navigateTo(TrafficChartView::class) })
                        separator()
                        item(getTranslation("cpas"), { navigateTo(CpasView::class) })
                        item(getTranslation("messages"), { navigateTo(MessagesView::class) })
                    }
                    item(getTranslation("about"), { navigateTo(AboutView::class) })
                }
            }
        }
    }

    private fun @VaadinDsl MenuItem.separator() {
        item(hr()).isEnabled = false
    }
}
