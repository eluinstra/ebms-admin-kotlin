package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.Icon
import nl.clockwork.ebms.admin.components.closeButton
import nl.clockwork.ebms.admin.components.showErrorNotification
import nl.clockwork.ebms.admin.components.showSuccessNotification
import nl.ordina.cpa.urlmapping._2_18.URLMappingServiceException
import nl.ordina.cpa.urlmapping._2_18.UrlMappingService
import javax.validation.constraints.NotEmpty
import nl.ordina.cpa.urlmapping._2.UrlMapping as UrlMappingExt

fun urlMappingDialog(urlMappingClient: UrlMappingService, urlMapping: UrlMappingExt, onUpdate: () -> Unit = {}) : Dialog =
    urlMappingDialog(urlMappingClient, UrlMapping.urlMapping(urlMapping), onUpdate)

fun urlMappingDialog(urlMappingClient: UrlMappingService, urlMapping: UrlMapping = UrlMapping(), onSave: () -> Unit = {}) : Dialog {
    val binder = beanValidationBinder<UrlMapping>()
    binder.readBean(urlMapping)
    fun HasComponents.saveButton(text: String?, onSuccess: () -> Unit = {}) =
        button(text, Icon("lumo", "checkmark")) {
            onLeftClick {
                if (binder.writeBeanIfValid(urlMapping)) {
                    try {
                        urlMappingClient.setURLMapping(urlMapping.toUrlMapping())
                        onSuccess()
                        showSuccessNotification("UrlMapping set")
                    } catch (e: URLMappingServiceException) {
//                        logger.error("", e)
                        showErrorNotification(e.message)
                    }
                }
            }
            setPrimary()
        }

    return Dialog().apply {
        width = "80%"
        formLayout {
            setResponsiveSteps(FormLayout.ResponsiveStep("0", 2))
            label(getTranslation("lbl.urlMapping")) {
                colspan = 2
            }
            textField(getTranslation("lbl.source")) {
                colspan = 2
                bind(binder).bind(UrlMapping::source)
                value = urlMapping.source ?: ""
            }
            textField(getTranslation("lbl.destination")) {
                colspan = 2
                bind(binder).bind(UrlMapping::destination)
                value = urlMapping.destination ?: ""
            }
        }
        horizontalLayout {
            closeButton(getTranslation("cmd.close")) {
                addClickListener{ _ -> this@apply.close() }
            }
            saveButton(getTranslation("cmd.set")) {
                this@apply.close()
                onSave()
            }
        }
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

    companion object {
        fun urlMapping(urlMapping: UrlMappingExt) =
            UrlMapping(
                source = urlMapping.source,
                destination = urlMapping.destination
            )
    }
}
