package nl.clockwork.ebms.admin.views.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.HasValue.ValueChangeListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datetimepicker.DateTimePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.listbox.MultiSelectListBox
import nl.clockwork.ebms.EbMSMessageStatus
import nl.clockwork.ebms.admin.CPAUtils
import nl.clockwork.ebms.admin.EbMSMessageFilter
import nl.clockwork.ebms.admin.components.*
import nl.clockwork.ebms.admin.views.WithBean
import nl.clockwork.ebms.jaxb.JAXBParser
import nl.clockwork.ebms.service.model.Party
import nl.ordina.ebms._2_18.EbMSMessageServiceException
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import javax.xml.bind.JAXBException


class SearchFilter(
    messageFilter: EbMSMessageFilter,
    refresh: () -> Unit = {}
) : KComposite(), WithBean {
    private val binder = beanValidationBinder<SearchFilterFormData>()
    private lateinit var cpaIdSelect: ComboBox<String>
    private lateinit var fromPartyIdSelect: ComboBox<String>
    private lateinit var fromRoleSelect: ComboBox<String>
    private lateinit var toPartyIdSelect: ComboBox<String>
    private lateinit var toRoleSelect: ComboBox<String>
    private lateinit var serviceSelect: ComboBox<String>
    private lateinit var actionSelect: ComboBox<String>

    private val root =
        ui {
            binder.readBean(SearchFilterFormData())
            formLayout {
                setResponsiveSteps(FormLayout.ResponsiveStep("0", 2))
                classNames.add("panel")
                cpaIdSelect = aComboBox(getTranslation("lbl.cpaId"), ebMSAdminDAO.selectCPAIds(),2) {
                    bind(binder).bind(SearchFilterFormData::cpaId)
                    addValueChangeListener { onCpaIdSelected(it) }
                }
                fromPartyIdSelect = aComboBox(getTranslation("lbl.fromPartyId"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::fromPartyId)
                    addValueChangeListener { onFromPartyIdSelected(it) }
                }
                fromRoleSelect = aComboBox(getTranslation("lbl.fromRole"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::fromRole)
                    addValueChangeListener { onFromRoleSelected(it) }
                }
                toPartyIdSelect = aComboBox(getTranslation("lbl.toPartyId"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::toPartyId)
                    addValueChangeListener { onToPartyIdSelected(it) }
                }
                toRoleSelect = aComboBox(getTranslation("lbl.toRole"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::toRole)
                    addValueChangeListener { onToRoleSelected(it) }
                }
                serviceSelect = aComboBox(getTranslation("lbl.service"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::service)
                    addValueChangeListener { onServiceSelected(it) }
                }
                actionSelect = aComboBox(getTranslation("lbl.action"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::action)
                    addValueChangeListener { onActionSelected(it) }
                }
                aTextField(getTranslation("lbl.conversationId"), 1) {
                    bind(binder).bind(SearchFilterFormData::conversationId)
                }
                aTextField(getTranslation("lbl.messageId"), 1) {
                    bind(binder).bind(SearchFilterFormData::messageId)
                }
                aTextField(getTranslation("lbl.refToMessageId"), 1) {
                    bind(binder).bind(SearchFilterFormData::refToMessageId)
                }
                createStatuses(1)
//                createDateTimePicker(getTranslation("lbl.from"),1)
                add(DateTimeSelect(getTranslation("lbl.fromDate"), getTranslation("lbl.fromTime"), 1))
//                createDateTimePicker(getTranslation("lbl.to"),1)
                add(DateTimeSelect(getTranslation("lbl.toDate"), getTranslation("lbl.toTime"), 1))
                aButton(getTranslation("cmd.search"), Icon("lumo", "search"), 1) {
                    onLeftClick {
                        val formData = SearchFilterFormData()
                        if (binder.writeBeanIfValid(formData)) {
                            try {
                                with(messageFilter) {
                                    cpaId = formData.cpaId
                                    fromParty = formData.fromPartyId?.let { Party(it, formData.fromRole) }
                                    toParty = formData.toPartyId?.let { Party(it, formData.fromRole) }
                                    service = formData.service
                                    action = formData.action
                                    conversationId = formData.conversationId
                                    messageId = formData.messageId
                                    refToMessageId = formData.refToMessageId
                                    statuses = formData.status
                                    from = formData.fromDate
                                    to = formData.toDate
                                }
                                refresh()
//                                showSuccessNotification(getTranslation("search.ok"))
                            } catch (e: EbMSMessageServiceException) {
                                logger.error("", e)
                                showErrorNotification(e.message)
                            }
                        } else {
                            showErrorNotification("Invalid data")
                        }
                    }
                }
                resetButton(getTranslation("cmd.reset")) {
                    binder.readBean(SearchFilterFormData())
                    refresh()
                }
            }
        }

    private fun onCpaIdSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            fromPartyIdSelect.isEnabled = true
            fromPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(it)))
            toPartyIdSelect.isEnabled = true
            toPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(it)))
        } ?: run {
            fromPartyIdSelect.disable()
            toPartyIdSelect.disable()
        }
        onFromPartyIdSelected(null)
        onToPartyIdSelected(null)
    }

    private fun getCpa(cpaId: String) =
        JAXBParser.getInstance(CollaborationProtocolAgreement::class.java)
            .handleUnsafe(ebMSAdminDAO.findCPA(cpaId)?.cpa)

    private fun ComboBox<*>.disable() {
        setItems(emptyList())
        isEnabled = false
    }

    private fun onFromPartyIdSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            fromRoleSelect.isEnabled = true
            fromRoleSelect.setItems(CPAUtils.getRoleNames(getCpa(cpaIdSelect.value), it))
            toPartyIdSelect.disable()

        } ?: run {
            fromRoleSelect.disable()
            toPartyIdSelect.isEnabled = true
            toPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(cpaIdSelect.value)))
        }
        onFromRoleSelected(null)
    }

    private fun onFromRoleSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            serviceSelect.isEnabled = true
            serviceSelect.setItems(
                CPAUtils.getServiceNamesCanSend(
                    getCpa(cpaIdSelect.value),
                    fromPartyIdSelect.value,
                    fromRoleSelect.value
                ))
        } ?: serviceSelect.disable()
        onServiceSelected(null)
    }

    private fun onToPartyIdSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            toRoleSelect.isEnabled = true
            toRoleSelect.setItems(CPAUtils.getRoleNames(getCpa(cpaIdSelect.value), it))
            fromPartyIdSelect.disable()
        } ?: run {
            toRoleSelect.disable()
            fromPartyIdSelect.isEnabled = true
            fromPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(cpaIdSelect.value)))
        }
        onToRoleSelected(null)
    }

    private fun onToRoleSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            serviceSelect.isEnabled = true
            serviceSelect.setItems(
                CPAUtils.getServiceNamesCanReceive(
                    getCpa(cpaIdSelect.value),
                    toPartyIdSelect.value,
                    toRoleSelect.value
                ))
        } ?: serviceSelect.disable()
        onServiceSelected(null)
    }

    private fun onServiceSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
        e?.value?.let {
            actionSelect.isEnabled = true
            actionSelect.setItems(
                fromRoleSelect.value?.let {
                    CPAUtils.getFromActionNamesCanSend(
                    getCpa(cpaIdSelect.value),
                    fromPartyIdSelect.value,
                    fromRoleSelect.value,
                    serviceSelect.value)
                } ?: CPAUtils.getFromActionNamesCanReceive(
                    getCpa(cpaIdSelect.value),
                    toPartyIdSelect.value,
                    toRoleSelect.value,
                    serviceSelect.value
            ))
        } ?: actionSelect.disable()
        onActionSelected(null)
    }

    private fun onActionSelected(e: ComponentValueChangeEvent<ComboBox<String>, String>?) {
//        submitButton.isEnabled = e?.value != null
    }

    private fun FormLayout.createStatuses(colspan: Int): MultiSelectListBox<EbMSMessageStatus> =
        multiSelectListBox() {
            setColspan(this, colspan)
            height = "11em"
            setItems(*EbMSMessageStatus.values())
            //getElement().setAttribute("size","5")
        }

    private fun FormLayout.createDateTimePicker(label: String, colspan: Int): DateTimePicker =
        dateTimePicker(label) {
            setColspan(this, colspan)
        }

    private fun FormLayout.aButton(
        label: String,
        icon: Icon,
        colspan: Int,
        block: (@VaadinDsl Button).() -> Unit = {}
    ): Button =
        button(label, icon) {
            setColspan(this, colspan)
            text = label
            block()
        }

    private fun cpaSelectChangeListener(
        cpaComboBox: ComboBox<String>,
        fromPartySelect: PartySelect,
        toPartySelect: PartySelect
    ): ValueChangeListener<in ComponentValueChangeEvent<ComboBox<String>, String>> {
        return ValueChangeListener {
            try {
                val value = cpaComboBox.value?.let { ebMSAdminDAO?.findCPA(cpaComboBox.value!!) }
                val cpa: CollaborationProtocolAgreement? =
                    value?.let { JAXBParser.getInstance(CollaborationProtocolAgreement::class.java).handleUnsafe(value.cpa) }
                fromPartySelect.updateState(cpa)
                toPartySelect.updateState(cpa)
            } catch (e: JAXBException) {
                logger.error("", e)
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SearchFilter::class.java)
    }
}

data class SearchFilterFormData(
    var cpaId: String? = null,
    var fromPartyId: String? = null,
    var fromRole: String? = null,
    var toPartyId: String? = null,
    var toRole: String? = null,
    var service: String? = null,
    var action: String? = null,
    var conversationId: String? = null,
    var messageId: String? = null,
    var refToMessageId: String? = null,
    var status: Set<EbMSMessageStatus> = emptySet(),
    var fromDate: LocalDateTime? = null,
    var toDate: LocalDateTime? = null,
)