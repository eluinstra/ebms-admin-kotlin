package nl.clockwork.ebms.admin.views.cpa

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.*
import nl.clockwork.ebms.admin.Cpa
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean


@Route(value = "cpa/:cpaId", layout = MainLayout::class)
@PageTitle("CPA")
class CpaView : KComposite(), BeforeEnterObserver, WithBean {
    private val root = ui {
        verticalLayout {
            h1(getTranslation("cpa"))
        }
    }

    override fun beforeEnter(event: BeforeEnterEvent?) {
        val cpaId = event?.routeParameters?.get("cpaId")?.orElse(null)
        val cpa = cpaId?.let { ebMSAdminDAO!!.findCPA(it) }
        with (root) {
            cpa?.let { cpaForm(it) } ?: text("CPA not found")
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun VerticalLayout.cpaForm(cpa: Cpa) =
        formLayout {
            setSizeFull()
            textArea {
                colspan = 2
//                element.setAttribute("rows","40");
                height = "600px"
                isReadOnly = true
                label = getTranslation("lbl.cpa")
                value = cpa.cpa
            }
        }

    companion object {
        fun cpaIdLink(): ComponentRenderer<RouterLink, Cpa> =
            ComponentRenderer { cpa -> cpaRouterLink(CpaView::class.java, cpa.cpaId) }

        private fun cpaRouterLink(cpaView: Class<CpaView>, cpaId: String): RouterLink =
            RouterLink(cpaId, cpaView, RouteParameters("cpaId", cpaId))
    }
}