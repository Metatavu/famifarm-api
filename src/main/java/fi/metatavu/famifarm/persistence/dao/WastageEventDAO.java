package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.WastageEvent_;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.persistence.model.WastageReason;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * DAO for wastage events
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class WastageEventDAO extends AbstractEventDAO<WastageEvent> {

  /**
   * Creates new wastage event
   * 
   * @param id id
   * @param amount amount
   * @param product product
   * @param wastageReason wastage reason
   * @param description description
   * @param startTime start time
   * @param endTime end time
   * @param remainingUnits remaining units
   * @param creatorId creator id
   * @param lastModifierId last modfier id
   * @return created wastage event
   */
  @SuppressWarnings ("squid:S00107")
  public WastageEvent create(UUID id, Integer amount, Product product, WastageReason wastageReason, OffsetDateTime startTime, OffsetDateTime endTime, Integer remainingUnits, EventType phase, String additionalInformation, ProductionLine productionLine, UUID creatorId, UUID lastModifierId) {
    WastageEvent wastageEvent = new WastageEvent();
    wastageEvent.setRemainingUnits(remainingUnits);
    wastageEvent.setAmount(amount);
    wastageEvent.setProduct(product);
    wastageEvent.setWastageReason(wastageReason);
    wastageEvent.setId(id);
    wastageEvent.setStartTime(startTime);
    wastageEvent.setEndTime(endTime);
    wastageEvent.setCreatorId(creatorId);
    wastageEvent.setLastModifierId(lastModifierId);
    wastageEvent.setAdditionalInformation(additionalInformation);
    wastageEvent.setProductionLine(productionLine);
    wastageEvent.setPhase(phase);
    return persist(wastageEvent);
  }
  
  /**
   * Lists events by product
   * 
   * @param product product
   * @return List of events
   */
  public List<WastageEvent> listByProduct(Product product) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<WastageEvent> criteria = criteriaBuilder.createQuery(WastageEvent.class);
    Root<WastageEvent> root = criteria.from(WastageEvent.class);
    
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(WastageEvent_.product), product));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Updates amount
   * 
   * @param wastageEvent wastage event to update
   * @param amount new amount
   * @param lastModifierId last modifier id
   * @return updated wastage event
   */
  public WastageEvent updateAmount(WastageEvent wastageEvent, Integer amount, UUID lastModifierId) {
    wastageEvent.setAmount(amount);
    wastageEvent.setLastModifierId(lastModifierId);
    return persist(wastageEvent);
  }

  /**
   * Updates phase
   * 
   * @param wastageEvent wastage event to update
   * @param phase new phase
   * @param lastModifierId last modifier id
   * @return updated wastage event
   */
  public WastageEvent updatePhase(WastageEvent wastageEvent, EventType phase, UUID lastModifierId) {
    wastageEvent.setPhase(phase);
    wastageEvent.setLastModifierId(lastModifierId);
    return persist(wastageEvent);
  }

  /**
   * Updates production line
   *
   * @param wastageEvent wastage event
   * @param productionLine production line
   * @param lastModifierId last modifier id
   * @return updated wastage event
   */
  public WastageEvent updateProductionLine(WastageEvent wastageEvent, ProductionLine productionLine, UUID lastModifierId) {
    wastageEvent.setProductionLine(productionLine);
    wastageEvent.setLastModifierId(lastModifierId);
    return persist(wastageEvent);
  }

  /**
   * Updates wastage reason
   * 
   * @param wastageEvent wastage event to update
   * @param wastageReason new wastage reason
   * @param lastModifierId last modifier id
   * @return updated wastage event
   */
  public WastageEvent updateWastageReason(WastageEvent wastageEvent, WastageReason wastageReason, UUID lastModifierId) {
    wastageEvent.setWastageReason(wastageReason);
    wastageEvent.setLastModifierId(lastModifierId);
    return persist(wastageEvent);
  }
}