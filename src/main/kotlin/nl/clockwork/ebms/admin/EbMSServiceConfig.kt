package nl.clockwork.ebms.admin

import nl.ordina.cpa._2_18.CPAService
import nl.ordina.cpa._2_18.CPAService_Service
import nl.ordina.cpa.certificatemapping._2_18.CertificateMappingService
import nl.ordina.cpa.certificatemapping._2_18.CertificateMappingService_Service
import nl.ordina.cpa.urlmapping._2_18.URLMappingService_Service
import nl.ordina.cpa.urlmapping._2_18.UrlMappingService
import nl.ordina.ebms._2_18.EbMSMessageService
import nl.ordina.ebms._2_18.EbMSMessageService_Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EbMSServiceConfig(
    @Value("\${service.cpas.baseUrl}") val basePath: String
) {
    @Bean("cpaService")
    fun createCpaService() : CPAService = CPAService_Service().cpaPort

    @Bean("urlMappingService")
    fun createUrlMappingService() : UrlMappingService = URLMappingService_Service().urlMappingPort

    @Bean("certificateMappingService")
    fun createCertificateMappingService() : CertificateMappingService = CertificateMappingService_Service().certificateMappingPort

    @Bean("ebMSMessageService")
    fun createEbMSMessageService() : EbMSMessageService = EbMSMessageService_Service().ebMSMessagePort
}