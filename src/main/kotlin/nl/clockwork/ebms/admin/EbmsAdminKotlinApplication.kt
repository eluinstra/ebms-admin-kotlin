package nl.clockwork.ebms.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import nl.clockwork.ebms.admin.TranslationProvider

@SpringBootApplication(scanBasePackageClasses = [
	TranslationProvider::class])
class EbmsAdminKotlinApplication

fun main(args: Array<String>) {
	runApplication<EbmsAdminKotlinApplication>(*args)
}
