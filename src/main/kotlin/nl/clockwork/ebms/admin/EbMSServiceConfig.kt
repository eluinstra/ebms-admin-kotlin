package nl.clockwork.ebms.admin

import nl.clockwork.ebms.service.mapping.url.api.DefaultApi as UrlMappingApi
import nl.clockwork.ebms.service.mapping.certificate.api.DefaultApi as CertificateMappingApi
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
        CpaApi(basePath)

    @Bean("urlMappingApi")
    fun createUrlMappingApi(): UrlMappingApi =
        UrlMappingApi(basePath)

    @Bean("certificateMappingApi")
    fun createCertificateMappingApi(): CertificateMappingApi =
        CertificateMappingApi(basePath)
}