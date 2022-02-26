package nl.clockwork.ebms.admin.views.service.cpa

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import nl.clockwork.ebms.service.mapping.url.api.DefaultApi
import nl.clockwork.ebms.service.mapping.url.model.URLMapping
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType

class UrlMappingApiImpl(
    private val basePath: String
) : DefaultApi {
    override fun deleteURLMapping(id: String?) {
        createTarget(basePath)
            .path("$id")
            .request()
            .get()
    }

    private fun createTarget(basePath: String): WebTarget =
        ClientBuilder.newClient()
            .register(JacksonJsonProvider())
            .target(basePath)

    override fun getResource(resource: String?) {
        TODO("Not yet implemented")
    }

    override fun getURLMappings(): MutableList<URLMapping> =
        createTarget(basePath)
            .request()
            .get(MutableList::class.java) as MutableList<URLMapping>

    override fun setURLMapping(urLMapping: URLMapping?) {
        createTarget(basePath)
            .request()
            .post(Entity.entity(urLMapping, MediaType.APPLICATION_JSON_TYPE))
    }
}