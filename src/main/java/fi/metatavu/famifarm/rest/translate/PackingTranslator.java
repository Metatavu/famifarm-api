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

    if (packing.getPackageSize() != null) {
      result.setPackageSizeId(packing.getPackageSize().getId());
    }

    result.setPackedCount(packing.getPackedCount());

    if (packing.getProduct() != null) {
      result.setProductId(packing.getProduct().getId());
    }

    if (packing.getCampaign() != null) {
      result.setCampaignId(packing.getCampaign().getId());
    }

    result.setRemovedFromStorage(packing.getRemovedFromStorage());
    result.setState(packing.getPackingState());
    result.setTime(packing.getTime());
    result.setType(packing.getType());

    return result;
  }
}
