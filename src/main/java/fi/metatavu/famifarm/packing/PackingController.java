package fi.metatavu.famifarm.packing;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.PackingDAO;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.rest.model.PackingState;

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
   * Creates new packing
   * 
   * @param creatorId
   * @param productId
   * @param packageSize
   * @param packedCount
   * @param packingState
   * @param time
   * @return packing
   */
  public Packing create(UUID creatorId, Product product, PackageSize packageSize, Integer packedCount, PackingState packingState, OffsetDateTime time) {
    return packingDAO.create(creatorId, product, UUID.randomUUID(), creatorId, packageSize, packedCount, packingState, time);
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
   * @param productId
   * @param state
   * @param createdBefore
   * @param createdAfter
   * @return packings
   */
  public List<Packing> listPackings(Integer firstResult, Integer maxResults, Product product, PackingState state, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    return packingDAO.list(firstResult, maxResults, product, state, createdBefore, createdAfter);
  }
  
  /**
   * Updates packing
   * 
   * @param packing
   * @param packageSize
   * @param packedCount
   * @param modifier
   * @return updated packing
   */
  public Packing updatePacking(Packing packing, PackageSize packageSize, PackingState packingState, Integer packedCount, Product product, OffsetDateTime time, UUID modifier) {
    packingDAO.updatePackageSize(packing, packageSize, modifier);
    packingDAO.updatePackedCount(packing, packedCount, modifier);
    packingDAO.updatePackingState(packing, packingState, modifier);
    packingDAO.updateProduct(packing, product, modifier);
    packingDAO.updateTime(packing, time, modifier);
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
