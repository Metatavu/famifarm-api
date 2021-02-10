package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.PlantingEvent_;
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
   * @param product product
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param sowingDate sowing date
   * @param gutterHoleCount gutterHoleCount
   * @param gutterCount gutterCount
   * @param trayCount trayCount
   * @param workerCount workerCount
   * @param remainingUnits remaining units
   * @return created plantingEvent
   * @param lastModifier modifier
   */
  @SuppressWarnings ("squid:S00107")
  public PlantingEvent create(UUID id, Product product, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, OffsetDateTime sowingDate, Integer gutterHoleCount, Integer gutterCount, Integer trayCount, Integer workerCount, Integer remainingUnits, String additionalInformation, UUID creatorId, UUID lastModifierId) {
    PlantingEvent plantingEvent = new PlantingEvent();
    plantingEvent.setProduct(product);
    plantingEvent.setRemainingUnits(remainingUnits);
    plantingEvent.setStartTime(startTime);
    plantingEvent.setEndTime(endTime);
    plantingEvent.setProductionLine(productionLine);
    plantingEvent.setGutterHoleCount(gutterHoleCount);
    plantingEvent.setGutterCount(gutterCount);
    plantingEvent.setTrayCount(trayCount);
    plantingEvent.setWorkerCount(workerCount);
    plantingEvent.setId(id);
    plantingEvent.setCreatorId(creatorId);
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setAdditionalInformation(additionalInformation);
    plantingEvent.setSowingDate(sowingDate);
    return persist(plantingEvent);
  }

  /**
   * Lists latest event by product and production line. Sorts result by descending start time 
   * 
   * @param product product to retrieve events from
   * @param productionLine production line
   * @param startBefore start before
   * @return List of single event filtered by product and production line
   */
  public List<PlantingEvent> listLatestByProductAndProductionLine(Product product, ProductionLine productionLine, OffsetDateTime startBefore) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PlantingEvent> criteria = criteriaBuilder.createQuery(PlantingEvent.class);
    Root<PlantingEvent> root = criteria.from(PlantingEvent.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PlantingEvent_.product), product));
    criteria.where(criteriaBuilder.equal(root.get(PlantingEvent_.productionLine), productionLine));
    criteria.where(criteriaBuilder.lessThanOrEqualTo(root.get(PlantingEvent_.startTime), startBefore));
    criteria.orderBy(criteriaBuilder.desc(root.get(PlantingEvent_.startTime)));
    
    TypedQuery<PlantingEvent> query = entityManager.createQuery(criteria);
    query.setFirstResult(0);
    query.setMaxResults(1);

    return query.getResultList();
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
   * Updates gutterHoleCount
   *
   * @param gutterHoleCount gutterHoleCount
   * @param lastModifier modifier
   * @return updated plantingEvent
   */
  public PlantingEvent updateGutterHoleCount(PlantingEvent plantingEvent, Integer gutterHoleCount, UUID lastModifierId) {
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setGutterHoleCount(gutterHoleCount);
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
   * Updates sowingDate
   *
   * @param sowingDate sowingDate
   * @param lastModifier modifier
   * @return updated plantingEvent
   */
  public PlantingEvent updateSowingDate(PlantingEvent plantingEvent, OffsetDateTime sowingDate, UUID lastModifierId) {
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setSowingDate(sowingDate);
    return persist(plantingEvent);
  }

  /**
   * Updates trayCount
   *
   * @param trayCount trayCount
   * @param lastModifier modifier
   * @return updated plantingEvent
   */
  public PlantingEvent updateTrayCount(PlantingEvent plantingEvent, Integer trayCount, UUID lastModifierId) {
    plantingEvent.setLastModifierId(lastModifierId);
    plantingEvent.setTrayCount(trayCount);
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
