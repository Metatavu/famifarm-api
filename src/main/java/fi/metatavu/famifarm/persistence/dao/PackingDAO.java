package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Packing;

/**
 * DAO class for Packing
 * 
 * @author simeon
 *
 */

@ApplicationScoped
public class PackingDAO extends AbstractDAO<Packing>{
    /**
     * Creates new packing
     * 
     * @param creatorId
     * @param id
     * @param lastModifierId
     * @param packageSize
     * @param packedCount
     * @return packing
     */
    public Packing create(UUID creatorId, UUID id, UUID lastModifierId, PackageSize packageSize, Integer packedCount) {
      Packing packing = new Packing();
      packing.setCreatorId(creatorId);
      packing.setId(id);
      packing.setLastModifierId(lastModifierId);
      packing.setPackageSize(packageSize);
      packing.setPackedCount(packedCount);
      return persist(packing);
    }
    
    /**
     * Updates package size
     * 
     * @param packing
     * @param packageSize
     * @param lastModifierId
     * @return updated packing
     */
    public Packing updatePackageSize(Packing packing, PackageSize packageSize, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setPackageSize(packageSize);
      return persist(packing);
    }
    
    /**
     * Updates packed count
     * 
     * @param packing
     * @param packedCount
     * @param lastModifierId
     * @return updated packing
     */
    public Packing updatePackedCount(Packing packing, Integer packedCount, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setPackedCount(packedCount);
      return persist(packing);
    }
}
