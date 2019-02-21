package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.Pest;

/**
 * Translator for pests
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PestsTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA pest object into REST pest object
   * 
   * @param pest JPA pest object
   * @return REST Pest
   */
  public Pest translatePest(fi.metatavu.famifarm.persistence.model.Pest pest) {
    if (pest == null) {
      return null;
    }
    
    Pest result = new Pest();
    result.setId(pest.getId());
    result.setName(translatelocalizedValue(pest.getName()));

    return result;
  }
  
}
