package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Packing;

import fi.metatavu.famifarm.rest.model.PackageState;

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
     * @param productId
     * @param id
     * @param lastModifierId
     * @param packageSize
     * @param packedCount
     * @param packageState
     * @param startTime
     * @param endTime
     * @return packing
     */
    public Packing create(UUID creatorId, UUID productId, UUID id, UUID lastModifierId, PackageSize packageSize, Integer packedCount, PackageState packageState, OffsetDateTime startTime, OffsetDateTime endTime) {
      Packing packing = new Packing();
      packing.setCreatorId(creatorId);
      packing.setId(id);
      packing.setProductId(productId);
      packing.setPackageState(packageState);
      packing.setLastModifierId(lastModifierId);
      packing.setPackageSize(packageSize);
      packing.setPackedCount(packedCount);
      packing.setStartTime(startTime);
      packing.setEndTime(endTime);
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
    
    /**
     * Updates package state
     * 
     * @param packing
     * @param packageState
     * @return updated packing
     */
    public Packing updatePackageState(Packing packing, PackageState packageState UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setPackageState(packageState);
      return persist(packaging);
    }
    
    /**
     * Updates startTime
     *
     * @param startTime startTime
     * @param lastModifier modifier
     * @return updated packing
     */
    public Packing updateStartTime(Packing packing, OffsetDateTime startTime, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setStartTime(startTime);
      return persist(packing);
    }

    /**
     * Updates endTime
     *
     * @param endTime endTime
     * @param lastModifier modifier
     * @return updated event
     */
    public Packing updateEndTime(Packing packing, OffsetDateTime endTime, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setEndTime(endTime);
      return persist(packing);
    }
}

