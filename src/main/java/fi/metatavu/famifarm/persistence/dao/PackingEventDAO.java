package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.PackingEvent;

/**
 * DAO class for PackingEvents
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PackingEventDAO extends AbstractEventDAO<PackingEvent> {

  /**
   * Creates new packingEvent
   *
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param packageSize package size
   * @param packedAmount packed amount
   * @param lastModifier modifier
   * @return created packingEvent
   */
  @SuppressWarnings ("squid:S00107")
  public PackingEvent create(UUID id, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, PackageSize packageSize, Integer packedAmount, UUID creatorId, UUID lastModifierId) {
    PackingEvent packingEvent = new PackingEvent();
    packingEvent.setBatch(batch);
    packingEvent.setStartTime(startTime);
    packingEvent.setEndTime(endTime);
    packingEvent.setPackageSize(packageSize);
    packingEvent.setPackedAmount(packedAmount);
    packingEvent.setId(id);
    packingEvent.setCreatorId(creatorId);
    packingEvent.setLastModifierId(lastModifierId);
    return persist(packingEvent);
  }

  /**
   * Updates packageSize
   *
   * @param packageSize packageSize
   * @param lastModifier modifier
   * @return updated packingEvent
   */
  public PackingEvent updatePackageSize(PackingEvent packingEvent, PackageSize packageSize, UUID lastModifierId) {
    packingEvent.setLastModifierId(lastModifierId);
    packingEvent.setPackageSize(packageSize);
    return persist(packingEvent);
  }

  /**
   * Updates packedAmount
   *
   * @param packedAmount packedAmount
   * @param lastModifier modifier
   * @return updated packingEvent
   */
  public PackingEvent updatePackedAmount(PackingEvent packingEvent, Integer packedAmount, UUID lastModifierId) {
    packingEvent.setLastModifierId(lastModifierId);
    packingEvent.setPackedAmount(packedAmount);
    return persist(packingEvent);
  }
  
}
