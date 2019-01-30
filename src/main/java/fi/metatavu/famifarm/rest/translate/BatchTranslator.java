package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.Batch;

/**
 * Translator for package size
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class BatchTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA batch object into REST batch object
   * 
   * @param seed JPA seed object
   * @return REST seed
   */
  public Batch translateBatch(fi.metatavu.famifarm.persistence.model.Batch batch) {
    if (batch == null) {
      return null;
    }
    
    Batch result = new Batch();
    result.setId(batch.getId());
    
    if (batch.getProduct() != null) {
      result.setProductId(batch.getProduct().getId());
    }

    return result;
  }
  
}
