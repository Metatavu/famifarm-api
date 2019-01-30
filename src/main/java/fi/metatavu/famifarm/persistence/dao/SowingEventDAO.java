package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.CellType;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.SeedBatch;
import fi.metatavu.famifarm.persistence.model.SowingEvent;

/**
 * DAO class for SowingEvents
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SowingEventDAO extends AbstractEventDAO<SowingEvent> {

  /**
   * Creates new sowingEvent
   *
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param gutterNumber gutterNumber
   * @param seedBatch seedBatch
   * @param cellType cellType
   * @param amount amount
   * @return created sowingEvent
   * @param lastModifier modifier
   */
  public SowingEvent create(UUID id, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Integer gutterNumber, SeedBatch seedBatch, CellType cellType, Double amount, UUID creatorId, UUID lastModifierId) {
    SowingEvent sowingEvent = new SowingEvent();
    sowingEvent.setBatch(batch);
    sowingEvent.setStartTime(startTime);
    sowingEvent.setEndTime(endTime);
    sowingEvent.setProductionLine(productionLine);
    sowingEvent.setGutterNumber(gutterNumber);
    sowingEvent.setSeedBatch(seedBatch);
    sowingEvent.setCellType(cellType);
    sowingEvent.setAmount(amount);
    sowingEvent.setId(id);
    sowingEvent.setCreatorId(creatorId);
    sowingEvent.setLastModifierId(lastModifierId);
    return persist(sowingEvent);
  }

  /**
   * Updates productionLine
   *
   * @param productionLine productionLine
   * @param lastModifier modifier
   * @return updated sowingEvent
   */
  public SowingEvent updateProductionLine(SowingEvent sowingEvent, ProductionLine productionLine, UUID lastModifierId) {
    sowingEvent.setLastModifierId(lastModifierId);
    sowingEvent.setProductionLine(productionLine);
    return persist(sowingEvent);
  }

  /**
   * Updates gutterNumber
   *
   * @param gutterNumber gutterNumber
   * @param lastModifier modifier
   * @return updated sowingEvent
   */
  public SowingEvent updateGutterNumber(SowingEvent sowingEvent, Integer gutterNumber, UUID lastModifierId) {
    sowingEvent.setLastModifierId(lastModifierId);
    sowingEvent.setGutterNumber(gutterNumber);
    return persist(sowingEvent);
  }

  /**
   * Updates seedBatch
   *
   * @param seedBatch seedBatch
   * @param lastModifier modifier
   * @return updated sowingEvent
   */
  public SowingEvent updateSeedBatch(SowingEvent sowingEvent, SeedBatch seedBatch, UUID lastModifierId) {
    sowingEvent.setLastModifierId(lastModifierId);
    sowingEvent.setSeedBatch(seedBatch);
    return persist(sowingEvent);
  }

  /**
   * Updates cellType
   *
   * @param cellType cellType
   * @param lastModifier modifier
   * @return updated sowingEvent
   */
  public SowingEvent updateCellType(SowingEvent sowingEvent, CellType cellType, UUID lastModifierId) {
    sowingEvent.setLastModifierId(lastModifierId);
    sowingEvent.setCellType(cellType);
    return persist(sowingEvent);
  }

  /**
   * Updates amount
   *
   * @param amount amount
   * @param lastModifier modifier
   * @return updated sowingEvent
   */
  public SowingEvent updateAmount(SowingEvent sowingEvent, Double amount, UUID lastModifierId) {
    sowingEvent.setLastModifierId(lastModifierId);
    sowingEvent.setAmount(amount);
    return persist(sowingEvent);
  }

}
