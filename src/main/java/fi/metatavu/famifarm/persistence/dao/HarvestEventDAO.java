package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.ProductionLine;

/**
 * DAO class for HarvestEvents
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class HarvestEventDAO extends AbstractEventDAO<HarvestEvent> {


  /**
   * Creates new harvest event
   * 
   * @param id event id
   * @param batch batch event is connected to
   * @param startTime start time
   * @param endTime end time
   * @param harvestType type
   * @param productionLine production line
   * @param remainingUnits remaining units
   * @param additionalInformation additional information
   * @param gutterCount gutterCount
   * @param creatorId creator
   * @param lastModifierId last modifier
   * @return created harvest event
   */
  @SuppressWarnings ("squid:S00107")
  public HarvestEvent create(UUID id, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum harvestType, ProductionLine productionLine, Integer remainingUnits, String additionalInformation, Integer gutterCount, UUID creatorId, UUID lastModifierId) {
    HarvestEvent harvestEvent = new HarvestEvent();
    harvestEvent.setBatch(batch);
    harvestEvent.setRemainingUnits(remainingUnits);
    harvestEvent.setStartTime(startTime);
    harvestEvent.setEndTime(endTime);
    harvestEvent.setHarvestType(harvestType);
    harvestEvent.setProductionLine(productionLine);
    harvestEvent.setGutterCount(gutterCount);
    harvestEvent.setId(id);
    harvestEvent.setCreatorId(creatorId);
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setAdditionalInformation(additionalInformation);
    return persist(harvestEvent);
  }

  /**
   * Updates harvestType
   *
   * @param harvestType harvestType
   * @param lastModifier modifier
   * @return updated harvestEvent
   */
  public HarvestEvent updateHarvestType(HarvestEvent harvestEvent, fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum harvestType, UUID lastModifierId) {
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setHarvestType(harvestType);
    return persist(harvestEvent);
  }

  /**
   * Updates productionLine
   *
   * @param productionLine productionLine
   * @param lastModifier modifier
   * @return updated harvestEvent
   */
  public HarvestEvent updateProductionLine(HarvestEvent harvestEvent, ProductionLine productionLine, UUID lastModifierId) {
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setProductionLine(productionLine);
    return persist(harvestEvent);
  }

}
