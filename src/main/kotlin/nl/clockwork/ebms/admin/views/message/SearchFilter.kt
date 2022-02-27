package nl.clockwork.ebms.admin.views.message

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v10.formLayout
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasValue.ValueChangeListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datetimepicker.DateTimePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.listbox.MultiSelectListBox
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.DataProvider
import nl.clockwork.ebms.EbMSMessageStatus
import nl.clockwork.ebms.admin.EbMSMessage
import nl.clockwork.ebms.admin.EbMSMessageFilter
import nl.clockwork.ebms.admin.components.DateTimeSelect
import nl.clockwork.ebms.admin.components.PartySelect
import nl.clockwork.ebms.admin.views.WithBean
import nl.clockwork.ebms.jaxb.JAXBParser
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.xml.bind.JAXBException


object SearchFilter : WithBean {
    private val logger: Logger = LoggerFactory.getLogger(SearchFilter::class.java)

    fun HasComponents.searchFilter(
        messageFilter: EbMSMessageFilter,
        dataProvider: DataProvider<EbMSMessage, *>,
        hideFilter: Runnable
    ) : FormLayout {
        val binder = beanValidationBinder<EbMSMessageFilter>()
        binder.readBean(messageFilter)
        //TODO use binder
        return formLayout {
            createComboBox(getTranslation("lbl.cpaId"), ebMSAdminDAO.selectCPAIds(),2)
            PartySelect(getTranslation("lbl.fromPartyId"), getTranslation("lbl.fromRole"),2)
            PartySelect(getTranslation("lbl.toPartyId"), getTranslation("lbl.toRole"),2)
            createComboBox(getTranslation("lbl.service"), emptyList(),1)
            createComboBox(getTranslation("lbl.action"), emptyList(),1)
            createTextField(getTranslation("lbl.conversationId"),1)
            createTextField(getTranslation("lbl.messageId"),1)
            createTextField(getTranslation("lbl.refToMessageId"),1)
            createStatuses(1)
//            createDateTimePicker(getTranslation("lbl.from"),1)
            DateTimeSelect(getTranslation("lbl.fromDate"), getTranslation("lbl.fromTime"),1)
//            createDateTimePicker(getTranslation("lbl.to"),1)
            DateTimeSelect(getTranslation("lbl.toDate"), getTranslation("lbl.toTime"),1)
            createButton(getTranslation("cmd.search"), 1) {
                binder.writeBeanIfValid(messageFilter);
                dataProvider.refreshAll();
                hideFilter.run();
            }
            createButton(getTranslation("cmd.reset"),1) {
// TODO: fix                messageFilter.reset();
                binder.readBean(messageFilter);
                dataProvider.refreshAll();
                hideFilter.run();
            }
        }
    }

    private fun FormLayout.createComboBox(label: String, items: List<String>, colspan: Int): ComboBox<String> =
        comboBox(label) {
            setColspan(this, colspan)
            setItems(items)
            isClearButtonVisible = true
            isEnabled = items != emptyList<Any>()
        }

    private fun FormLayout.createTextField(label: String, colspan: Int): TextField =
        textField(label) {
            setColspan(this, colspan)
            isClearButtonVisible = true
        }

    private fun FormLayout.createStatuses(colspan: Int): MultiSelectListBox<EbMSMessageStatus> =
        multiSelectListBox() {
            setColspan(this, colspan)
            height = "11em"
            setItems(*EbMSMessageStatus.values())
            //getElement().setAttribute("size","5");
        }

    private fun FormLayout.createDateTimePicker(label: String, colspan: Int): DateTimePicker =
        dateTimePicker(label) {
            setColspan(this, colspan)
        }

    private fun FormLayout.createButton(
        label: String,
        colspan: Int,
        clickListener: ComponentEventListener<ClickEvent<Button>>
    ): Button =
        button(label) {
            setColspan(this, colspan)
            text = label
            addClickListener(clickListener)
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
}