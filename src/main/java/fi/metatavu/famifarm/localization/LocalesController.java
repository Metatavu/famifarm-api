package fi.metatavu.famifarm.localization;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;

/**
 * Locales controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class LocalesController {

  /**
   * Returns localized string for given locale and key
   * 
   * @param locale locale
   * @param key key
   * @return localized string
   */
  public String getString(Locale locale, String key) {
    ResourceBundle resourceBundle = getLocaleBundle(locale);
    return resourceBundle.getString(key);
  }
  
  /**
   * Returns localized string for given locale, key and params
   * 
   * @param locale locale
   * @param key key
   * @param params params
   * @return localized string
   */
  public String getString(Locale locale, String key, Object... params) {
    return (new MessageFormat(getString(locale, key), locale)).format(params);
  }
  
  /**
   * Returns resource bundle for given locale
   * 
   * @param locale locale
   * @return resource bundle
   */
  private ResourceBundle getLocaleBundle(Locale locale) {
    return ResourceBundle.getBundle("fi.metatavu.famifarm.server.locales", locale);
  }
  
}
