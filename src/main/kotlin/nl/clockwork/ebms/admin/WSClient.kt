package nl.clockwork.ebms.admin

import nl.ordina.cpa._2_18.CPAService
import nl.ordina.cpa._2_18.CPAService_Service

object WSClient {
    @JvmStatic
    fun main(args: Array<String>) {
        val service = CPAService_Service()
        val port = service.cpaPort
        println(port.cpaIds)
    }
}