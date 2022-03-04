package nl.clockwork.ebms.admin.components

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.anchor
import com.github.mvysny.karibudsl.v10.button
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.VaadinSession


fun HasComponents.downloadButton(text: String, resource: StreamResource) : Anchor =
	anchor(resource, "") {
		element.setAttribute("download", true)
		button(text, Icon("lumo", "download"))
	}

fun downloadButton1(text: String, resource: StreamResource, block: (@VaadinDsl Button).() -> Unit = {}) : Anchor =
	Anchor("").apply {
		setHref(resource)
		element.setAttribute("download", true)
		add(Button(text, Icon("lumo", "download")).apply {
			block()
		})
	}

fun downloadButton2(text: String, resource: StreamResource) : Button =
	Button(text, Icon("lumo", "download")) {
		val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resource)
		UI.getCurrent().page.setLocation(registration.resourceUri)
	}
