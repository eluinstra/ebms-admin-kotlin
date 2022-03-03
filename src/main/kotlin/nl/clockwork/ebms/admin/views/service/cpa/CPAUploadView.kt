package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import java.io.InputStream
import javax.validation.constraints.NotEmpty


@Route(value = "service/cpaUpload", layout = MainLayout::class)
@PageTitle("cpaUpload")
class CPAUploadView : KComposite(), WithBean {
    private val formData = CpaUploadFormData()
    private val root = ui {
        verticalLayout {
            h1(getTranslation("cpaUpload"))
            cpaUploadForm()
        }
    }

    private fun HasComponents.cpaUploadForm() : FormLayout {
        val binder = beanValidationBinder<CpaUploadFormData>()
        binder.readBean(formData)
        //TODO use binder
        val memoryBuffer = MemoryBuffer()
        return formLayout {
            val uploadButton = createButton(getTranslation("cmd.upload"), 1) {
                isEnabled = formData.cpaFile != null
                addClickListener {
                    formData.cpaFile?.let { cpaClient.insertCPA(String(it.readAllBytes()), formData.overwrite) }
                }
            }
            upload(memoryBuffer) {
                text("CpaFile")
                colspan = 2
                addSucceededListener {
                    formData.cpaFile = memoryBuffer.inputStream
                    uploadButton.isEnabled = true
                }
            }
            checkBox(getTranslation("lbl.overwrite")) {
                colspan = 2
                addValueChangeListener {
                    formData.overwrite = it.value
                }
            }
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                add(uploadButton)
            }
        }
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
}

data class CpaUploadFormData(
    @field:NotEmpty
    var cpaFile: InputStream? = null,
    var overwrite: Boolean = false
)
