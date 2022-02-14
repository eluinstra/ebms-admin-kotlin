package nl.clockwork.ebms.admin.views

import com.vaadin.flow.server.VaadinServlet
import nl.clockwork.ebms.admin.dao.EbMSDAO
import org.springframework.web.context.support.WebApplicationContextUtils


interface WithBean {
    val ebMSAdminDAO: EbMSDAO?
        get() = get("ebMSAdminDAO", EbMSDAO::class.java)

    operator fun <T> get(beanType: Class<T>): T? {
        return WebApplicationContextUtils
            .getWebApplicationContext(VaadinServlet.getCurrent().servletContext)
            ?.getBean(beanType)
    }

    operator fun <T> get(name: String, beanType: Class<T>): T? {
        return WebApplicationContextUtils
            .getWebApplicationContext(VaadinServlet.getCurrent().servletContext)
            ?.getBean(name, beanType)
    }
}
