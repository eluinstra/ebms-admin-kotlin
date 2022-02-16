package nl.clockwork.ebms.admin.components

import com.github.mvysny.karibudsl.v10.anchor
import com.github.mvysny.karibudsl.v10.button
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.server.StreamResource


fun HasComponents.downloadButton(text: String, resource: StreamResource) : Anchor =
	anchor(resource, "") {
		element.setAttribute("download", true)
		button(text)
	}
