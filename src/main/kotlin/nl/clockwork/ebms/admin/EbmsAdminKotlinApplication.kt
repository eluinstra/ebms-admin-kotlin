package nl.clockwork.ebms.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication(scanBasePackageClasses = [
	TranslationProvider::class])
@EnableTransactionManagement
class EbmsAdminKotlinApplication

fun main(args: Array<String>) {
	runApplication<EbmsAdminKotlinApplication>(*args)
}
