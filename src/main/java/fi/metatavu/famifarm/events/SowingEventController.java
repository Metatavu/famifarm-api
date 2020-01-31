package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.BatchDAO;
import fi.metatavu.famifarm.persistence.dao.SowingEventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.SeedBatch;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.rest.model.PotType;

/**
 * Controller for sowing events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SowingEventController {

  @Inject
  private BatchDAO batchDAO;
  
  @Inject
  private SowingEventDAO sowingEventDAO;  
  
  /**
   * Update sowingEvent
   *
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param seedBatch seedBatch
   * @param potType pot type
   * @param amount amount
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated sowingEvent
   */
  @SuppressWarnings ("squid:S00107")
  public SowingEvent createSowingEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, SeedBatch seedBatch, PotType potType, Integer amount, String additionalInformation, UUID creatorId) {
    return sowingEventDAO.create(UUID.randomUUID(), batch, startTime, endTime, productionLine, seedBatch, potType, amount, 0, additionalInformation, creatorId, creatorId);
  }
  
  /**
   * Returns sowing event by id
   * 
   * @param sowingEventId id
   * @return sowing event or null if not found
   */
  public SowingEvent findSowingEventById(UUID sowingEventId) {
    return sowingEventDAO.findById(sowingEventId);
  }
  
  /**
   * Returns list of sowing events
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of sowing events
   */
  public List<SowingEvent> listSowingEvents(Integer firstResult, Integer maxResults) {
    return sowingEventDAO.listAll(firstResult, maxResults);
  }
  
  /**
   * Lists sowing events by batch 
   * 
   * @param batch batch
   * @return sowing events by batch 
   */
  public List<SowingEvent> listBatchSowingEvents(Batch batch) {
    return sowingEventDAO.listByBatch(batch);
  }

  /**
   * Update sowingEvent
   *
   * @param sowingEvent sowing event
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param seedBatch seedBatch
   * @param potType pot type
   * @param amount amount
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated sowingEvent
   */
  @SuppressWarnings ("squid:S00107")
  public SowingEvent updateSowingEvent(SowingEvent sowingEvent, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, SeedBatch seedBatch, PotType potType, Integer amount, String additionalInformation, UUID modifier) {
    sowingEventDAO.updateBatch(sowingEvent, batch, modifier);
    sowingEventDAO.updateStartTime(sowingEvent, startTime, modifier);
    sowingEventDAO.updateEndTime(sowingEvent, endTime, modifier);
    sowingEventDAO.updateProductionLine(sowingEvent, productionLine, modifier);
    sowingEventDAO.updateSeedBatch(sowingEvent, seedBatch, modifier);
    sowingEventDAO.updatePotType(sowingEvent, potType, modifier);
    sowingEventDAO.updateAmount(sowingEvent, amount, modifier);
    sowingEventDAO.updateAdditionalInformation(sowingEvent, additionalInformation, modifier);
    
    return sowingEvent;
  }
  
  /**
   * Deletes an sowing event
   * 
   * @param sowingEvent sowing event to be deleted
   */
  public void deleteSowingEvent(SowingEvent sowingEvent) {
    batchDAO.listByActiveBatch(sowingEvent).stream().forEach(batch -> batchDAO.updateActiveEvent(batch, null));
    sowingEventDAO.delete(sowingEvent);
  }

}
