package nl.clockwork.ebms.admin

import java.io.PrintWriter
import java.io.Writer
import java.util.*


object Utils {
    fun readVersion(propertiesFile: String): String =
        readProperties(propertiesFile)?.run {
            "${getProperty("artifactId")}-${getProperty("version")}"
        } ?: run {
            "unknown"
        }

    fun readProperties(propertiesFile: String): Properties? =
        Utils::class.java.getResourceAsStream(propertiesFile)?.let {
            val properties = Properties()
            properties.load(it)
            properties
        }

    fun writeProperties(properties: Properties, writer: Writer) =
        properties.list(PrintWriter(writer, true))

    fun writeProperties(properties: Map<String, String>, writer: Writer) {
        fun safeValue(key: String, value: String) =
            if (key.matches(Regex(".*(password|pwd).*")))
                value.replace(".".toRegex(), "*")
            else
                value

        fun writeKeyValue(key: String, value: String) =
//            writer.write("$key = ${safeValue(key, value)}\n")
            with(writer) {
                write(key)
                write(" = ")
                write(safeValue(key, value))
                write("\n")
            }

        TreeSet(properties.keys).forEach {
            writeKeyValue(it, properties[it] ?: "")
        }
    }

//    fun <T> toList(list: List<T>?): List<T> = list.orEmpty()

    fun getHost(host: String): String =
        if ("0.0.0.0" == host || "::" == host) "localhost" else host
}
