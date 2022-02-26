package nl.clockwork.ebms.admin.views.service.cpa

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import nl.clockwork.ebms.service.mapping.certificate.api.DefaultApi
import nl.clockwork.ebms.service.mapping.certificate.model.CertificateMapping
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType

class CertificateMappingApiImpl(
    private val basePath: String
) : DefaultApi {
    override fun deleteCertificateMapping(cpaId: String?, body: String?) {
        createTarget(basePath)
            .queryParam("$cpaId")
            .request()
            //TODO: fix
            .delete()
//            .post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE))
    }

    private fun createTarget(basePath: String): WebTarget =
        ClientBuilder.newClient()
            .register(JacksonJsonProvider())
            .target(basePath)

    override fun getCertificateMappingsRest(): MutableList<CertificateMapping> =
        createTarget(basePath)
            .request()
            .get(MutableList::class.java) as MutableList<CertificateMapping>

    override fun getResource(resource: String?) {
        TODO("Not yet implemented")
    }

    override fun setCertificateMapping(certificateMapping: CertificateMapping?) {
        createTarget(basePath)
            .request()
            .post(Entity.entity(certificateMapping, MediaType.APPLICATION_JSON_TYPE))
    }
}