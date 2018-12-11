package fi.metatavu.famifarm.localization;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.LocalizedEntryDAO;
import fi.metatavu.famifarm.persistence.dao.LocalizedValueDAO;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.LocalizedValue;

/**
 * Controller for localized values
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class LocalizedValueController {
  
  @Inject
  private LocalizedEntryDAO localizedEntryDAO;

  @Inject
  private LocalizedValueDAO localizedValueDAO;
  
  /**
   * Creates new localized entry
   * 
   * @return new localized entry
   */
  public LocalizedEntry createEntry() {
    return localizedEntryDAO.create(UUID.randomUUID());
  }
  
  /**
   * Sets localized entry values
   * 
   * @param entry entry
   * @param locale locale
   * @param values values
   * @return localized entry
   */
  public LocalizedEntry setEntryValues(LocalizedEntry entry, Map<Locale, String> values) {
    localizedValueDAO.listByEntry(entry).stream().forEach(localizedValueDAO::delete);

    for (Entry<Locale, String> localeEntry : values.entrySet()) {
      Locale locale = localeEntry.getKey();
      localizedValueDAO.create(UUID.randomUUID(), entry, locale, localeEntry.getValue());
    }
    
    return entry;
  } 
  
  /**
   * Returns value for entry, locale and type
   * 
   * @param entry entry
   * @param locale locale
   * @param type type
   * @return value or null if not found
   */
  public String getValue(LocalizedEntry entry, Locale locale) {
    if (entry == null) {
      return null;
    }
    
    LocalizedValue localizedValue = localizedValueDAO.findByEntryAndLocale(entry, locale);
    return localizedValue != null ? localizedValue.getValue() : null;
  }
  
  /**
   * Lists localized values by entry
   * 
   * @param entry entry
   * @return list of localized values
   */
  public List<LocalizedValue> listLocalizedValues(LocalizedEntry entry) {
    return localizedValueDAO.listByEntry(entry);
  }

  /**
   * Deletes localized entry
   * 
   * @param entry entry
   */
  public void deleteEntry(LocalizedEntry entry) {
    localizedValueDAO.listByEntry(entry).stream().forEach(localizedValueDAO::delete);
    localizedEntryDAO.delete(entry);
  }
  
}
