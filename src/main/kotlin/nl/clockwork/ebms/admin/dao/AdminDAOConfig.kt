package nl.clockwork.ebms.admin.dao

import nl.clockwork.ebms.admin.dao.exposed.EbMSDAOFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class AdminDAOConfig(val dataSource: DataSource) {
    @Bean("ebMSAdminDAO")
    fun ebMSDAO(): EbMSDAO? {
        return EbMSDAOFactory(dataSource).getObject()
    }
}