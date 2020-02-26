package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.Packing;

/**
 * Translator for packings
 * 
 * @author simeon
 *
 */

@ApplicationScoped
public class PackingTranslator {
  
  /**
   * Translates JPA batch object into REST batch object
   * 
   * @param packing JPA packing object
   * @return REST packing
   */
  public Packing translate(fi.metatavu.famifarm.persistence.model.Packing packing) {
    if (packing == null) {
      return null;
    }
    
    Packing result = new Packing();
    result.setId(packing.getId());
    result.setPackageSizeId(packing.getPackageSize().getId());
    result.setPackedCount(packing.getPackedCount());
    result.setProductId(packing.getProduct().getId());
    result.setState(packing.getPackingState());
    result.setTime(packing.getTime());
    
    return result;
  }
}
