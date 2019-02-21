package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.ProductionLine;

/**
 * DAO class for PlantingEvents
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PlantingEventDAO extends AbstractEventDAO<PlantingEvent> {

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
   * @param remainingUnits remaining units
   * @return created plantingEvent
   * @param lastModifier modifier
   */
  @SuppressWarnings ("squid:S00107")
  public PlantingEvent create(UUID id, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Integer gutterSize, Integer gutterCount, Integer cellCount, Integer workerCount, Integer remainingUnits, String additionalInformation, UUID creatorId, UUID lastModifierId) {
    PlantingEvent plantingEvent = new PlantingEvent();
    plantingEvent.setBatch(batch);
    plantingEvent.setRemainingUnits(remainingUnits);
    plantingEvent.setStartTime(startTime);
    plantingEvent.setEndTime(endTime);
    plantingEvent.setProductionLine(productionLine);
    plantingEvent.setGutterSize(gutterSize);
    plantingEvent.setGutterCount(gutterCount);
    plantingEvent.setCellCount(cellCount);
    plantingEvent.setWorkerCount(workerCount);
    plantingEvent.setId(id);
    plantingEvent.setCreatorId(creatorId);
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setAdditionalInformation(additionalInformation);
    return persist(plantingEvent);
  }

  /**
   * Updates productionLine
   *
   * @param productionLine productionLine
   * @param lastModifier modifier
   * @return updated plantingEvent
   */
  public PlantingEvent updateProductionLine(PlantingEvent plantingEvent, ProductionLine productionLine, UUID lastModifierId) {
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setProductionLine(productionLine);
    return persist(plantingEvent);
  }

  /**
   * Updates gutterSize
   *
   * @param gutterSize gutterSize
   * @param lastModifier modifier
   * @return updated plantingEvent
   */
  public PlantingEvent updateGutterSize(PlantingEvent plantingEvent, Integer gutterSize, UUID lastModifierId) {
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setGutterSize(gutterSize);
    return persist(plantingEvent);
  }

  /**
   * Updates gutterCount
   *
   * @param gutterCount gutterCount
   * @param lastModifier modifier
   * @return updated plantingEvent
   */
  public PlantingEvent updateGutterCount(PlantingEvent plantingEvent, Integer gutterCount, UUID lastModifierId) {
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setGutterCount(gutterCount);
    return persist(plantingEvent);
  }

  /**
   * Updates cellCount
   *
   * @param cellCount cellCount
   * @param lastModifier modifier
   * @return updated plantingEvent
   */
  public PlantingEvent updateCellCount(PlantingEvent plantingEvent, Integer cellCount, UUID lastModifierId) {
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setCellCount(cellCount);
    return persist(plantingEvent);
  }

  /**
   * Updates workerCount
   *
   * @param workerCount workerCount
   * @param lastModifier modifier
   * @return updated plantingEvent
   */
  public PlantingEvent updateWorkerCount(PlantingEvent plantingEvent, Integer workerCount, UUID lastModifierId) {
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setWorkerCount(workerCount);
    return persist(plantingEvent);
  }
  
}
