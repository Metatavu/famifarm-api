package fi.metatavu.famifarm.packings;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.PackingDAO;
import fi.metatavu.famifarm.persistence.model.Campaign;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.rest.model.Facility;
import fi.metatavu.famifarm.rest.model.PackingState;
import fi.metatavu.famifarm.rest.model.PackingType;

/**
 * Controller for packing
 * 
 * @author simeon
 *
 */

@ApplicationScoped
public class PackingController {
  
  @Inject
  private PackingDAO packingDAO;
  
  /**
   * Creates a new packing
   * 
   * @param creatorId creator id
   * @param facility required facility param
   * @param product product
   * @param packageSize package size
   * @param packedCount packed count
   * @param packingState packing status
   * @param time packing time
   * @param campaign campaign
   * @param type packing type
   * @return packing
   */
  public Packing create(UUID creatorId, Facility facility, Product product, PackageSize packageSize, Integer packedCount, PackingState packingState, OffsetDateTime time, Campaign campaign, PackingType type) {
    return packingDAO.create(creatorId, facility, product, UUID.randomUUID(), packageSize, packedCount, packingState, time, campaign, type);
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
   * @param facility non nullable facility
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
   * @param packing packing
   * @param packageSize package size
   * @param packedCount packed count
   * @param modifier modifier
   * @param type type
   * @return updated packing
   */
  public Packing updatePacking(Packing packing, PackageSize packageSize, PackingState packingState, Integer packedCount, Product product, OffsetDateTime time, Campaign campaign, PackingType type, UUID modifier) {
    packingDAO.updatePackageSize(packing, packageSize, modifier);
    packingDAO.updatePackedCount(packing, packedCount, modifier);
    packingDAO.updatePackingState(packing, packingState, modifier);
    packingDAO.updateProduct(packing, product, modifier);
    packingDAO.updateTime(packing, time, modifier);
    packingDAO.updateCampaign(packing, campaign, modifier);
    packingDAO.updateType(packing, type, modifier);
    return packing;
  }
  
  /**
   * Deletes packing
   * 
   * @param packing to be deleted
   */
  public void deletePacking(Packing packing) {
    packingDAO.delete(packing);
  }
}
