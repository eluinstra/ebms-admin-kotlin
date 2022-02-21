package nl.clockwork.ebms.admin.views.service.cpa

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import nl.clockwork.ebms.service.cpa.api.DefaultApi
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget


class CpaApiImpl(
    private val basePath: String
) : DefaultApi {
    override fun deleteCPA(cpaId: String?) {
        createTarget()
            .path("$cpaId")
            .request()
            .get()
    }

    private fun createTarget(): WebTarget =
        ClientBuilder.newClient()
            .register(JacksonJsonProvider())
            .target(basePath)

    override fun getCPA(cpaId: String?): String =
        createTarget()
            .path("$cpaId")
            .request()
            .get(String::class.java)

    override fun getCPAIds(): MutableList<String> =
        createTarget()
            .request()
            .get(MutableList::class.java) as MutableList<String>

    override fun getResource(resource: String?) {
        TODO("Not yet implemented")
    }

    override fun insertCPA(overwrite: Boolean?, body: String?): String =
        createTarget()
            .queryParam("overwrite", overwrite)
            .request()
            .post(Entity.entity(body, "application/json"))
            .readEntity(String::class.java)

    override fun validateCPA(body: String?) {
        createTarget()
            .request()
            .post(Entity.entity(body, "application/json"))
//            .readEntity(Unit::class.java)
    }
}