package fi.metatavu.famifarm.rest.translate;

import fi.metatavu.famifarm.persistence.dao.PackingBasketDAO;
import fi.metatavu.famifarm.persistence.dao.PackingVerificationWeightingDAO;
import fi.metatavu.famifarm.persistence.model.PackingBasket;
import fi.metatavu.famifarm.persistence.model.PackingVerificationWeighting;
import fi.metatavu.famifarm.rest.model.Packing;
import fi.metatavu.famifarm.rest.model.PackingUsedBasket;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Translator for packings
 * 
 * @author simeon
 *
 */

@ApplicationScoped
public class PackingTranslator {

  @Inject
  PackingVerificationWeightingDAO packingVerificationWeightingDAO;

  @Inject
  PackingBasketDAO packingBasketDAO;

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

    result.setState(packing.getPackingState());
    result.setTime(packing.getTime());
    result.setType(packing.getType());

    List<PackingBasket> packingBaskets = packingBasketDAO.listByPacking(packing);
    if (packingBaskets != null) {
      result.setBasketsUsed(
        packingBaskets.stream().map(packingBasket -> {
            PackingUsedBasket translatedBasket = new PackingUsedBasket();
            translatedBasket.setBasketCount(packingBasket.getCount());
            translatedBasket.setProductId(packingBasket.getProduct().getId());
            return translatedBasket;
          }
        ).collect(Collectors.toList())
      );
    }

    List<PackingVerificationWeighting> weightings = packingVerificationWeightingDAO.listByPacking(packing);
    if (weightings != null) {
      result.setVerificationWeightings(
        weightings.stream().map(weighting -> {
            fi.metatavu.famifarm.rest.model.PackingVerificationWeighing translatedWeighting = new fi.metatavu.famifarm.rest.model.PackingVerificationWeighing();
            translatedWeighting.setTime(weighting.getTime());
            translatedWeighting.setWeight(weighting.getWeight());
            return translatedWeighting;
          }
        ).collect(Collectors.toList())
      );
    }

    return result;
  }
}
