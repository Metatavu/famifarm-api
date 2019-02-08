package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.PackingEventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.PackingEvent;

/**
 * Controller for packing events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PackingEventController {
  
  @Inject
  private PackingEventDAO packingEventDAO;  

  /**
   * Creates packing event
   * 
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param packageSize package size
   * @param packedAmount packed amount
   * @param creatorId creatorId
   * @return created packing event
   */
  @SuppressWarnings ("squid:S00107")
  public PackingEvent createPackingEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, PackageSize packageSize, Integer packedAmount, UUID creatorId) {
    return packingEventDAO.create(UUID.randomUUID(), batch, startTime, endTime, packageSize, packedAmount, creatorId, creatorId);
  }
  
  /**
   * Returns packing event by id
   * 
   * @param packingEventId id
   * @return packing event or null if not found
   */
  public PackingEvent findPackingEventById(UUID packingEventId) {
    return packingEventDAO.findById(packingEventId);
  }
  
  /**
   * Returns list of packing events
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of packing events
   */
  public List<PackingEvent> listPackingEvents(Integer firstResult, Integer maxResults) {
    return packingEventDAO.listAll(firstResult, maxResults);
  }

  /**
   * Updates packing event
   * 
   * @param packingEvent
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param packageSize package size
   * @param packedAmount packed amount
   * @param modifier modifier
   * @return updated packing event
   */
  @SuppressWarnings ("squid:S00107")
  public PackingEvent updatePackingEvent(PackingEvent packingEvent, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, PackageSize packageSize, Integer packedAmount, UUID modifier) {
    packingEventDAO.updateBatch(packingEvent, batch, modifier);
    packingEventDAO.updateStartTime(packingEvent, startTime, modifier);
    packingEventDAO.updateEndTime(packingEvent, endTime, modifier);
    packingEventDAO.updatePackageSize(packingEvent, packageSize, modifier);
    packingEventDAO.updatePackedAmount(packingEvent, packedAmount, modifier);
    return packingEvent;
  }
  
  /**
   * Deletes an packing event
   * 
   * @param packingEvent packing event to be deleted
   */
  public void deletePackingEvent(PackingEvent packingEvent) {
    packingEventDAO.delete(packingEvent);
  }

}
