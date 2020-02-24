package fi.metatavu.famifarm.packing;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.PackingDAO;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Packing;

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
  public Packing create(UUID creatorId, UUID productId, PackageSize packageSize, Integer packedCount, PackingState packingState, OffsetDateTime time) {
    return packingDAO.create(creatorId, productId, UUID.randomUUID(), creatorId, packageSize, packedCount, packingState, time);
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
   * Returns list of packing
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of packing
   */
  public List<Packing> listPackings(Integer firstResult, Integer maxResults) {
    return packingDAO.listAll(firstResult, maxResults);
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
  public Packing updatePacking(Packing packing, PackageSize packageSize, PackingState packingState, Integer packedCount, UUID modifier) {
    packingDAO.updatePackageSize(packing, packageSize, modifier);
    packingDAO.updatePackedCount(packing, packedCount, modifier);
    packingDAO.updatePackingState(packingState, packingState, modifier);
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
