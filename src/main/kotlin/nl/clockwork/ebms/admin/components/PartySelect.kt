package nl.clockwork.ebms.admin.components

import com.vaadin.flow.component.AbstractCompositeField
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.HasValue.ValueChangeListener
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import nl.clockwork.ebms.admin.CPAUtils
import nl.clockwork.ebms.service.model.Party
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement


class PartySelect constructor(partyIdLabel: String, roleLabel: String, colspan: Int) :
    AbstractCompositeField<HorizontalLayout, PartySelect, Party>(null), WithElement {
    private var cpa: CollaborationProtocolAgreement? = null
    private val partyIdSelect: ComboBox<String>
    private val roleSelect: ComboBox<String>

    init {
        setColSpan(this, colspan)
        partyIdSelect = createComboBox(partyIdLabel, emptyList<String>())
        roleSelect = createComboBox(roleLabel, emptyList<String>())
        partyIdSelect.addValueChangeListener(partyIdSelectChangeListener())
        roleSelect.addValueChangeListener(roleSelectChangeListener())
        content.add(partyIdSelect, roleSelect)
    }

    private fun createComboBox(label: String, items: List<String>): ComboBox<String> =
        ComboBox<String>(label).apply {
            setSizeFull()
            setItems(items)
            isEnabled = false
            isClearButtonVisible = true
        }

    private fun partyIdSelectChangeListener(): ValueChangeListener<in ComponentValueChangeEvent<ComboBox<String>, String>> =
        ValueChangeListener {
            val partyId = partyIdSelect.value
            if (partyId != null)
                setModelValue(Party(partyId, null), it.isFromClient)
            else
                clear()
            val isEnabled = cpa != null && partyIdSelect.value != null
            roleSelect.setItems(
                if (isEnabled)
                    CPAUtils.getRoleNames(
                        cpa!!,
                        partyIdSelect.value
                    )
                else
                    emptyList()
            )
            roleSelect.setEnabled(isEnabled)
        }

    private fun roleSelectChangeListener(): ValueChangeListener<in ComponentValueChangeEvent<ComboBox<String>, String>> =
        ValueChangeListener {
            val partyId = partyIdSelect.value
            val role = roleSelect.value
            if (partyId != null && role != null)
                setModelValue(Party(partyId, role), it.isFromClient)
        }

    fun updateState(cpa: CollaborationProtocolAgreement?) {
        if (this.cpa !== cpa) {
            this.cpa = cpa
            reset(cpa)
        }
    }

    private fun reset(cpa: CollaborationProtocolAgreement?) {
        setPartIdSelect(cpa)
        roleSelect.disable()
    }

    private fun setPartIdSelect(cpa: CollaborationProtocolAgreement?) {
        partyIdSelect.isEnabled = cpa != null
        partyIdSelect.setItems(if (cpa != null) CPAUtils.getPartyIds(cpa) else emptyList())
    }

    private fun ComboBox<String>.disable() {
        setItems(emptyList<String>())
        isEnabled = false
    }

    override fun setPresentationValue(party: Party?) {
        if (party == null) {
            partyIdSelect.clear()
            roleSelect.clear()
        } else {
            partyIdSelect.setValue(party.partyId)
            roleSelect.setValue(party.role)
        }
    }
}
