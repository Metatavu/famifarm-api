package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.BatchDAO;
import fi.metatavu.famifarm.persistence.dao.HarvestEventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.Team;

/**
 * Controller for harvest events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class HarvestEventController {

  @Inject
  private BatchDAO batchDAO;
  
  @Inject
  private HarvestEventDAO harvestEventDAO;  
  

  /**
   * Creates new harvest event
   * 
   * @param batch batch event is connected to
   * @param startTime start time
   * @param endTime end time
   * @param harvestType type
   * @param productionLine production line
   * @param additionalInformation additional information
   * @param gutterCount gutterCount
   * @param creatorId creator id
   * @return created harvest event
   */
  @SuppressWarnings ("squid:S00107")
  public HarvestEvent createHarvestEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Team team, fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum harvestType, ProductionLine productionLine, String additionalInformation, Integer gutterCount, UUID creatorId) {
    return harvestEventDAO.create(UUID.randomUUID(), batch, startTime, endTime, team, harvestType, productionLine, 0, additionalInformation, gutterCount, creatorId, creatorId);
  }
  
  /**
   * Returns harvest event by id
   * 
   * @param harvestEventId id
   * @return harvest event or null if not found
   */
  public HarvestEvent findHarvestEventById(UUID harvestEventId) {
    return harvestEventDAO.findById(harvestEventId);
  }
  
  /**
   * Returns list of harvest events
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of harvest events
   */
  public List<HarvestEvent> listHarvestEvents(Integer firstResult, Integer maxResults) {
    return harvestEventDAO.listAll(firstResult, maxResults);
  }
  
  /**
   * Update harvestEvent
   *
   * @param harvestEvent event
   * @param batch batch
   * @param harvestType harvestType
   * @param productionLine productionLine
   * @param type type
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated harvestEvent
   */
  @SuppressWarnings ("squid:S00107")
  public HarvestEvent updateHarvestEvent(HarvestEvent harvestEvent, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Team team, fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum harvestType, ProductionLine productionLine, Integer gutterCount, String additionalInformation, UUID modifier) {
    harvestEventDAO.updateBatch(harvestEvent, batch, modifier);
    harvestEventDAO.updateStartTime(harvestEvent, startTime, modifier);
    harvestEventDAO.updateEndTime(harvestEvent, endTime, modifier);
    harvestEventDAO.updateHarvestType(harvestEvent, harvestType, modifier);
    harvestEventDAO.updateProductionLine(harvestEvent, productionLine, modifier);
    harvestEventDAO.updateAdditionalInformation(harvestEvent, additionalInformation, modifier);
    harvestEventDAO.updateTeam(harvestEvent, team, modifier);
    harvestEventDAO.updateGutterCount(harvestEvent, gutterCount, modifier);
    return harvestEvent;
  }
  
  /**
   * Deletes an harvest event
   * 
   * @param harvestEvent harvest event to be deleted
   */
  public void deleteHarvestEvent(HarvestEvent harvestEvent) {
    batchDAO.listByActiveBatch(harvestEvent).stream().forEach(batch -> batchDAO.updateActiveEvent(batch, null));
    harvestEventDAO.delete(harvestEvent);
  }

}
