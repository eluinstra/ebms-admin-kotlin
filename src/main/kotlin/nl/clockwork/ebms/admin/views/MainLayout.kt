package nl.clockwork.ebms.admin.views

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.RouterLayout
import nl.clockwork.ebms.admin.views.cpa.CpasView
import nl.clockwork.ebms.admin.views.message.MessagesView
import nl.clockwork.ebms.admin.views.message.TrafficChartView
import nl.clockwork.ebms.admin.views.message.TrafficView
import nl.clockwork.ebms.admin.views.service.cpa.UrlMappingsView
import nl.clockwork.ebms.admin.views.service.message.PingView
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
                        item(getTranslation("cpas"), { navigateTo(CpaServiceView::class) })//.add(hr())
                        item(getTranslation("urlMappings"), { navigateTo(UrlMappingsView::class) })//.add(hr())
                        item(getTranslation("certificateMappings"))
                    }
                    item(getTranslation("messageService")) {
                        item(getTranslation("ping"), { navigateTo(PingView::class) })//.separator()
                        item(getTranslation("unprocessedMessages"))//.separator()
                        item(getTranslation("messageEvents"))
                        item(getTranslation("messageSend"))//.separator()
                        item(getTranslation("messageResend"))
                        item(getTranslation("messageStatus"))
                    }
                    item(getTranslation("advanced")) {
                        item(getTranslation("traffic"), { navigateTo(TrafficView::class) })
                        item(getTranslation("trafficChart"), { navigateTo(TrafficChartView::class) })//.separator()
                        item(getTranslation("cpas"), { navigateTo(CpasView::class) })
                        item(getTranslation("messages"), { navigateTo(MessagesView::class) })
                    }
                    item(getTranslation("about"), { navigateTo(AboutView::class) })
                }
            }
        }
    }
}
