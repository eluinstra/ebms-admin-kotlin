package nl.clockwork.ebms.admin.views.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
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
            h2(getTranslation("cpa"))
        }
    }

    override fun beforeEnter(event: BeforeEnterEvent?) {
        val cpaId = event?.routeParameters?.get("cpaId")?.orElse(null)
        val cpa = cpaId?.let { ebMSAdminDAO.findCPA(it) }
        with (root) {
            cpa?.let { cpaForm(it) } ?: text("CPA not found")
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun VerticalLayout.cpaForm(cpa: Cpa) =
        formLayout {
            setSizeFull()
            label(getTranslation("lbl.cpaId")) {
                colspan = 2
            }
            textField {
                colspan = 2
                isReadOnly = true
                value = cpa.cpaId
            }
            label(getTranslation("lbl.cpa")) {
                colspan = 2
            }
            textArea {
                colspan = 2
//                element.setAttribute("rows","40");
                height = "600px"
                isReadOnly = true
                value = cpa.cpa
            }
        }

    companion object {
        fun navigateTo(cpaId: String) {
            cpaRouterLink(cpaId).navigateTo()
        }

        private fun cpaRouterLink(cpaId: String): RouterLink =
            RouterLink(cpaId, CpaView::class.java, RouteParameters("cpaId", cpaId))
    }
}