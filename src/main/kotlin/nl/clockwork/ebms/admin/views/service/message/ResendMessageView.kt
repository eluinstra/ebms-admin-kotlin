package nl.clockwork.ebms.admin.views.service.message

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
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
import javax.xml.ws.Holder

@Route(value = "service/message/resendMessage", layout = MainLayout::class)
@PageTitle("Resend Message")
class ResendMessageView : KComposite(), WithBean {
    private val binder = beanValidationBinder<ResendMessageFormData>()

    private val root = ui {
        verticalLayout {
            h2(getTranslation("messageResend"))
            resendMessageForm()
            horizontalLayout {
                backButton(getTranslation("cmd.back"))
                resendButton(getTranslation("cmd.resend"))
            }
        }
    }

    private fun HasComponents.resendMessageForm() {
        formLayout {
            aTextField(getTranslation("lbl.messageId"), 2) {
                bind(binder).bind(ResendMessageFormData::messageId)
            }
        }
    }

    private fun @VaadinDsl HorizontalLayout.resendButton(text: String?) {
        button(text) {
            onLeftClick {
                val formData = ResendMessageFormData()
                if (binder.writeBeanIfValid(formData)) {
                    try {
                        val messageId = ebMSMessageClient.resendMessage(
                            Holder<String?>().apply {
                                formData.messageId
                            }
                        )
                        showSuccessNotification(getTranslation("resendMessage.ok", messageId))
                    } catch (e: EbMSMessageServiceException) {
                        logger.error("", e)
                        showErrorNotification(e.message)
                    }
                } else {
                    showErrorNotification("Invalid data")
                }
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ResendMessage::class.java)
    }
}

data class ResendMessageFormData(
    @field:NotEmpty
    var messageId: String? = null
)