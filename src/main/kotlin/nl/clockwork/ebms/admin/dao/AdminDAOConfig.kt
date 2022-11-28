package nl.clockwork.ebms.admin.dao

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class AdminDAOConfig() {
    @Bean("ebMSAdminDAO")
    fun ebMSDAO(): EbMSDAO {
        return EbMSDAOImpl()
    }
}