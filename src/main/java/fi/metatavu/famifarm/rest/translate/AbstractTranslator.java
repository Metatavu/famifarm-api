package fi.metatavu.famifarm.rest.translate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.rest.model.LocalizedValue;

/**
 * Abstract translator class
 * 
 * @author Antti Leppä
 */
public abstract class AbstractTranslator {

  @Inject
  private LocalizedValueController localizedValueController;
  
  /**
   * Translates JPA localized entry into list of REST localized values
   * 
   * @param entry JPA localized entry
   * @return list of REST localized values
   */
  protected List<LocalizedValue> translatelocalizedValue(LocalizedEntry entry) {
    List<LocalizedValue> result = new ArrayList<>();
    
    localizedValueController.listLocalizedValues(entry).stream().map(localizedValue -> {
      LocalizedValue restItem = new LocalizedValue();
      
      restItem.setLanguage(localizedValue.getLocale().getLanguage());
      restItem.setValue(localizedValue.getValue());
      
      return restItem;
    }).forEach(result::add);
    
    return result;
  }
  
}
