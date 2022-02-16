package nl.clockwork.ebms.admin.components

import com.vaadin.flow.component.AbstractCompositeField
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.HasValue.ValueChangeListener
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.timepicker.TimePicker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


class DateTimeSelect(dateLabel: String, timeLabel: String, colspan: Int) :
    AbstractCompositeField<HorizontalLayout, DateTimeSelect, LocalDateTime>(null), WithElement {
    private val datePicker: DatePicker
    private val timePicker: TimePicker

    init {
        setColSpan(this, colspan)
        datePicker = createDatePicker(dateLabel)
        timePicker = createTimePicker(timeLabel)
        datePicker.addValueChangeListener(datePickerChangeListener())
        timePicker.addValueChangeListener(timePickerChangeListener())
        content.add(datePicker, timePicker)
    }

    private fun createDatePicker(label: String): DatePicker =
        DatePicker(label).apply {
            setSizeFull()
            isClearButtonVisible = true
        }

    private fun createTimePicker(label: String): TimePicker =
        TimePicker(label).apply {
            setSizeFull()
            isClearButtonVisible = true
        }

    private fun datePickerChangeListener(): ValueChangeListener<in ComponentValueChangeEvent<DatePicker, LocalDate?>> =
        ValueChangeListener { event: ComponentValueChangeEvent<DatePicker, LocalDate?> ->
            datePicker.value?.let {
                setModelValue(
                    LocalDateTime.of(it, DEFAULT_TIME),
                    event.isFromClient
                )
                timePicker.value = DEFAULT_TIME
            } ?: clear()
        }

    private fun timePickerChangeListener(): ValueChangeListener<in ComponentValueChangeEvent<TimePicker?, LocalTime?>> =
        ValueChangeListener { event: ComponentValueChangeEvent<TimePicker?, LocalTime?> ->
            datePicker.value?.let {
                setModelValue(
                    LocalDateTime.of(
                        it,
                        timePicker.value ?: DEFAULT_TIME
                    ), event.isFromClient
                )
            }
        }

    override fun setPresentationValue(value: LocalDateTime?) =
        if (value == null) {
            datePicker.clear()
            timePicker.clear()
        } else {
            datePicker.value = value.toLocalDate()
            timePicker.value = value.toLocalTime()
        }

    companion object {
        private val DEFAULT_TIME = LocalTime.MIDNIGHT
    }
}
