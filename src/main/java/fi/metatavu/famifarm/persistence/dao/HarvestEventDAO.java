package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.rest.model.HarvestEventType;

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
   * @param product product event is connected to
   * @param startTime start time
   * @param endTime end time
   * @param harvestType type
   * @param productionLine production line
   * @param sowingDate sowing date
   * @param remainingUnits remaining units
   * @param additionalInformation additional information
   * @param gutterCount gutterCount
   * @param gutterHoleCount gutter hole count
   * @param numberOfBaskets number of baskets
   * @param creatorId creator
   * @param lastModifierId last modifier
   * @return created harvest event
   */
  @SuppressWarnings ("squid:S00107")
  public HarvestEvent create(UUID id, Product product, OffsetDateTime startTime, OffsetDateTime endTime, HarvestEventType harvestType, ProductionLine productionLine, OffsetDateTime sowingDate, Integer remainingUnits, String additionalInformation, Integer gutterCount, Integer gutterHoleCount, Integer numberOfBaskets, UUID creatorId, UUID lastModifierId) {
    HarvestEvent harvestEvent = new HarvestEvent();
    harvestEvent.setProduct(product);
    harvestEvent.setRemainingUnits(remainingUnits);
    harvestEvent.setStartTime(startTime);
    harvestEvent.setEndTime(endTime);
    harvestEvent.setHarvestType(harvestType);
    harvestEvent.setProductionLine(productionLine);
    harvestEvent.setGutterCount(gutterCount);
    harvestEvent.setGutterHoleCount(gutterHoleCount);
    harvestEvent.setNumberOfBaskets(numberOfBaskets);
    harvestEvent.setId(id);
    harvestEvent.setCreatorId(creatorId);
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setAdditionalInformation(additionalInformation);
    harvestEvent.setSowingDate(sowingDate);
    return persist(harvestEvent);
  }

  /**
   * Updates sowingTime
   *
   * @return updated harvestEvent
   */
  public HarvestEvent updateSowingDate(HarvestEvent harvestEvent, OffsetDateTime sowingDate, UUID lastModifierId) {
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setSowingDate(sowingDate);
    return persist(harvestEvent);
  }

  /**
   * Updates harvestType
   *
   * @param harvestType harvestType
   * @param lastModifierId modifier
   * @return updated harvestEvent
   */
  public HarvestEvent updateHarvestType(HarvestEvent harvestEvent, HarvestEventType harvestType, UUID lastModifierId) {
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setHarvestType(harvestType);
    return persist(harvestEvent);
  }

  /**
   * Updates productionLine
   *
   * @param productionLine productionLine
   * @param lastModifierId modifier
   * @return updated harvestEvent
   */
  public HarvestEvent updateProductionLine(HarvestEvent harvestEvent, ProductionLine productionLine, UUID lastModifierId) {
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setProductionLine(productionLine);
    return persist(harvestEvent);
  }

  /**
   * Updates gutter count
   *
   * @param harvestEvent harvest event to be updated
   * @param gutterCount new gutter count
   * @param lastModifierId modifier
   * @return updated harvestEvent
   */
  public HarvestEvent updateGutterCount(HarvestEvent harvestEvent, Integer gutterCount, UUID lastModifierId) {
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setGutterCount(gutterCount);
    return persist(harvestEvent);
  }

  /**
   * Updates gutter hole count
   *
   * @param harvestEvent harvest event to be updated
   * @param gutterHoleCount new gutter hole count
   * @param lastModifierId modifier
   * @return updated harvestEvent
   */
  public HarvestEvent updateGutterHoleCount(HarvestEvent harvestEvent, Integer gutterHoleCount, UUID lastModifierId) {
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setGutterHoleCount(gutterHoleCount);
    return persist(harvestEvent);
  }

  /**
   * Updates number of baskets
   *
   * @param harvestEvent harvest event to be updated
   * @param numberOfBaskets number of baskets
   * @param lastModifierId modifier
   * @return updated harvestEvent
   */
  public HarvestEvent updateNumberOfBaskets(HarvestEvent harvestEvent, Integer numberOfBaskets, UUID lastModifierId) {
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setNumberOfBaskets(numberOfBaskets);
    return persist(harvestEvent);
  }

}
