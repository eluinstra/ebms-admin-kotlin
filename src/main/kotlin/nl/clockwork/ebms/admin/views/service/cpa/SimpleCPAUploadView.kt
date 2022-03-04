package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.components.showErrorNotification
import nl.clockwork.ebms.admin.components.showSuccessNotification
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import java.io.InputStream
import javax.validation.constraints.NotEmpty


@Route(value = "service/simpleCpaUpload", layout = MainLayout::class)
@PageTitle("cpaUpload")
class SimpleCPAUploadView : KComposite(), WithBean {
    private val formData = SimpleCpaUploadFormData()
    private val root = ui {
        verticalLayout {
            h2(getTranslation("cpaUpload"))
            cpaUploadForm()
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
            }
        }
    }

    private fun HasComponents.cpaUploadForm(): FormLayout {
        val memoryBuffer = MemoryBuffer()
        return formLayout {
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
    }
}

data class SimpleCpaUploadFormData(
    var validate: Boolean = false,
    var overwrite: Boolean = false
)