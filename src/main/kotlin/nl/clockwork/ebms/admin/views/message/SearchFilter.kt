package nl.clockwork.ebms.admin.views.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
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


class SearchFilter(
    messageFilter: EbMSMessageFilter,
    onUpdate: () -> Unit = {}
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
            binder.readBean(SearchFilterFormData(messageFilter))
            formLayout {
                setResponsiveSteps(FormLayout.ResponsiveStep("0", 2))
                classNames.add("panel")
                cpaIdSelect = aComboBox(getTranslation("lbl.cpaId"), ebMSAdminDAO.selectCPAIds(),2) {
                    bind(binder).bind(SearchFilterFormData::cpaId)
                    addValueChangeListener { onCpaIdChanged(it) }
                }
                fromPartyIdSelect = aComboBox(getTranslation("lbl.fromPartyId"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::fromPartyId)
                    addValueChangeListener { onFromPartyIdChanged(it) }
                }
                fromRoleSelect = aComboBox(getTranslation("lbl.fromRole"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::fromRole)
                    addValueChangeListener { onFromRoleChanged(it) }
                }
                toPartyIdSelect = aComboBox(getTranslation("lbl.toPartyId"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::toPartyId)
                    addValueChangeListener { onToPartyIdChanged(it) }
                }
                toRoleSelect = aComboBox(getTranslation("lbl.toRole"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::toRole)
                    addValueChangeListener { onToRoleChanged(it) }
                }
                serviceSelect = aComboBox(getTranslation("lbl.service"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::service)
                    addValueChangeListener { onServiceChanged(it) }
                }
                actionSelect = aComboBox(getTranslation("lbl.action"), emptyList(),1) {
                    bind(binder).bind(SearchFilterFormData::action)
                    addValueChangeListener { onActionChanged(it) }
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
                messageStatuses(1)
//                createDateTimePicker(getTranslation("lbl.from"),1)
                add(DateTimeSelect(getTranslation("lbl.fromDate"), getTranslation("lbl.fromTime"), 1))
//                createDateTimePicker(getTranslation("lbl.to"),1)
                add(DateTimeSelect(getTranslation("lbl.toDate"), getTranslation("lbl.toTime"), 1))
                aButton(getTranslation("cmd.search"), Icon("lumo", "search"), 1) {
                    onLeftClick {
                        val formData = SearchFilterFormData()
                        if (binder.writeBeanIfValid(formData)) {
                            try {
                                formData.toMessageFilter(messageFilter)
                                onUpdate()
                            } catch (e: EbMSMessageServiceException) {
                                logger.error("", e)
                                showErrorNotification(e.message)
                            }
                        }
                    }
                }
                resetButton(getTranslation("cmd.reset")) {
                    val formData = SearchFilterFormData()
                    binder.readBean(formData)
                    formData.toMessageFilter(messageFilter)
                    onUpdate()
                }
            }
        }

    private fun onCpaIdChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            fromPartyIdSelect.isEnabled = true
            fromPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(it)))
            toPartyIdSelect.isEnabled = true
            toPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(it)))
        } ?: run {
            fromPartyIdSelect.disable()
            toPartyIdSelect.disable()
        }
    }

    private fun getCpa(cpaId: String) =
        JAXBParser.getInstance(CollaborationProtocolAgreement::class.java)
            .handleUnsafe(ebMSAdminDAO.findCPA(cpaId)?.cpa)

    private fun onFromPartyIdChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            val roleNames = CPAUtils.getRoleNames(getCpa(cpaIdSelect.value), it)
            fromRoleSelect.enable(roleNames)
            toPartyIdSelect.disable()
        } ?: run {
            fromRoleSelect.disable()
            cpaIdSelect.value?.let {
                toPartyIdSelect.isEnabled = true
                toPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(it)))
            }
        }
    }

    private fun onFromRoleChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            serviceSelect.enable(
                CPAUtils.getServiceNamesCanSend(
                    getCpa(cpaIdSelect.value),
                    fromPartyIdSelect.value,
                    fromRoleSelect.value
                ))
        } ?: serviceSelect.disable()
    }

    private fun onToPartyIdChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            val roleNames = CPAUtils.getRoleNames(getCpa(cpaIdSelect.value), it)
            toRoleSelect.enable(roleNames)
            fromPartyIdSelect.disable()
        } ?: run {
            toRoleSelect.disable()
            cpaIdSelect.value?.let {
                fromPartyIdSelect.isEnabled = true
                fromPartyIdSelect.setItems(CPAUtils.getPartyIds(getCpa(it)))
            }
        }
    }

    private fun onToRoleChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            serviceSelect.enable(
                CPAUtils.getServiceNamesCanReceive(
                    getCpa(cpaIdSelect.value),
                    toPartyIdSelect.value,
                    toRoleSelect.value
                )
            )
        } ?: serviceSelect.disable()
    }

    private fun onServiceChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
        e.value?.let {
            actionSelect.enable(fromRoleSelect.value?.let {
                CPAUtils.getFromActionNamesCanSend(
                    getCpa(cpaIdSelect.value),
                    fromPartyIdSelect.value,
                    fromRoleSelect.value,
                    serviceSelect.value
                )
            } ?: CPAUtils.getFromActionNamesCanReceive(
                getCpa(cpaIdSelect.value),
                toPartyIdSelect.value,
                toRoleSelect.value,
                serviceSelect.value
            ))
        } ?: actionSelect.disable()
    }

    private fun onActionChanged(e: ComponentValueChangeEvent<ComboBox<String>, String>) {
//        submitButton.isEnabled = e?.value != null
    }

    private fun FormLayout.messageStatuses(colspan: Int): MultiSelectListBox<EbMSMessageStatus> =
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
) {
    fun toMessageFilter(filter: EbMSMessageFilter) {
        filter.cpaId = cpaId
        filter.fromParty = fromPartyId?.let { Party(it, fromRole) }
        filter.toParty = toPartyId?.let { Party(it, fromRole) }
        filter.service = service
        filter.action = action
        filter.conversationId = conversationId
        filter.messageId = messageId
        filter.refToMessageId = refToMessageId
        filter.statuses = status
        filter.from = fromDate
        filter.to = toDate
    }

    companion object {
        operator fun invoke(messageFilter: EbMSMessageFilter) =
            SearchFilterFormData(
                cpaId = messageFilter.cpaId,
                fromPartyId = messageFilter.fromParty?.partyId,
                fromRole = messageFilter.fromParty?.role,
                toPartyId = messageFilter.toParty?.partyId,
                toRole = messageFilter.toParty?.role,
                service = messageFilter.service,
                action = messageFilter.action,
                conversationId = messageFilter.conversationId,
                messageId = messageFilter.messageId,
                refToMessageId = messageFilter.refToMessageId,
                status = messageFilter.statuses,
                fromDate = messageFilter.from,
                toDate = messageFilter.to
            )
    }
}