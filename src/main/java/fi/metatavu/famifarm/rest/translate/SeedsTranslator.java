package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.Seed;

/**
 * Translator for seeds
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SeedsTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA seed object into REST seed object
   * 
   * @param seed JPA seed object
   * @return REST seed
   */
  public Seed translateSeed(fi.metatavu.famifarm.persistence.model.Seed seed) {
    if (seed == null) {
      return null;
    }
    
    Seed result = new Seed();
    result.setId(seed.getId());
    result.setName(translatelocalizedValue(seed.getName()));

    return result;
  }
  
}
