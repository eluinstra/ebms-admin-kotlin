package nl.clockwork.ebms.admin.views.service.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.components.showSuccessNotification
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import javax.validation.constraints.NotEmpty


@Route(value = "service/cpaUpload", layout = MainLayout::class)
@PageTitle("cpaUpload")
class CPAUploadView : KComposite(), WithBean {
    private lateinit var uploadButton1: Button
    private val formData = CpaUploadFormData()
    private val root = ui {
        verticalLayout {
            h2(getTranslation("cpaUpload"))
            cpaUploadForm()
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                add(uploadButton1)
            }
        }
    }

    private fun HasComponents.cpaUploadForm() : FormLayout {
        val binder = beanValidationBinder<CpaUploadFormData>()
        binder.readBean(formData)
        //TODO use binder
        val memoryBuffer = MemoryBuffer()
        return formLayout {
            uploadButton1 = aButton(getTranslation("cmd.upload"), Icon("lumo", "upload"), 1) {
                isEnabled = false
                addClickListener {
                    formData.cpaFile?.let { cpaClient.insertCPA(String(formData.cpaFile!!), formData.overwrite) }
                    navigateTo(CpasView::class)
                }
            }
            upload(memoryBuffer) {
                text("CpaFile")
                colspan = 2
                addSucceededListener {
                    uploadButton1.isEnabled = true
                    formData.cpaFile = memoryBuffer.inputStream.readAllBytes()
                    cpaClient.insertCPA(String(memoryBuffer.inputStream.readAllBytes()), formData.overwrite)
                    showSuccessNotification("Cpa uploaded")
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

    private fun FormLayout.aButton(
        label: String,
        icon: Icon,
        colspan: Int,
        block: () -> Unit = {}
    ): Button =
        button(label, icon) {
            setColspan(this, colspan)
            text = label
            block()
        }
}

data class CpaUploadFormData(
    @field:NotEmpty
    var cpaFile: ByteArray? = null,
    var overwrite: Boolean = false
)
