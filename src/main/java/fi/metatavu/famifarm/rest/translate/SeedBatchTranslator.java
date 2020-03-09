package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.SeedBatch;

/**
 * Translator for seed batch
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class SeedBatchTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA seed batch object into REST seed object
   * 
   * @param seed JPA seed object
   * @return REST seed
   */
  public SeedBatch translateSeedBatch(fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch) {
    if (seedBatch == null) {
      return null;
    }

    SeedBatch result = new SeedBatch();
    result.setId(seedBatch.getId());
    result.setCode(seedBatch.getCode());
    result.setActive(seedBatch.isActive());
    if (seedBatch.getSeed() != null) {
      result.setSeedId(seedBatch.getSeed().getId());
    }
    
    result.setTime(seedBatch.getTime());
    
    return result;
  }
  
}
