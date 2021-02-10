package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.PlantingEventDAO;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.ProductionLine;

/**
 * Controller for planting events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PlantingEventController {

  @Inject
  private PlantingEventDAO plantingEventDAO;  

  /**
   * Creates new plantingEvent
   *
   * @param product product
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param gutterHoleCount gutterHoleCount
   * @param gutterCount gutterCount
   * @param trayCount trayCount
   * @param workerCount workerCount
   * @param additionalInformation additional information
   * @return created plantingEvent
   * @param lastModifier modifier
   */
  @SuppressWarnings ("squid:S00107")
  public PlantingEvent createPlantingEvent(Product product, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, OffsetDateTime sowingDate, Integer gutterHoleCount, Integer gutterCount, Integer trayCount, Integer workerCount, String additionalInformation, UUID creatorId) {
    return plantingEventDAO.create(UUID.randomUUID(), product, startTime, endTime, productionLine, sowingDate, gutterHoleCount, gutterCount, trayCount, workerCount, 0, additionalInformation, creatorId, creatorId);
  }
  
  /**
   * Returns planting event by id
   * 
   * @param plantingEventId id
   * @return planting event or null if not found
   */
  public PlantingEvent findPlantingEventById(UUID plantingEventId) {
    return plantingEventDAO.findById(plantingEventId);
  }
  
  /**
   * Returns list of planting events
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of planting events
   */
  public List<PlantingEvent> listPlantingEvents(Integer firstResult, Integer maxResults) {
    return plantingEventDAO.listAll(firstResult, maxResults);
  }

  /**
   * Lists latest event by product and production line. Sorts result by descending start time 
   * 
   * @param product product to retrieve events from
   * @param productionLine production line
   * @return List of single event filtered by product and production line
   */
  public List<PlantingEvent> listLatestPlatingEventByProductAndProductionLine(Product product, ProductionLine productionLine, OffsetDateTime startBefore) {
    return plantingEventDAO.listLatestByProductAndProductionLine(product, productionLine, startBefore);
  }

  /**
   * Update plantingEvent
   *
   * @param plantingEvent plantingEvent
   * @param product product
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param gutterHoleCount gutterHoleCount
   * @param gutterCount gutterCount
   * @param trayCount trayCount
   * @param workerCount workerCount
   * @param type type
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated plantingEvent
   */
  @SuppressWarnings ("squid:S00107")
  public PlantingEvent updatePlantingEvent(PlantingEvent plantingEvent, Product product, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, OffsetDateTime sowingDate, Integer gutterHoleCount, Integer gutterCount, Integer trayCount, Integer workerCount, String additionalInformation, UUID modifier) {
    plantingEventDAO.updateProduct(plantingEvent, product, modifier);
    plantingEventDAO.updateStartTime(plantingEvent, startTime, modifier);
    plantingEventDAO.updateEndTime(plantingEvent, endTime, modifier);
    plantingEventDAO.updateProductionLine(plantingEvent, productionLine, modifier);
    plantingEventDAO.updateGutterHoleCount(plantingEvent, gutterHoleCount, modifier);
    plantingEventDAO.updateGutterCount(plantingEvent, gutterCount, modifier);
    plantingEventDAO.updateTrayCount(plantingEvent, trayCount, modifier);
    plantingEventDAO.updateWorkerCount(plantingEvent, workerCount, modifier);
    plantingEventDAO.updateAdditionalInformation(plantingEvent, additionalInformation, modifier);
    plantingEventDAO.updateSowingDate(plantingEvent, sowingDate, modifier);
    return plantingEvent;
  }
  
  /**
   * Deletes an planting event
   * 
   * @param plantingEvent planting event to be deleted
   */
  public void deletePlantingEvent(PlantingEvent plantingEvent) {
    plantingEventDAO.delete(plantingEvent);
  }

}
