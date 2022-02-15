package nl.clockwork.ebms.admin.views.cpa

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.*
import nl.clockwork.ebms.admin.Cpa
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean


@Route(value = "cpa", layout = MainLayout::class)
@PageTitle("CPA")
class CpaView : KComposite(), HasUrlParameter<String>, WithBean {
    private var root = ui {
        verticalLayout {
            h1(getTranslation("cpa"))
        }
    }

    override fun setParameter(event: BeforeEvent?, cpaId: String?) {
        val cpa = cpaId?.let { ebMSAdminDAO?.findCPA(it) }
        with (root) {
            cpa?.let { cpaForm(cpa) } ?: text("CPA not found")
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
            RouterLink(cpaId, cpaView, cpaId)
    }
}