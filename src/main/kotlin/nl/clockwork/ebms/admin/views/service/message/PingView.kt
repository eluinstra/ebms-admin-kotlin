package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.CPAUtils
import nl.clockwork.ebms.admin.components.aComboBox
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import nl.clockwork.ebms.admin.views.service.cpa.UrlMappingsView
import nl.clockwork.ebms.jaxb.JAXBParser
import nl.ordina.ebms._2_18.EbMSMessageServiceException
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement
import javax.validation.constraints.NotEmpty


@Route(value = "service/message/ping", layout = MainLayout::class)
@PageTitle("Ping")
class PingView : KComposite(), WithBean {
    private val binder = beanValidationBinder<PingFormData>()
    private lateinit var cpaIdSelect: ComboBox<String>
    private lateinit var fromPartyIdSelect: ComboBox<String>
    private lateinit var toPartyIdSelect: ComboBox<String>
    private lateinit var submitButton: Button

    private val root = ui {
        verticalLayout {
            h1(getTranslation("ping"))
            pingForm()
        }
    }

    private fun HasComponents.pingForm() {
        binder.readBean(PingFormData())
        formLayout {
            cpaIdSelect = aComboBox(getTranslation("lbl.cpaId"), cpaClient.cpaIds,2) {
                bind(binder).bind(PingFormData::cpaId)
                addValueChangeListener { onCpaIdSelected(it) }
            }
            fromPartyIdSelect = aComboBox(getTranslation("lbl.fromPartyId"), emptyList(),2) {
                bind(binder).bind(PingFormData::fromPartyId)
                addValueChangeListener { onFromPartyIdSelected(it) }
            }
            toPartyIdSelect = aComboBox(getTranslation("lbl.toPartyId"), emptyList(),2) {
                bind(binder).bind(PingFormData::toPartyId)
                addValueChangeListener { onToPartyIdSelected(it) }
            }
        }
        horizontalLayout {
            submitButton = saveButton(getTranslation("cmd.ping"))
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun onCpaIdSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            fromPartyIdSelect.isEnabled = true
            fromPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(it)))
        } ?: run {
            fromPartyIdSelect.disable()
        }
        toPartyIdSelect.disable()
        submitButton.isEnabled = false
    }

    private fun getCpa(cpaId: String) =
        JAXBParser.getInstance(CollaborationProtocolAgreement::class.java)
            .handleUnsafe(cpaClient.getCPA(cpaId))

    private fun ComboBox<*>.disable() {
        setItems(emptyList())
        isEnabled = false
    }

    private fun onFromPartyIdSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            toPartyIdSelect.isEnabled = true
            toPartyIdSelect.setItems(CPAUtils.getOtherPartyIds(getCpa(cpaIdSelect.value), it))

        } ?: run {
            toPartyIdSelect.disable()
        }
        submitButton.isEnabled = false
    }

    private fun onToPartyIdSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        submitButton.isEnabled = e.value != null
    }

    private fun @VaadinDsl HorizontalLayout.saveButton(text: String?) =
        button(text) {
            isEnabled = false
            onLeftClick {
                val pingFormData = PingFormData()
                if (binder.writeBeanIfValid(pingFormData)) {
                    with(pingFormData) {
                        try {
                            ebMSMessageClient.ping(cpaId, fromPartyId, toPartyId)
                        } catch (e: EbMSMessageServiceException) {
                            // TODO show error
                            println(e)
                        }
                    }
                } else {
                    //TODO show error
                }
            }
            setPrimary()
        }

}

data class PingFormData(
    @field:NotEmpty
    var cpaId: String? = null,
    @field:NotEmpty
    var fromPartyId: String? = null,
    @field:NotEmpty
    var toPartyId: String? = null
)