package nl.clockwork.ebms.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EbmsAdminKotlinApplication

fun main(args: Array<String>) {
	runApplication<EbmsAdminKotlinApplication>(*args)
}
