package nl.clockwork.ebms.admin.dao

import nl.clockwork.ebms.dao.AbstractDAOFactory.DefaultDAOFactory
import nl.clockwork.ebms.transaction.TransactionManagerConfig.TransactionManagerType
import javax.sql.DataSource

class EbMSDAOFactory(
    private val dataSource: DataSource
) : DefaultDAOFactory<EbMSDAO>(dataSource) {
    override fun getObjectType(): Class<EbMSDAO> {
        return EbMSDAO::class.java
    }

    override fun createDB2DAO(): EbMSDAO {
        return DB2EbMSDAO()
    }

    override fun createH2DAO(): EbMSDAO {
        return H2EbMSDAO()
    }

    override fun createHSQLDBDAO(): EbMSDAO {
        return HSQLDBEbMSDAO()
    }

    override fun createMSSQLDAO(): EbMSDAO {
        return MSSQLEbMSDAO()
    }

    override fun createMySQLDAO(): EbMSDAO {
        return MySQLEbMSDAO()
    }

    override fun createOracleDAO(): EbMSDAO {
        return OracleEbMSDAO()
    }

    override fun createPostgreSQLDAO(): EbMSDAO {
        return PostgreSQLEbMSDAO()
    }
}