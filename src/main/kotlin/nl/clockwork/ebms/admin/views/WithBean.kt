package nl.clockwork.ebms.admin.views

import com.vaadin.flow.server.VaadinServlet
import nl.clockwork.ebms.admin.dao.EbMSDAO
import org.springframework.web.context.support.WebApplicationContextUtils


interface WithBean {
    val ebMSAdminDAO: EbMSDAO?
        get() = getBean("ebMSAdminDAO", EbMSDAO::class.java)

    companion object {
        fun <T> getBean(beanType: Class<T>): T? {
            return WebApplicationContextUtils
                .getWebApplicationContext(VaadinServlet.getCurrent().servletContext)
                ?.getBean(beanType)
        }

        fun <T> getBean(name: String, beanType: Class<T>): T? {
            return WebApplicationContextUtils
                .getWebApplicationContext(VaadinServlet.getCurrent().servletContext)
                ?.getBean(name, beanType)
        }
    }
}
