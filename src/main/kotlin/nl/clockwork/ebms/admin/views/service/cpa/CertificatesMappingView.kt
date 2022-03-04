package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.h2
import com.github.mvysny.karibudsl.v10.span
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.views.MainLayout

@Route(value = "service/certificateMapping", layout = MainLayout::class)
@PageTitle("CertificateMappings")
class CertificatesMappingView : KComposite() {
    private val root = ui {
        verticalLayout {
            h2(getTranslation("certificateMappings"))
            span("Not implemented yet")
        }
    }
}