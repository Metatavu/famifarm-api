package fi.metatavu.famifarm.packing;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.PackingDAO;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Packing;

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
   * Creates packing
   * 
   * @param creatorId
   * @param packageSize
   * @param packedCount
   * @return created packing
   */
  public Packing create(UUID creatorId, PackageSize packageSize, Integer packedCount) {
    return packingDAO.create(creatorId, UUID.randomUUID(), creatorId, packageSize, packedCount);
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
  public List<Packing> listPackingEvents(Integer firstResult, Integer maxResults) {
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
  public Packing updatePacking(Packing packing, PackageSize packageSize, Integer packedCount, UUID modifier) {
    packingDAO.updatePackageSize(packing, packageSize, modifier);
    packingDAO.updatePackedCount(packing, packedCount, modifier);
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
