package nl.clockwork.ebms.admin.views

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v10.span
import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.Utils.readVersion


@Route(value = "about", layout = MainLayout::class)
@PageTitle("About")
class AboutView : KComposite() {
    private val root = ui {
        verticalLayout {
            h1(getTranslation("about"))
            accordion {
                setSizeFull()
                versionsPanel()
                propertiesPanel()
                licensePanel()
            }
        }
    }

    private fun @VaadinDsl Accordion.versionsPanel() {
        panel {
            summary {
                button { getTranslation("versions") }
            }
            content {
                verticalLayout {
                    span(readVersion("/META-INF/maven/nl.clockwork.ebms.admin/ebms-admin/pom.properties"))
                    span(readVersion("/META-INF/maven/nl.clockwork.ebms/ebms-core/pom.properties"))
                }
            }
        }
    }

    private fun @VaadinDsl Accordion.propertiesPanel() {
        panel {
            summary {
                button { getTranslation("properties") }
            }
            content {
                pre(
                    """prop1=val1
                    |prop2=val2
                    |prop3=val3""".trimMargin()
                )
            }
        }
    }

    private fun @VaadinDsl Accordion.licensePanel() {
        panel {
            summary {
                button { getTranslation("license") }
            }
            content {
                pre(
                    """Copyright 2021 Ordina
                    |
                    |Licensed under the Apache License, Version 2.0 (the "License");
                    |you may not use this file except in compliance with the License.
                    |You may obtain a copy of the License at
                    |
                    |  http://www.apache.org/licenses/LICENSE-2.0
                    |
                    |Unless required by applicable law or agreed to in writing, software
                    |distributed under the License is distributed on an "AS IS" BASIS,
                    |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                    |See the License for the specific language governing permissions and
                    |limitations under the License.""".trimMargin()
                )
            }
        }
    }
}