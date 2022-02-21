package nl.clockwork.ebms.admin

import nl.clockwork.ebms.admin.views.service.cpa.CpaApiImpl
import nl.clockwork.ebms.service.cpa.api.DefaultApi as CpaApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EbMSServiceConfig(
    @Value("\${service.cpas.baseUrl}") val basePath: String
) {
    @Bean("cpaApi")
    fun createCpaApi(): CpaApi =
        CpaApiImpl(basePath)

//    @Bean("urlMappingApi")
//    fun createUrlMappingApi(): UrlMappingApi =
//        UrlMappingApiImpl(basePath)
//
//    @Bean("certificateMappingApi")
//    fun createCertificateMappingApi(): CertificateMappingApi =
//        CertificateMappingApiImpl(basePath)
}