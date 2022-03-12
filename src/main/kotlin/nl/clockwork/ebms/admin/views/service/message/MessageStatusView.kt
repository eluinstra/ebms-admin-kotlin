package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.components.aTextField
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.components.showErrorNotification
import nl.clockwork.ebms.admin.components.showSuccessNotification
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import nl.ordina.ebms._2.ResendMessage
import nl.ordina.ebms._2_18.EbMSMessageServiceException
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotEmpty

@Route(value = "service/message/messageStatus", layout = MainLayout::class)
@PageTitle("Message Status")
class MessageStatusView : KComposite(), WithBean {
    private val binder = beanValidationBinder<MessageStatusFormData>()

    private val root = ui {
        verticalLayout {
            h2(getTranslation("messageStatus"))
            messageStatusForm()
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                messageStatusButton(getTranslation("cmd.check"))
            }
        }
    }

    private fun HasComponents.messageStatusForm() {
        formLayout {
            aTextField(getTranslation("lbl.messageId"), 2) {
                bind(binder).bind(MessageStatusFormData::messageId)
            }
        }
    }

    private fun @VaadinDsl HorizontalLayout.messageStatusButton(text: String?) {
        button(text, Icon("lumo", "checkmark")) {
            onLeftClick {
                val formData = MessageStatusFormData()
                if (binder.writeBeanIfValid(formData)) {
                    try {
                        val status = ebMSMessageClient.getMessageStatus(formData.messageId)
                        showSuccessNotification(getTranslation("getMessageStatus.ok", status.status))
                    } catch (e: EbMSMessageServiceException) {
                        logger.error("", e)
                        showErrorNotification(e.message)
                    }
                }
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ResendMessage::class.java)
    }
}

data class MessageStatusFormData(
    @field:NotEmpty
    var messageId: String? = null
)