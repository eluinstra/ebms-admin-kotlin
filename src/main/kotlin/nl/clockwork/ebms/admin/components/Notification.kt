package nl.clockwork.ebms.admin.components

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment
import com.vaadin.flow.component.orderedlayout.HorizontalLayout


fun showNotification(message: String?, autoCloseable: Boolean = true) {
    showNotification(
        text = message ?: "???",
        variant = NotificationVariant.LUMO_PRIMARY,
        autoCloseable)
}

fun showSuccessNotification(message: String?, autoCloseable: Boolean = true) {
    showNotification(
        text = message ?: "???",
        variant = NotificationVariant.LUMO_SUCCESS,
        autoCloseable)
}

fun showErrorNotification(message: String?, autoCloseable: Boolean = false) {
    showNotification(
        text = message ?: "An unknown error occurred",
        variant = NotificationVariant.LUMO_ERROR,
        autoCloseable)
}

private fun showNotification(text: String, variant: NotificationVariant, autoCloseable: Boolean) {
    if (autoCloseable) {
        showSimpleNotification(text, variant)
    } else {
        showNotification(text, variant)
    }
}

private fun showSimpleNotification(message: String, variant: NotificationVariant) {
    Notification.show(message).apply {
        addThemeVariants(variant)
    }
}

private fun showNotification(message: String?, variant: NotificationVariant) {
    fun horizontalLayout(text: Div, closeButton: Button): HorizontalLayout =
        HorizontalLayout(text, closeButton).apply {
            alignItems = Alignment.CENTER
        }

    fun closeButton(notification: Notification): Button =
        Button(Icon("lumo", "cross")).apply {
            addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
            element.setAttribute("aria-label", "Close")
            addClickListener {
                notification.close()
            }
        }

    Notification().apply {
        addThemeVariants(variant)
        add(horizontalLayout(Div(Text(message)), closeButton(this)))
        open()
    }
}
