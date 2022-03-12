package nl.clockwork.ebms.admin.views.cpa

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.*
import nl.clockwork.ebms.admin.Cpa
import nl.clockwork.ebms.admin.components.backButton
import nl.clockwork.ebms.admin.components.closeButton
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean


fun cpaDialog(cpa: Cpa) : Dialog =
    Dialog().apply {
        width = "80%"
        formLayout {
            setResponsiveSteps(FormLayout.ResponsiveStep("0", 2))
            textField(getTranslation("lbl.cpaId")) {
                colspan = 2
                isReadOnly = true
                value = cpa.cpaId
            }
            textArea(getTranslation("lbl.cpa")) {
                colspan = 2
                maxHeight = "800px"
                isReadOnly = true
                value = cpa.cpa
            }
        }
        closeButton(getTranslation("cmd.close")) {
            addClickListener { _ -> this@apply.close() }
        }
    }