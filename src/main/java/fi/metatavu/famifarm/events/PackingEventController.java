package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.BatchDAO;
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
  private BatchDAO batchDAO;
  
  @Inject
  private PackingEventDAO packingEventDAO;  

  /**
   * Creates packing event
   * 
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param packageSize package size
   * @param packedCount packed amount
   * @param additionalInformation additional information
   * @param creatorId creatorId
   * @return created packing event
   */
  @SuppressWarnings ("squid:S00107")
  public PackingEvent createPackingEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, PackageSize packageSize, Integer packedCount, String additionalInformation, UUID creatorId) {
    return packingEventDAO.create(UUID.randomUUID(), batch, startTime, endTime, packageSize, packedCount, 0, additionalInformation, creatorId, creatorId);
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
   * @param packedCount packed amount
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated packing event
   */
  @SuppressWarnings ("squid:S00107")
  public PackingEvent updatePackingEvent(PackingEvent packingEvent, Batch batch,  OffsetDateTime startTime, OffsetDateTime endTime, PackageSize packageSize, Integer packedCount, String additionalInformation, UUID modifier) {
    packingEventDAO.updateBatch(packingEvent, batch, modifier);
    packingEventDAO.updateStartTime(packingEvent, startTime, modifier);
    packingEventDAO.updateEndTime(packingEvent, endTime, modifier);
    packingEventDAO.updatePackageSize(packingEvent, packageSize, modifier);
    packingEventDAO.updatePackedCount(packingEvent, packedCount, modifier);
    packingEventDAO.updateAdditionalInformation(packingEvent, additionalInformation, modifier);
    return packingEvent;
  }
  
  /**
   * Deletes an packing event
   * 
   * @param packingEvent packing event to be deleted
   */
  public void deletePackingEvent(PackingEvent packingEvent) {
    batchDAO.listByActiveBatch(packingEvent).stream().forEach(batch -> batchDAO.updateActiveEvent(batch, null));
    packingEventDAO.delete(packingEvent);
  }

}
