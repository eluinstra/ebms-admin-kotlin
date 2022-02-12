package nl.clockwork.ebms.admin.dao

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.FactoryBean
import javax.sql.DataSource


abstract class AbstractDAOFactory<T>(
    private val dataSource: DataSource
) : FactoryBean<T> {

    override fun getObject(): T =
        createDAO(dataSource)

    private fun createDAO(dataSource: DataSource): T =
        when(val driverClassName = getDriverClassName(dataSource)) {
            in "db2" -> createDB2DAO()
            in "hsqldb" -> createHSqlDbDAO()
            in "mysql", "mariadb" -> createMySqlDAO()
            in "oracle" -> createOracleDAO()
            in "postgresql" -> createPostgresDAO()
            in "sqlserver" -> createMsSqlDAO()
            else -> {
                throw RuntimeException("Jdbc url $driverClassName not recognized!")
            }
        }

    open fun getDriverClassName(dataSource: DataSource?): String =
        when (dataSource) {
            is HikariDataSource -> dataSource.driverClassName
            else -> throw IllegalStateException("$dataSource not supported")
        }

    abstract override fun getObjectType(): Class<T>?

    override fun isSingleton(): Boolean = true

    abstract fun createHSqlDbDAO(): T

    abstract fun createMySqlDAO(): T

    abstract fun createPostgresDAO(): T

    abstract fun createOracleDAO(): T

    abstract fun createMsSqlDAO(): T

    abstract fun createDB2DAO(): T

    abstract class DefaultDAOFactory<U>(dataSource: DataSource) : AbstractDAOFactory<U>(dataSource) {
        override fun createHSqlDbDAO(): U {
            throw RuntimeException("HSQLDB not supported!")
        }

        override fun createMySqlDAO(): U {
            throw RuntimeException("MySQL not supported!")
        }

        override fun createPostgresDAO(): U {
            throw RuntimeException("Postgres not supported!")
        }

        override fun createOracleDAO(): U {
            throw RuntimeException("Oracle not supported!")
        }

        override fun createMsSqlDAO(): U {
            throw RuntimeException("MSSQL not supported!")
        }

        override fun createDB2DAO(): U {
            throw RuntimeException("DB2 not supported!")
        }
    }

}