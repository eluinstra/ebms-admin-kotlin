package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.*
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import java.net.URLDecoder
import java.net.URLEncoder
import javax.validation.constraints.NotEmpty
import nl.ordina.cpa.urlmapping._2.UrlMapping as UrlMappingExt

@Route(value = "service/newUrlMapping", layout = MainLayout::class)
@PageTitle("UrlMapping")
class CreateUrlMappingView : KComposite(), BeforeEnterObserver, WithBean {
    private val binder = beanValidationBinder<UrlMapping>()

    private val root = ui {
        verticalLayout {
            h2(getTranslation("urlMapping"))
        }
    }

    override fun beforeEnter(event: BeforeEnterEvent?) {
        with (root) {
            urlMappingForm(UrlMapping())
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                saveButton(getTranslation("cmd.set"))
            }
        }
    }

    private fun HasComponents.urlMappingForm(urlMapping: UrlMapping) {
        formLayout {
            setSizeFull()
            label(getTranslation("lbl.urlMapping")) {
                colspan = 2
            }
            textField(getTranslation("lbl.source")) {
                colspan = 2
                bind(binder).bind(UrlMapping::source)
            }
            textField(getTranslation("lbl.destination")) {
                colspan = 2
                bind(binder).bind(UrlMapping::destination)
            }
        }
    }

    private fun HasComponents.saveButton(text: String?) =
        button(text, Icon("lumo", "checkmark")) {
            onLeftClick {
                val urlMapping = UrlMapping()
                if (binder.writeBeanIfValid(urlMapping)) {
                    urlMappingClient.setURLMapping(urlMapping.toUrlMapping())
                    navigateTo(UrlMappingsView::class)
                } else {
                    //TODO show error
                }
            }
            setPrimary()
        }

    companion object {
        fun newUrlLink(): ComponentRenderer<RouterLink, String> =
            ComponentRenderer { text -> urlMappingRouterLink(text) }

        private fun urlMappingRouterLink(text: String): RouterLink =
            RouterLink(text, CreateUrlMappingView::class.java)
    }
}

@Route(value = "service/urlMapping/:sourceUrl", layout = MainLayout::class)
@PageTitle("UrlMapping")
class UpdateUrlMappingView : KComposite(), BeforeEnterObserver, WithBean {
    private val binder = beanValidationBinder<UrlMapping>()

    private val root = ui {
        verticalLayout {
            h2(getTranslation("urlMapping"))
        }
    }

    override fun beforeEnter(event: BeforeEnterEvent?) {
        val sourceUrl = event?.routeParameters?.get("sourceUrl")?.map { u -> URLDecoder.decode(u) }?.orElse(null)
        val urlMapping = sourceUrl?.let { urlMappingClient.urlMappings.firstOrNull { it.source == sourceUrl } }
        with (root) {
            urlMapping?.let { urlMappingForm(it.toUrlMapping()) } ?: text(getTranslation("urlMappingNotFound"))
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                saveButton(getTranslation("cmd.set"))
            }
        }
    }

    private fun UrlMappingExt.toUrlMapping() =
        UrlMapping(source, destination)

    private fun HasComponents.urlMappingForm(urlMapping: UrlMapping) {
        binder.readBean(urlMapping)
        formLayout {
            setSizeFull()
            label(getTranslation("lbl.urlMapping")) {
                colspan = 2
            }
            textField(getTranslation("lbl.source")) {
                colspan = 2
                bind(binder).bind(UrlMapping::source)
                value = urlMapping.source
            }
            textField(getTranslation("lbl.destination")) {
                colspan = 2
                bind(binder).bind(UrlMapping::destination)
                value = urlMapping.destination
            }
        }
    }

    private fun HasComponents.saveButton(text: String?) =
        button(text, Icon("lumo", "checkmark")) {
            onLeftClick {
                val urlMapping = UrlMapping()
                if (binder.writeBeanIfValid(urlMapping)) {
                    urlMappingClient.setURLMapping(urlMapping.toUrlMapping())
                    navigateTo(UrlMappingsView::class)
                } else {
                    //TODO show error
                }
            }
            setPrimary()
        }

    companion object {
        fun navigateTo(sourceUrl: String) {
            urlMappingRouterLink(sourceUrl).navigateTo()
        }

        private fun urlMappingRouterLink(sourceUrl: String): RouterLink =
            RouterLink(sourceUrl, UpdateUrlMappingView::class.java, RouteParameters("sourceUrl", URLEncoder.encode(sourceUrl)))
    }
}

data class UrlMapping(
    @field:NotEmpty
    var source: String? = null,
    @field:NotEmpty
    var destination: String? = null
) {
    fun toUrlMapping() : UrlMappingExt =
        UrlMappingExt().apply {
            source = this@UrlMapping.source
            destination = this@UrlMapping.destination
        }
}
