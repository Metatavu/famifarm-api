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
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.SowingEvent_;
import fi.metatavu.famifarm.rest.model.PotType;

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
   * @param product product
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param potType pot type
   * @param amount amount
   * @param remainingUnits remaining units
   * @return created sowingEvent
   * @param lastModifier modifier
   */
  @SuppressWarnings ("squid:S00107")
  public SowingEvent create(UUID id, Product product, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, PotType potType, Integer amount, Integer remainingUnits, String additionalInformation, UUID creatorId, UUID lastModifierId) {
    SowingEvent sowingEvent = new SowingEvent();
    sowingEvent.setProduct(product);
    sowingEvent.setRemainingUnits(remainingUnits);
    sowingEvent.setStartTime(startTime);
    sowingEvent.setEndTime(endTime);
    sowingEvent.setProductionLine(productionLine);
    sowingEvent.setPotType(potType);
    sowingEvent.setAmount(amount);
    sowingEvent.setId(id);
    sowingEvent.setCreatorId(creatorId);
    sowingEvent.setLastModifierId(lastModifierId);
    sowingEvent.setAdditionalInformation(additionalInformation);
    return persist(sowingEvent);
  }
  
  /**
   * Lists events by product
   * 
   * @param product product
   * @return List of events
   */
  public List<SowingEvent> listByProduct(Product product) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SowingEvent> criteria = criteriaBuilder.createQuery(SowingEvent.class);
    Root<SowingEvent> root = criteria.from(SowingEvent.class);
    
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(SowingEvent_.product), product));
    
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
   * Updates pot type
   *
   * @param sowingEvent event to be updated
   * @param potType pot type
   * @param lastModifier modifier
   * @return updated sowingEvent
   */
  public SowingEvent updatePotType(SowingEvent sowingEvent, PotType potType, UUID lastModifierId) {
    sowingEvent.setLastModifierId(lastModifierId);
    sowingEvent.setPotType(potType);
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
  public SowingEvent updateAmount(SowingEvent sowingEvent, Integer amount, UUID lastModifierId) {
    sowingEvent.setLastModifierId(lastModifierId);
    sowingEvent.setAmount(amount);
    return persist(sowingEvent);
  }

}
