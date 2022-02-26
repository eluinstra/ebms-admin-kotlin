package nl.clockwork.ebms.admin.views

import com.vaadin.flow.server.VaadinServlet
import nl.clockwork.ebms.admin.Cpa
import nl.clockwork.ebms.admin.dao.EbMSDAO
import nl.ordina.cpa._2_18.CPAService
import nl.ordina.cpa.certificatemapping._2_18.CertificateMappingService
import nl.ordina.cpa.urlmapping._2_18.UrlMappingService
import org.springframework.web.context.support.WebApplicationContextUtils


interface WithBean {
    val ebMSAdminDAO: EbMSDAO
        get() = getBean("ebMSAdminDAO", EbMSDAO::class.java)

    val cpaClient: CPAService
        get() = getBean("cpaService", CPAService::class.java)

    val urlMappingClient: UrlMappingService
        get() = getBean("urlMappingApi", UrlMappingService::class.java)

    val certificateMappingClient: CertificateMappingService
        get() = getBean("certificateMappingApi", CertificateMappingService::class.java)

    companion object {
        fun <T> getBean(beanType: Class<T>): T? =
            WebApplicationContextUtils
                .getWebApplicationContext(VaadinServlet.getCurrent().servletContext)
                ?.getBean(beanType)

        fun <T> getBean(name: String, beanType: Class<T>): T =
            WebApplicationContextUtils
                .getWebApplicationContext(VaadinServlet.getCurrent().servletContext)
                ?.getBean(name, beanType) ?: throw IllegalStateException("Bean $name not found")
    }
}
