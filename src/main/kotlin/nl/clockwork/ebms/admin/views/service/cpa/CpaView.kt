package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.*
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean

@Route(value = "service/cpa/:cpaId", layout = MainLayout::class)
@PageTitle("CPAs")
class CpaView : KComposite(), BeforeEnterObserver, WithBean {
    private val root = ui {
        verticalLayout {
            h1(getTranslation("cpa"))
        }
    }
    override fun beforeEnter(event: BeforeEnterEvent?) {
        val cpaId = event?.routeParameters?.get("cpaId")?.orElse(null)
        val cpa = cpaId?.let { cpaClient.getCPA(it) }
        with (root) {
            cpa?.let { cpaForm(it) } ?: text("CPA not found")
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun VerticalLayout.cpaForm(cpa: String) =
        formLayout {
            setSizeFull()
            label(getTranslation("lbl.cpa")) {
                colspan = 2
            }
            textArea {
                colspan = 2
//                element.setAttribute("rows","40");
                height = "600px"
                isReadOnly = true
                value = cpa
            }
        }

    companion object {
        fun cpaIdLink(): ComponentRenderer<RouterLink, String> =
            ComponentRenderer { cpaId -> cpaRouterLink(CpaView::class.java, cpaId) }

        private fun cpaRouterLink(cpaView: Class<CpaView>, cpaId: String): RouterLink =
            RouterLink(cpaId, cpaView, RouteParameters("cpaId", cpaId))
    }
}