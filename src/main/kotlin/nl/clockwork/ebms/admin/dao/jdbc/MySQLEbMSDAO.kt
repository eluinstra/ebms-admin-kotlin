package nl.clockwork.ebms.admin.dao.jdbc

import nl.clockwork.ebms.admin.EbMSAttachment
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.util.zip.ZipOutputStream

interface MySQLEbMSDAO : AbstractEbMSAttachmentDAO {
}