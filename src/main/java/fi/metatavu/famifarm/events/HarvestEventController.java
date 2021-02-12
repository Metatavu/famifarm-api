package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.HarvestEventDAO;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.ProductionLine;

/**
 * Controller for harvest events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class HarvestEventController {

  @Inject
  private HarvestEventDAO harvestEventDAO;  
  

  /**
   * Creates new harvest event
   * 
   * @param product product event is connected to
   * @param startTime start time
   * @param endTime end time
   * @param harvestType type
   * @param productionLine production line
   * @param additionalInformation additional information
   * @param gutterCount gutterCount
   * @param gutterHoleCount gutter hole count
   * @param creatorId creator id
   * @return created harvest event
   */
  @SuppressWarnings ("squid:S00107")
  public HarvestEvent createHarvestEvent(Product product, OffsetDateTime startTime, OffsetDateTime endTime, fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum harvestType, ProductionLine productionLine, OffsetDateTime sowingDate, String additionalInformation, Integer gutterCount, Integer gutterHoleCount, UUID creatorId) {
    return harvestEventDAO.create(UUID.randomUUID(), product, startTime, endTime, harvestType, productionLine, sowingDate, 0, additionalInformation, gutterCount, gutterHoleCount, creatorId, creatorId);
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
   * @param product product
   * @param startTime start time
   * @param endTime end time
   * @param harvestType harvestType
   * @param productionLine productionLine
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated harvestEvent
   */
  @SuppressWarnings ("squid:S00107")

  public HarvestEvent updateHarvestEvent(HarvestEvent harvestEvent, Product product, OffsetDateTime startTime, OffsetDateTime endTime, fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum harvestType, ProductionLine productionLine, OffsetDateTime sowingDate, Integer gutterCount, Integer gutterHoleCount, String additionalInformation, UUID modifier) {

    harvestEventDAO.updateProduct(harvestEvent, product, modifier);
    harvestEventDAO.updateStartTime(harvestEvent, startTime, modifier);
    harvestEventDAO.updateEndTime(harvestEvent, endTime, modifier);
    harvestEventDAO.updateHarvestType(harvestEvent, harvestType, modifier);
    harvestEventDAO.updateProductionLine(harvestEvent, productionLine, modifier);
    harvestEventDAO.updateAdditionalInformation(harvestEvent, additionalInformation, modifier);
    harvestEventDAO.updateGutterCount(harvestEvent, gutterCount, modifier);
    harvestEventDAO.updateSowingDate(harvestEvent, sowingDate, modifier);
    harvestEventDAO.updateGutterHoleCount(harvestEvent, gutterHoleCount, modifier);

    return harvestEvent;
  }
  
  /**
   * Deletes an harvest event
   * 
   * @param harvestEvent harvest event to be deleted
   */
  public void deleteHarvestEvent(HarvestEvent harvestEvent) {
    harvestEventDAO.delete(harvestEvent);
  }

}
