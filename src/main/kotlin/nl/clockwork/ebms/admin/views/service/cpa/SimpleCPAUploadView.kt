package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import nl.clockwork.ebms.admin.components.closeButton
import nl.clockwork.ebms.admin.components.showErrorNotification
import nl.clockwork.ebms.admin.components.showSuccessNotification
import nl.ordina.cpa._2_18.CPAService


fun newCpaDialog(cpaClient: CPAService, onSave: () -> Unit = {}) : Dialog {
    val binder = beanValidationBinder<SimpleCpaUploadFormData>()
    val formData = SimpleCpaUploadFormData()
    binder.readBean(formData)
    val memoryBuffer = MemoryBuffer()
    return Dialog().apply {
        width = "80%"
        formLayout {
            setResponsiveSteps(FormLayout.ResponsiveStep("0", 2))
            upload(memoryBuffer) {
                text("CpaFile")
                colspan = 2
                addSucceededListener {
                    try {
                        val cpa = String(memoryBuffer.inputStream.readAllBytes())
                        if (formData.validate) {
                            cpaClient.validateCPA(cpa)
                            showSuccessNotification(getTranslation("cpa.valid"))
                        } else {
                            cpaClient.insertCPA(cpa, formData.overwrite)
                            onSave()
                            showSuccessNotification("Cpa uploaded")
                        }
                    } catch (e: java.lang.Exception) {
                        showErrorNotification(e.message)
                        throw e
                    }
                }
            }
            checkBox(getTranslation("lbl.validate")) {
                colspan = 2
                isEnabled = true
                addValueChangeListener {
                    formData.validate = it.value
                }
            }
            checkBox(getTranslation("lbl.overwrite")) {
                colspan = 2
                isEnabled = true
                addValueChangeListener {
                    formData.overwrite = it.value
                }
            }
        }
        closeButton(getTranslation("cmd.close")) {
            addClickListener{ _ -> this@apply.close() }
        }
    }
}

data class SimpleCpaUploadFormData(
    var validate: Boolean = false,
    var overwrite: Boolean = false
)