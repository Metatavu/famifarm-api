package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.BatchDAO;
import fi.metatavu.famifarm.persistence.dao.WastageEventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.persistence.model.WastageReason;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Controller for wastage events
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class WastageEventController {

  @Inject
  private BatchDAO batchDAO;
  
  @Inject
  private WastageEventDAO wastageEventDAO;

  /**
   * Creates new wastage event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param amount amount
   * @param wastageReason reason of wastage
   * @param description description
   * @param additionalInformation additional information
   * @param creatorId creator id
   * @return created wastage event
   */
  @SuppressWarnings ("squid:S00107")
  public WastageEvent createWastageEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer amount, WastageReason wastageReason, EventType phase, String additionalInformation, UUID creatorId) {
    return wastageEventDAO.create(UUID.randomUUID(), amount, batch, wastageReason, startTime, endTime, 0, phase, additionalInformation, creatorId, creatorId);
  }

  /**
   * Updates wastage event
   * 
   * @param wastageEvent wastage event to update
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param amount amount
   * @param wastageReason wastage reason
   * @param additionalInformation additional information
   * @param lastModifierId last modifier id
   * @param phase phase
   * @return updated wastage event
   */
  @SuppressWarnings ("squid:S00107")
  public WastageEvent updateWastageEvent(WastageEvent wastageEvent, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer amount, WastageReason wastageReason, EventType phase, String additionalInformation, UUID lastModifierId) {
    wastageEventDAO.updateAmount(wastageEvent, amount, lastModifierId);
    wastageEventDAO.updateBatch(wastageEvent, batch, lastModifierId);
    wastageEventDAO.updateEndTime(wastageEvent, endTime, lastModifierId);
    wastageEventDAO.updateStartTime(wastageEvent, startTime, lastModifierId);
    wastageEventDAO.updateWastageReason(wastageEvent, wastageReason, lastModifierId);
    wastageEventDAO.updateAdditionalInformation(wastageEvent, additionalInformation, lastModifierId);
    wastageEventDAO.updatePhase(wastageEvent, phase, lastModifierId);
    return wastageEvent;
  }

  /**
   * Finds wastage event by id
   * 
   * @param wastageEventId wastage event id
   * @return wastage event with id or null if not found
   */
  public WastageEvent findWastageEventById(UUID wastageEventId) {
    return wastageEventDAO.findById(wastageEventId);
  }

  /**
   * Lists wastage events filtered by first and max results
   * 
   * @param firstResult start from index
   * @param maxResults max results
   * @return list of wastage events
   */
  public List<WastageEvent> listWastageEvents(Integer firstResult, Integer maxResults) {
    return wastageEventDAO.listAll(firstResult, maxResults);
  }

  /**
   * Deletes wastage event
   * 
   * @param wastageEvent wastage event to delete
   */
  public void deleteWastageEvent(WastageEvent wastageEvent) {
    batchDAO.listByActiveBatch(wastageEvent).stream().forEach(batch -> batchDAO.updateActiveEvent(batch, null));
    wastageEventDAO.delete(wastageEvent);
  }

}