package fi.metatavu.famifarm.packings;

import fi.metatavu.famifarm.persistence.dao.PackingBasketDAO;
import fi.metatavu.famifarm.persistence.dao.PackingDAO;
import fi.metatavu.famifarm.persistence.dao.PackingVerificationWeightingDAO;
import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.Facility;
import fi.metatavu.famifarm.rest.model.PackingState;
import fi.metatavu.famifarm.rest.model.PackingType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller for packing
 *
 * @author simeon
 */

@ApplicationScoped
public class PackingController {

  @Inject
  PackingDAO packingDAO;

  @Inject
  PackingBasketDAO packingBasketDAO;

  @Inject
  PackingVerificationWeightingDAO packingVerificationWeightingDAO;

  /**
   * Creates a new packing
   *
   * @param creatorId      creator id
   * @param facility       required facility param
   * @param product        product
   * @param packageSize    package size
   * @param packingBody    packing body
   * @param packingBaskets packing baskets (incomplete objects)
   * @param campaign       campaign
   * @param type           packing type
   * @return packing
   */
  public Packing create(
    UUID creatorId,
    Facility facility,
    Product product,
    PackageSize packageSize,
    fi.metatavu.famifarm.rest.model.Packing packingBody,
    List<PackingBasket> packingBaskets,
    Campaign campaign,
    PackingType type
  ) {
    Packing createdPacking = packingDAO.create(
      creatorId,
      facility,
      product,
      UUID.randomUUID(),
      packageSize,
      packingBody.getPackedCount(),
      packingBody.getState(),
      packingBody.getTime(),
      campaign,
      type,
      packingBody.getStartTime(),
      packingBody.getEndTime(),
      packingBody.getAdditionalInformation()
    );

    if (packingBaskets != null) {
      for (PackingBasket packingBasket : packingBaskets) {
        packingBasket.setPacking(createdPacking);
        packingBasketDAO.persist(packingBasket);
      }
    }

    if (packingBody.getVerificationWeightings() != null) {
      for (fi.metatavu.famifarm.rest.model.PackingVerificationWeighing packingVerificationWeighing : packingBody.getVerificationWeightings()) {
        packingVerificationWeightingDAO.create(UUID.randomUUID(), createdPacking, packingVerificationWeighing.getWeight(), packingVerificationWeighing.getTime());
      }
    }
    return createdPacking;
  }

  /**
   * Returns packing by id
   *
   * @param packingId id
   * @return packing or null if not found
   */
  public Packing findById(UUID packingId) {
    return packingDAO.findById(packingId);
  }

  /**
   * Return list of packings
   *
   * @param firstResult
   * @param maxResults
   * @param facility      non nullable facility
   * @param product
   * @param campaign
   * @param state
   * @param createdBefore
   * @param createdAfter
   * @return packings
   */
  public List<Packing> listPackings(Integer firstResult, Integer maxResults, Facility facility, Product product, Campaign campaign, PackingState state, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    return packingDAO.list(firstResult, maxResults, facility, product, campaign, state, createdBefore, createdAfter);
  }

  /**
   * Updates packing
   *
   * @param packing             packing
   * @param packageSize         package size
   * @param packingBody         packing body
   * @param validPackingBaskets valid packing baskets
   * @param modifier            modifier
   * @return updated packing
   */
  public Packing updatePacking(
    Packing packing,
    PackageSize packageSize,
    fi.metatavu.famifarm.rest.model.Packing packingBody,
    List<PackingBasket> validPackingBaskets,
    Product product,
    Campaign campaign,
    UUID modifier
  ) {
    packingDAO.updatePackageSize(packing, packageSize, modifier);
    packingDAO.updatePackedCount(packing, packing.getPackedCount(), modifier);
    packingDAO.updatePackingState(packing, packingBody.getState(), modifier);
    packingDAO.updateProduct(packing, product, modifier);
    packingDAO.updateTime(packing, packingBody.getTime(), modifier);
    packingDAO.updateCampaign(packing, campaign, modifier);
    packingDAO.updateType(packing, packingBody.getType(), modifier);
    packingDAO.updateStartTime(packing, packingBody.getStartTime(), modifier);
    packingDAO.updateEndTime(packing, packingBody.getEndTime(), modifier);
    packingDAO.updateAdditionalInformation(packing, packingBody.getAdditionalInformation(), modifier);

    List<PackingBasket> existingBaskets = packingBasketDAO.listByPacking(packing);
    for (PackingBasket existingBasket : existingBaskets) {
      packingBasketDAO.delete(existingBasket);
    }
    if (validPackingBaskets != null) {
      for (PackingBasket packingBasket : validPackingBaskets) {
        packingBasketDAO.persist(packingBasket);
      }
    }

    List<PackingVerificationWeighting> existingVerificationWeightings = packingVerificationWeightingDAO.listByPacking(packing);
    for (PackingVerificationWeighting existingVerificationWeighing : existingVerificationWeightings) {
      packingVerificationWeightingDAO.delete(existingVerificationWeighing);
    }
    if (packingBody.getVerificationWeightings() != null) {
      for (fi.metatavu.famifarm.rest.model.PackingVerificationWeighing packingVerificationWeighing : packingBody.getVerificationWeightings()) {
        packingVerificationWeightingDAO.create(UUID.randomUUID(), packing, packingVerificationWeighing.getWeight(), packingVerificationWeighing.getTime());
      }
    }

    return packing;
  }

  /**
   * Deletes packing
   *
   * @param packing to be deleted
   */
  public void deletePacking(Packing packing) {
    List<PackingBasket> packingBaskets = packingBasketDAO.listByPacking(packing);
    for (PackingBasket packingBasket : packingBaskets) {
      packingBasketDAO.delete(packingBasket);
    }
    List<PackingVerificationWeighting> packingVerificationWeightings = packingVerificationWeightingDAO.listByPacking(packing);
    for (PackingVerificationWeighting packingVerificationWeighting : packingVerificationWeightings) {
      packingVerificationWeightingDAO.delete(packingVerificationWeighting);
    }
    packingDAO.delete(packing);
  }
}
