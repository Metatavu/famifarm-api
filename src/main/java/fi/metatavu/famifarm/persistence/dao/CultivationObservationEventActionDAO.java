package fi.metatavu.famifarm.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEventAction;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEventAction_;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEventPest;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEventPest_;
import fi.metatavu.famifarm.persistence.model.PerformedCultivationAction;
import fi.metatavu.famifarm.persistence.model.PerformedCultivationAction_;
import fi.metatavu.famifarm.persistence.model.Pest;
import fi.metatavu.famifarm.persistence.model.Pest_;

/**
 * DAO class for cultivation action event action
 * 
 * @author Antti Leppä
 */
public class CultivationObservationEventActionDAO extends AbstractDAO<CultivationObservationEventAction> {

  /**
   * Creates new cultivationActionEventAction
   *
   * @param event event
   * @param action action
   * @return created cultivationActionEventAction
   * @param lastModifier modifier
   */
  public CultivationObservationEventAction create(UUID id, CultivationObservationEvent event, PerformedCultivationAction action) {
    CultivationObservationEventAction cultivationActionEventAction = new CultivationObservationEventAction();
    cultivationActionEventAction.setEvent(event);
    cultivationActionEventAction.setAction(action);
    cultivationActionEventAction.setId(id);
    return persist(cultivationActionEventAction);
  }

  /**
   * Lists actions by event
   * 
   * @param event event
   * @return actions
   */
  public List<CultivationObservationEventAction> listByEvent(CultivationObservationEvent event) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CultivationObservationEventAction> criteria = criteriaBuilder.createQuery(CultivationObservationEventAction.class);
    Root<CultivationObservationEventAction> root = criteria.from(CultivationObservationEventAction.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(CultivationObservationEventAction_.event), event));
    
    return entityManager.createQuery(criteria).getResultList();
  }  

  /**
   * Lists performed cultivation action ids by event
   * 
   * @param event event
   * @return performed cultivation action ids by event
   */
  public List<UUID> listPerformedActionIdsByEvent(CultivationObservationEvent event) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UUID> criteria = criteriaBuilder.createQuery(UUID.class);
    Root<CultivationObservationEventAction> root = criteria.from(CultivationObservationEventAction.class);
    Join<CultivationObservationEventAction, PerformedCultivationAction> actionJoin = root.join(CultivationObservationEventAction_.action);
    criteria.select(actionJoin.get(PerformedCultivationAction_.id));
    criteria.where(criteriaBuilder.equal(root.get(CultivationObservationEventAction_.event), event));
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists pest ids by event
   * 
   * @param event event
   * @return pest ids by event
   */
  public List<UUID> listPestIdsByEvent(CultivationObservationEvent event) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UUID> criteria = criteriaBuilder.createQuery(UUID.class);
    Root<CultivationObservationEventPest> root = criteria.from(CultivationObservationEventPest.class);
    Join<CultivationObservationEventPest, Pest> actionJoin = root.join(CultivationObservationEventPest_.pest);
    criteria.select(actionJoin.get(Pest_.id));
    criteria.where(criteriaBuilder.equal(root.get(CultivationObservationEventPest_.event), event));
    return entityManager.createQuery(criteria).getResultList();
  }
  
  /**
   * Updates event
   *
   * @param event event
   * @return updated cultivationActionEventAction
   */
  public CultivationObservationEventAction updateEvent(CultivationObservationEventAction cultivationActionEventAction, CultivationObservationEvent event) {
    cultivationActionEventAction.setEvent(event);
    return persist(cultivationActionEventAction);
  }

  /**
   * Updates action
   *
   * @param action action
   * @return updated cultivationActionEventAction
   */
  public CultivationObservationEventAction updateAction(CultivationObservationEventAction cultivationActionEventAction, PerformedCultivationAction action) {
    cultivationActionEventAction.setAction(action);
    return persist(cultivationActionEventAction);
  }

}
