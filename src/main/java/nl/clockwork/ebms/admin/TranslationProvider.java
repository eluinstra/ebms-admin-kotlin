package nl.clockwork.ebms.admin;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
public class TranslationProvider implements I18NProvider {
    private final Logger log = LoggerFactory.getLogger(TranslationProvider.class);
    private final List<Locale> locales = List.of(new Locale("en", "US"));

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        try {
            if (key == null) {
                log.warn("key is null");
                return "";
            }
            String value = getValue(key, locale);
            return formatValue(value, params);
        } catch (final MissingResourceException e) {
            log.warn("", e);
            return "!" + locale.getLanguage() + ": " + key;
        }
    }

    private String getValue(String key, Locale locale) throws MissingResourceException {
        ResourceBundle bundle = ResourceBundle.getBundle("translate", locale);
        return bundle.getString(key);
    }

    private String formatValue(final String value, Object... params) {
        if (params.length == 0)
            return value;
        else
            return MessageFormat.format(value, params);
    }

}
