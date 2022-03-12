package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.CPAUtils
import nl.clockwork.ebms.admin.components.*
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import nl.clockwork.ebms.jaxb.JAXBParser
import nl.ordina.ebms._2.MessageRequest
import nl.ordina.ebms._2.MessageRequestProperties
import nl.ordina.ebms._2_18.EbMSMessageServiceException
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotEmpty


@Route(value = "service/message/sendMessage", layout = MainLayout::class)
@PageTitle("Send Message")
class SendMessageView : KComposite(), WithBean {
    private val binder = beanValidationBinder<SendMessageFormData>()
    private lateinit var cpaIdSelect: ComboBox<String>
    private lateinit var fromPartyIdSelect: ComboBox<String>
    private lateinit var fromRoleSelect: ComboBox<String>
    private lateinit var toPartyIdSelect: ComboBox<String>
    private lateinit var toRoleSelect: ComboBox<String>
    private lateinit var serviceSelect: ComboBox<String>
    private lateinit var actionSelect: ComboBox<String>
    private lateinit var submitButton: Button

    private val root = ui {
        verticalLayout {
            h2(getTranslation("messageSend"))
            sendMessageForm()
        }
    }

    private fun HasComponents.sendMessageForm() {
        binder.readBean(SendMessageFormData())
        formLayout {
            cpaIdSelect = aComboBox(getTranslation("lbl.cpaId"), cpaClient.cpaIds,2) {
                bind(binder).bind(SendMessageFormData::cpaId)
                addValueChangeListener { onCpaIdChanged(it) }
            }
            fromPartyIdSelect = aComboBox(getTranslation("lbl.fromPartyId"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::fromPartyId)
                addValueChangeListener { onFromPartyIdChanged(it) }
            }
            fromRoleSelect = aComboBox(getTranslation("lbl.fromRole"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::fromRole)
                addValueChangeListener { onFromRoleChanged(it) }
            }
            toPartyIdSelect = aComboBox(getTranslation("lbl.toPartyId"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::toPartyId)
                addValueChangeListener { onToPartyIdChanged(it) }
            }
            toRoleSelect = aComboBox(getTranslation("lbl.toRole"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::toRole)
                addValueChangeListener { onToRoleChanged(it) }
            }
            serviceSelect = aComboBox(getTranslation("lbl.service"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::service)
                addValueChangeListener { onServiceChanged(it) }
            }
            actionSelect = aComboBox(getTranslation("lbl.action"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::action)
                addValueChangeListener { onActionChanged(it) }
            }
            aTextField(getTranslation("lbl.conversationId"), 2) {
                bind(binder).bind(SendMessageFormData::conversationId)
            }
            aTextField(getTranslation("lbl.messageId"), 2) {
                bind(binder).bind(SendMessageFormData::messageId)
            }
            aTextField(getTranslation("lbl.refToMessageId"), 2) {
                bind(binder).bind(SendMessageFormData::refToMessageId)
            }
        }
        horizontalLayout {
            backButton(getTranslation("cmd.back"))
            submitButton = sendButton(getTranslation("cmd.send"))
            resetButton(getTranslation("cmd.reset")) {
                binder.readBean(SendMessageFormData())
            }
        }
    }

    private fun onCpaIdChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            fromPartyIdSelect.enable(CPAUtils.getPartyIds(getCpa(it)))
        } ?: fromPartyIdSelect.disable()
    }

    private fun getCpa(cpaId: String) =
        JAXBParser.getInstance(CollaborationProtocolAgreement::class.java)
            .handleUnsafe(cpaClient.getCPA(cpaId))

    private fun onFromPartyIdChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            fromRoleSelect.enable(CPAUtils.getRoleNames(getCpa(cpaIdSelect.value), it))

        } ?: fromRoleSelect.disable()
    }

    private fun onFromRoleChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            toPartyIdSelect.enable(CPAUtils.getOtherPartyIds(getCpa(cpaIdSelect.value), fromPartyIdSelect.value))
        } ?: toPartyIdSelect.disable()
    }

    private fun onToPartyIdChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            toRoleSelect.enable(CPAUtils.getRoleNames(getCpa(cpaIdSelect.value), it))
        } ?: toRoleSelect.disable()
    }

    private fun onToRoleChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            serviceSelect.enable(CPAUtils.getServiceNamesCanSend(
                getCpa(cpaIdSelect.value),
                fromPartyIdSelect.value,
                fromRoleSelect.value
            ).intersect(
                CPAUtils.getServiceNamesCanReceive(
                    getCpa(cpaIdSelect.value),
                    toPartyIdSelect.value,
                    toRoleSelect.value
                ).toSet()
            ).toList())
        } ?: serviceSelect.disable()
    }

    private fun onServiceChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            actionSelect.enable(CPAUtils.getFromActionNamesCanSend(
                getCpa(cpaIdSelect.value),
                fromPartyIdSelect.value,
                fromRoleSelect.value,
                serviceSelect.value
            ).intersect(
                CPAUtils.getFromActionNamesCanReceive(
                    getCpa(cpaIdSelect.value),
                    toPartyIdSelect.value,
                    toRoleSelect.value,
                    serviceSelect.value
                ).toSet()
            ).toList())
        } ?: actionSelect.disable()
    }

    private fun onActionChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        submitButton.isEnabled = e.value != null
    }

    private fun @VaadinDsl HorizontalLayout.sendButton(text: String) =
        button(text, Icon("lumo", "checkmark")) {
            isEnabled = false
            onLeftClick {
                val formData = SendMessageFormData()
                if (binder.writeBeanIfValid(formData)) {
                    try {
                        val messageId = ebMSMessageClient.sendMessage(
                            MessageRequest().apply {
                                properties = formData.toMessageProperties()
//                                dataSource = emptyList<DataSource>()
                            }
                        )
                        showSuccessNotification(getTranslation("sendMessage.ok", messageId))
                    } catch (e: EbMSMessageServiceException) {
                        logger.error("", e)
                        showErrorNotification(e.message)
                    }
                }
            }
            setPrimary()
        }

    companion object {
        private val logger = LoggerFactory.getLogger(SendMessageView::class.java)
    }
}

data class SendMessageFormData(
    @field:NotEmpty
    var cpaId: String? = null,
    @field:NotEmpty
    var fromPartyId: String? = null,
    @field:NotEmpty
    var fromRole: String? = null,
    @field:NotEmpty
    var toPartyId: String? = null,
    @field:NotEmpty
    var toRole: String? = null,
    @field:NotEmpty
    var service: String? = null,
    @field:NotEmpty
    var action: String? = null,
    var conversationId: String? = null,
    var messageId: String? = null,
    var refToMessageId: String? = null
) {
    fun toMessageProperties() =
        MessageRequestProperties().also {
            it.cpaId = cpaId
            it.fromPartyId = fromPartyId
            it.fromRole = fromRole
            it.toPartyId = toPartyId
            it.toRole = toRole
            it.service = service
            it.action = action
            it.conversationId = conversationId
            it.messageId = messageId
            it.refToMessageId = refToMessageId
        }
}