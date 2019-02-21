package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.Team;

/**
 * DAO class for HarvestEvents
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class HarvestEventDAO extends AbstractEventDAO<HarvestEvent> {

  /**
   * Creates new harvestEvent
   *
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param gutterNumber gutterNumber
   * @param seedBatch seedBatch
   * @param cellType cellType
   * @param amount amount
   * @param remainingUnits remaining units
   * @return created harvestEvent
   * @param lastModifier modifier
   */
  @SuppressWarnings ("squid:S00107")
  public HarvestEvent create(UUID id, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Team team, fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum harvestType, ProductionLine productionLine, Integer remainingUnits, String additionalInformation, UUID creatorId, UUID lastModifierId) {
    HarvestEvent harvestEvent = new HarvestEvent();
    harvestEvent.setBatch(batch);
    harvestEvent.setRemainingUnits(remainingUnits);
    harvestEvent.setStartTime(startTime);
    harvestEvent.setEndTime(endTime);
    harvestEvent.setTeam(team);
    harvestEvent.setHarvestType(harvestType);
    harvestEvent.setProductionLine(productionLine);
    harvestEvent.setId(id);
    harvestEvent.setCreatorId(creatorId);
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setAdditionalInformation(additionalInformation);
    return persist(harvestEvent);
  }

  /**
   * Updates team
   *
   * @param team team
   * @param lastModifier modifier
   * @return updated harvestEvent
   */
  public HarvestEvent updateTeam(HarvestEvent harvestEvent, Team team, UUID lastModifierId) {
    harvestEvent.setLastModifierId(lastModifierId);
    harvestEvent.setTeam(team);
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
