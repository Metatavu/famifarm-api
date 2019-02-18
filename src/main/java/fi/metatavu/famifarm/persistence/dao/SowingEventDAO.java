package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.PackingEvent_;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.SeedBatch;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.rest.model.CellType;

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
   * @param remainingUnits remaining units
   * @return created sowingEvent
   * @param lastModifier modifier
   */
  public SowingEvent create(UUID id, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Integer gutterNumber, SeedBatch seedBatch, CellType cellType, Double amount, Integer remainingUnits, UUID creatorId, UUID lastModifierId) {
    SowingEvent sowingEvent = new SowingEvent();
    sowingEvent.setBatch(batch);
    sowingEvent.setRemainingUnits(remainingUnits);
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
   * Lists events by batch
   * 
   * @param batch batch
   * @return List of events
   */
  public List<SowingEvent> listByBatch(Batch batch) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SowingEvent> criteria = criteriaBuilder.createQuery(SowingEvent.class);
    Root<SowingEvent> root = criteria.from(SowingEvent.class);
    
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PackingEvent_.batch), batch));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Updates productionLine
   *
   * @param sowingEvent event to be updated
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
   * @param sowingEvent event to be updated
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
   * @param sowingEvent event to be updated
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
   * @param sowingEvent event to be updated
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
