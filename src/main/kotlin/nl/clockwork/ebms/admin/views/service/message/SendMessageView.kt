package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
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
                addValueChangeListener { onCpaIdSelected(it) }
            }
            fromPartyIdSelect = aComboBox(getTranslation("lbl.fromPartyId"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::fromPartyId)
                addValueChangeListener { onFromPartyIdSelected(it) }
            }
            fromRoleSelect = aComboBox(getTranslation("lbl.fromRole"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::fromRole)
                addValueChangeListener { onFromRoleSelected(it) }
            }
            toPartyIdSelect = aComboBox(getTranslation("lbl.toPartyId"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::toPartyId)
                addValueChangeListener { onToPartyIdSelected(it) }
            }
            toRoleSelect = aComboBox(getTranslation("lbl.toRole"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::toRole)
                addValueChangeListener { onToRoleSelected(it) }
            }
            serviceSelect = aComboBox(getTranslation("lbl.service"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::service)
                addValueChangeListener { onServiceSelected(it) }
            }
            actionSelect = aComboBox(getTranslation("lbl.action"), emptyList(),2) {
                bind(binder).bind(SendMessageFormData::action)
                addValueChangeListener { onActionSelected(it) }
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
            resetButton(getTranslation("cmd.reset"), binder)
        }
    }

    private fun onCpaIdSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            fromPartyIdSelect.isEnabled = true
            fromPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(it)))
        } ?: fromPartyIdSelect.disable()
        onFromPartyIdSelected(null)
    }

    private fun getCpa(cpaId: String) =
        JAXBParser.getInstance(CollaborationProtocolAgreement::class.java)
            .handleUnsafe(cpaClient.getCPA(cpaId))

    private fun ComboBox<*>.disable() {
        setItems(emptyList())
        isEnabled = false
    }

    private fun onFromPartyIdSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            fromRoleSelect.isEnabled = true
            fromRoleSelect.setItems(CPAUtils.getRoleNames(getCpa(cpaIdSelect.value), it))

        } ?: fromRoleSelect.disable()
        onFromRoleSelected(null)
    }

    private fun onFromRoleSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            toPartyIdSelect.isEnabled = true
            toPartyIdSelect.setItems(CPAUtils.getOtherPartyIds(getCpa(cpaIdSelect.value), fromPartyIdSelect.value))
        } ?: toPartyIdSelect.disable()
        onToPartyIdSelected(null)
    }

    private fun onToPartyIdSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            toRoleSelect.isEnabled = true
            toRoleSelect.setItems(CPAUtils.getRoleNames(getCpa(cpaIdSelect.value), it))
        } ?: toRoleSelect.disable()
        onToRoleSelected(null)
    }

    private fun onToRoleSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            serviceSelect.isEnabled = true
            serviceSelect.setItems(CPAUtils.getServiceNamesCanSend(
                getCpa(cpaIdSelect.value),
                fromPartyIdSelect.value,
                fromRoleSelect.value
            ).intersect(
                CPAUtils.getServiceNamesCanReceive(
                    getCpa(cpaIdSelect.value),
                    toPartyIdSelect.value,
                    toRoleSelect.value
                ).toSet()
            ))
        } ?: serviceSelect.disable()
        onServiceSelected(null)
    }

    private fun onServiceSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            actionSelect.isEnabled = true
            actionSelect.setItems(CPAUtils.getFromActionNamesCanSend(
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
            ))
        } ?: actionSelect.disable()
        onActionSelected(null)
    }

    private fun onActionSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        submitButton.isEnabled = e?.value != null
    }

    private fun @VaadinDsl HorizontalLayout.sendButton(text: String) =
        button(text) {
            isEnabled = false
            onLeftClick {
                val formData = SendMessageFormData()
                if (binder.writeBeanIfValid(formData)) {
                    try {
                        ebMSMessageClient.sendMessage(
                            MessageRequest().apply {
                                properties = MessageRequestProperties().apply {
                                    cpaId = formData.cpaId
                                    fromPartyId = formData.fromPartyId
                                    fromRole = formData.fromRole
                                    toPartyId = formData.toPartyId
                                    toRole = formData.toRole
                                    service = formData.service
                                    action = formData.action
                                    conversationId = formData.conversationId
                                    messageId = formData.messageId
                                    refToMessageId = formData.refToMessageId
                                }
//                                dataSource = emptyList<DataSource>()
                            }
                        )
                        showSuccessNotification(getTranslation("sendMessage.ok"))
                    } catch (e: EbMSMessageServiceException) {
                        logger.error("", e)
                        showErrorNotification(e.message)
                    }
                } else {
                    showErrorNotification("Invalid data")
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
)