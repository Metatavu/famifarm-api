package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.WastageReason;

/**
 * Translator for wastage reasons
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class WastageReasonsTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA wastage reason object into REST wastage reason object
   * 
   * @param wastageReason JPA wastage reason object
   * @return REST WastageReason
   */
  public WastageReason translateWastageReason(fi.metatavu.famifarm.persistence.model.WastageReason wastageReason) {
    if (wastageReason == null) {
      return null;
    }
    
    WastageReason result = new WastageReason();
    result.setId(wastageReason.getId());
    result.setReason(translatelocalizedValue(wastageReason.getReason()));

    return result;
  }
  
}
