package nl.clockwork.ebms.admin

import com.vaadin.flow.i18n.I18NProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.text.MessageFormat
import java.util.*


@Component
class TranslationProvider : I18NProvider {
	private val locales = Collections.unmodifiableList(listOf(LOCALE_EN))
	override fun getProvidedLocales(): List<Locale> {
		return locales
	}

	override fun getTranslation(key: String, locale: Locale, vararg params: Any): String {
		return try {
			val value = getValue(key, locale)
			formatValue(value, params)
		} catch (e: MissingResourceException) {
			logger.warn("", e)
			"!${locale.language}: $key"
		}
	}

	private fun getValue(key: String, locale: Locale): String {
		val bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale)
		return bundle.getString(key)
	}

	private fun formatValue(value: String, vararg params: Any): String {
		return if (params.isEmpty()) value else MessageFormat.format(value, *params)
	}

	companion object {
		private val logger: Logger = LoggerFactory.getLogger(TranslationProvider::class.java)
		const val BUNDLE_PREFIX = "translate"
		val LOCALE_EN = Locale("en", "US")
	}
}
