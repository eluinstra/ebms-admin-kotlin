package nl.clockwork.ebms.admin.views.service.cpa

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import nl.clockwork.ebms.service.cpa.api.DefaultApi
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType


class CpaApiImpl(
    private val basePath: String
) : DefaultApi {
    override fun deleteCPA(cpaId: String?) {
        createTarget(basePath)
            .path("$cpaId")
            .request()
            .get()
    }

    private fun createTarget(basePath: String): WebTarget =
        ClientBuilder.newClient()
            .register(JacksonJsonProvider())
            .target(basePath)

    override fun getCPA(cpaId: String?): String =
        createTarget(basePath)
            .path("$cpaId")
            .request()
            .get(String::class.java)

    override fun getCPAIds(): MutableList<String> =
        createTarget(basePath)
            .request()
            .get(MutableList::class.java) as MutableList<String>

    override fun getResource(resource: String?) {
        TODO("Not yet implemented")
    }

    override fun insertCPA(overwrite: Boolean?, body: String?): String =
        createTarget(basePath)
            .queryParam("overwrite", overwrite)
            .request()
            .post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(String::class.java)

    override fun validateCPA(body: String?) {
        createTarget(basePath)
            .request()
            .post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE))
//            .readEntity(Unit::class.java)
    }
}