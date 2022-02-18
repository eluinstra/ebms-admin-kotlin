package nl.clockwork.ebms.admin.views

import com.vaadin.flow.server.VaadinServlet
import nl.clockwork.ebms.admin.Cpa
import nl.clockwork.ebms.admin.dao.EbMSDAO
import nl.clockwork.ebms.service.mapping.certificate.api.DefaultApi as CertificateMappingApi
import nl.clockwork.ebms.service.mapping.url.api.DefaultApi as UrlMappingApi
import nl.clockwork.ebms.service.cpa.api.DefaultApi as CpaApi
import org.springframework.web.context.support.WebApplicationContextUtils


interface WithBean {
    val ebMSAdminDAO: EbMSDAO?
        get() = getBean("ebMSAdminDAO", EbMSDAO::class.java)

    val cpaClient: CpaApi?
        get() = getBean("cpaApi", CpaApi::class.java)

    val urlMappingClient: UrlMappingApi?
        get() = getBean("urlMappingApi", UrlMappingApi::class.java)

    val certificateMappingClient: CertificateMappingApi?
        get() = getBean("certificateMappingApi", CertificateMappingApi::class.java)

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
