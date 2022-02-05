package nl.clockwork.ebms.admin

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.Viewport
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import eu.vaadinonkotlin.VaadinOnKotlin
import eu.vaadinonkotlin.rest.VokRest
import eu.vaadinonkotlin.rest.configureToJavalin
import eu.vaadinonkotlin.rest.gsonMapper
import eu.vaadinonkotlin.vokdb.dataSource
import io.javalin.Javalin
import io.javalin.http.JavalinServlet
import org.flywaydb.core.Flyway
import org.h2.Driver
import org.slf4j.LoggerFactory
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebListener
class Bootstrap: ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent?) {
        log.info("Starting up")
        val cfg = HikariConfig().apply {
            driverClassName = Driver::class.java.name
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
            username = "sa"
            password = ""
        }
        VaadinOnKotlin.dataSource = HikariDataSource(cfg)

        // Initializes the VoK framework
        log.info("Initializing VaadinOnKotlin")
        VaadinOnKotlin.init()

        // Makes sure the database is up-to-date
        log.info("Running DB migrations")
        val flyway = Flyway.configure()
            .dataSource(VaadinOnKotlin.dataSource)
            .load()
        flyway.migrate()
        log.info("Initialization complete")
    }

    override fun contextDestroyed(sce: ServletContextEvent?) {
        log.info("Shutting down")
        log.info("Destroying VaadinOnKotlin")
        VaadinOnKotlin.destroy()
        log.info("Shutdown complete")
    }

    companion object {
        private val log = LoggerFactory.getLogger(Bootstrap::class.java)
    }
}

@WebServlet(urlPatterns = ["/rest/*"], name = "JavalinRestServlet", asyncSupported = false)
class JavalinRestServlet : HttpServlet() {
    val javalin: JavalinServlet = Javalin.createStandalone()
            .configureRest()
            .javalinServlet()

    override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
        javalin.service(req, resp)
    }
}

fun Javalin.configureRest(): Javalin {
    gsonMapper(VokRest.gson)
    return this
}

@Viewport(Viewport.DEVICE_DIMENSIONS)
class AppShell: AppShellConfigurator
