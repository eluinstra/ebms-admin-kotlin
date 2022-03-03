package nl.clockwork.ebms.admin

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import com.vaadin.flow.theme.material.Material

@PWA(name = "ebms-admin-vaadin", shortName = "ebms-admin-vaadin", iconPath = "icons/icon.png")
@Theme(themeClass = Lumo::class)
class AppShell : AppShellConfigurator {
}