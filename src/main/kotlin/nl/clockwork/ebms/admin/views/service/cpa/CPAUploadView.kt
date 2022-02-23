package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.components.WithBinder
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import java.io.InputStream


@Route(value = "service/cpaUpload", layout = MainLayout::class)
@PageTitle("cpaUpload")
class CPAUploadView : KComposite(), WithBean, WithBinder {
    private val formData = EditUploadFormData()
    private val root = ui {
        verticalLayout {
            editUploadForm()
        }
    }

    private fun HasComponents.editUploadForm() : FormLayout {
        val binder: Binder<EditUploadFormData> = createBinder(EditUploadFormData::class.java)
        binder.readBean(formData)
        val memoryBuffer = MemoryBuffer()
        return formLayout {
            val uploadButton = createButton(getTranslation("cmd.upload"), 1) {
                isEnabled = formData.cpaFile != null
                addClickListener {
                    formData.cpaFile?.let { cpaClient!!.insertCPA(formData.overwrite, String(it.readAllBytes())) }
                }
            }
            upload(memoryBuffer) {
                text("CpaFile")
                addSucceededListener {
                    formData.cpaFile = memoryBuffer.inputStream
                    uploadButton.isEnabled = true
                }
            }
            checkBox(getTranslation("lbl.overwrite")) {
                addValueChangeListener {
                    formData.overwrite = it.value
                }
            }
            add(uploadButton)
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

    data class EditUploadFormData(
        var cpaFile: InputStream? = null,
        var overwrite: Boolean = false
    )
}