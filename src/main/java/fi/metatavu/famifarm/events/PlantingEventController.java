package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.BatchDAO;
import fi.metatavu.famifarm.persistence.dao.PlantingEventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
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
  private BatchDAO batchDAO;
  
  @Inject
  private PlantingEventDAO plantingEventDAO;  

  /**
   * Creates new plantingEvent
   *
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param gutterSize gutterSize
   * @param gutterCount gutterCount
   * @param cellCount cellCount
   * @param workerCount workerCount
   * @return created plantingEvent
   * @param lastModifier modifier
   */
  @SuppressWarnings ("squid:S00107")
  public PlantingEvent createPlantingEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Integer gutterSize, Integer gutterCount, Integer cellCount, Integer workerCount, UUID creatorId) {
    return plantingEventDAO.create(UUID.randomUUID(), batch, startTime, endTime, productionLine, gutterSize, gutterCount, cellCount, workerCount, 0, creatorId, creatorId);
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
   * Update plantingEvent
   *
   * @param plantingEvent plantingEvent
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param gutterSize gutterSize
   * @param gutterCount gutterCount
   * @param cellCount cellCount
   * @param workerCount workerCount
   * @param type type
   * @param modifier modifier
   * @return updated plantingEvent
   */
  @SuppressWarnings ("squid:S00107")
  public PlantingEvent updatePlantingEvent(PlantingEvent plantingEvent, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Integer gutterSize, Integer gutterCount, Integer cellCount, Integer workerCount, UUID modifier) {
    plantingEventDAO.updateBatch(plantingEvent, batch, modifier);
    plantingEventDAO.updateStartTime(plantingEvent, startTime, modifier);
    plantingEventDAO.updateEndTime(plantingEvent, endTime, modifier);
    plantingEventDAO.updateProductionLine(plantingEvent, productionLine, modifier);
    plantingEventDAO.updateGutterSize(plantingEvent, gutterSize, modifier);
    plantingEventDAO.updateGutterCount(plantingEvent, gutterCount, modifier);
    plantingEventDAO.updateCellCount(plantingEvent, cellCount, modifier);
    plantingEventDAO.updateWorkerCount(plantingEvent, workerCount, modifier);
    return plantingEvent;
  }
  
  /**
   * Deletes an planting event
   * 
   * @param plantingEvent planting event to be deleted
   */
  public void deletePlantingEvent(PlantingEvent plantingEvent) {
    batchDAO.listByActiveBatch(plantingEvent).stream().forEach(batch -> batchDAO.updateActiveEvent(batch, null));
    plantingEventDAO.delete(plantingEvent);
  }

}
