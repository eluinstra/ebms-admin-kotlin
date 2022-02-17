package nl.clockwork.ebms.admin.views

import nl.clockwork.ebms.EbMSMessageStatus
import org.apache.commons.lang3.StringUtils
import java.net.URLConnection
import java.util.*
import java.util.function.Function


object Utils {
    fun getContentType(pathInfo: String?): String {
        val result = URLConnection.guessContentTypeFromName(pathInfo)
        //val result = new MimetypesFileTypeMap().getContentType(pathInfo);
        //val result = URLConnection.getFileNameMap().getContentTypeFor(pathInfo);
        return result ?: "application/octet-stream"
    }

    fun getFileExtension(contentType: String): String =
        if (StringUtils.isNotEmpty(contentType))
            "." + (getExtension(contentType))
        else
            ""

    private fun getExtension(contentType: String) =
        if (contentType.contains("text"))
            "txt"
        else
            contentType.split("/").toTypedArray()[1]

    fun getTableCellCssClass(ebMSMessageStatus: EbMSMessageStatus?): String =
        ebMSMessageStatus?.let {
            Status.getCssClass(it) { obj: Status -> obj.cellClass }
        } ?: ""

    fun getTableRowCssClass(ebMSMessageStatus: EbMSMessageStatus?): String =
        ebMSMessageStatus?.let {
            Status.getCssClass(it) { obj: Status -> obj.rowClass }
        } ?: ""

    fun getErrorList(content: String): String {
        return content.replaceFirst("(?ms)^.*(<[^<>]*:?ErrorList.*ErrorList>).*$".toRegex(), "$1")
    }

    private enum class Status(
        val statuses: EnumSet<EbMSMessageStatus>,
        val rowClass: String,
        val cellClass: String
    ) {
        SUCCESS(
            EnumSet.of(EbMSMessageStatus.PROCESSED, EbMSMessageStatus.FORWARDED, EbMSMessageStatus.DELIVERED),
            "success",
            "text-success"
        ),
        WARNING(EnumSet.of(EbMSMessageStatus.RECEIVED, EbMSMessageStatus.CREATED), "warning", "text-warning"), DANGER(
            EnumSet.of(
                EbMSMessageStatus.UNAUTHORIZED,
                EbMSMessageStatus.NOT_RECOGNIZED,
                EbMSMessageStatus.FAILED,
                EbMSMessageStatus.DELIVERY_FAILED,
                EbMSMessageStatus.EXPIRED
            ),
            "danger",
            "text-danger"
        );

        companion object {
            var getCssClass =
                { status: EbMSMessageStatus, getClass: Function<Status, String> ->
                    Arrays.stream(values())
                        .filter { it.statuses!!.contains(status) }
                        .map { getClass.apply(it) }
                        .findFirst()
                        .orElse(null)
                }
        }
    }
}
