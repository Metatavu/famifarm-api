package fi.metatavu.famifarm.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEventPest;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEventPest_;
import fi.metatavu.famifarm.persistence.model.Pest;
import fi.metatavu.famifarm.persistence.model.Pest_;

/**
 * DAO class for cultivation pest event pest
 * 
 * @author Antti Leppä
 */
public class CultivationObservationEventPestDAO extends AbstractDAO<CultivationObservationEventPest> {

  /**
   * Creates new cultivationPestEventPest
   *
   * @param event event
   * @param pest pest
   * @return created cultivationPestEventPest
   * @param lastModifier modifier
   */
  public CultivationObservationEventPest create(UUID id, CultivationObservationEvent event, Pest pest) {
    CultivationObservationEventPest cultivationPestEventPest = new CultivationObservationEventPest();
    cultivationPestEventPest.setEvent(event);
    cultivationPestEventPest.setPest(pest);
    cultivationPestEventPest.setId(id);
    return persist(cultivationPestEventPest);
  }

  /**
   * Lists pests by event
   * 
   * @param event event
   * @return pests
   */
  public List<CultivationObservationEventPest> listByEvent(CultivationObservationEvent event) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CultivationObservationEventPest> criteria = criteriaBuilder.createQuery(CultivationObservationEventPest.class);
    Root<CultivationObservationEventPest> root = criteria.from(CultivationObservationEventPest.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(CultivationObservationEventPest_.event), event));
    
    return entityManager.createQuery(criteria).getResultList();
  }  

  /**
   * Lists performed cultivation pest ids by event
   * 
   * @param event event
   * @return performed cultivation pest ids by event
   */
  public List<UUID> listPerformedPestIdsByEvent(CultivationObservationEvent event) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UUID> criteria = criteriaBuilder.createQuery(UUID.class);
    Root<CultivationObservationEventPest> root = criteria.from(CultivationObservationEventPest.class);
    Join<CultivationObservationEventPest, Pest> pestJoin = root.join(CultivationObservationEventPest_.pest);
    criteria.select(pestJoin.get(Pest_.id));
    criteria.where(criteriaBuilder.equal(root.get(CultivationObservationEventPest_.event), event));
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
    Join<CultivationObservationEventPest, Pest> pestJoin = root.join(CultivationObservationEventPest_.pest);
    criteria.select(pestJoin.get(Pest_.id));
    criteria.where(criteriaBuilder.equal(root.get(CultivationObservationEventPest_.event), event));
    return entityManager.createQuery(criteria).getResultList();
  }
  
  /**
   * Updates event
   *
   * @param event event
   * @return updated cultivationPestEventPest
   */
  public CultivationObservationEventPest updateEvent(CultivationObservationEventPest cultivationPestEventPest, CultivationObservationEvent event) {
    cultivationPestEventPest.setEvent(event);
    return persist(cultivationPestEventPest);
  }

  /**
   * Updates pest
   *
   * @param pest pest
   * @return updated cultivationPestEventPest
   */
  public CultivationObservationEventPest updatePest(CultivationObservationEventPest cultivationPestEventPest, Pest pest) {
    cultivationPestEventPest.setPest(pest);
    return persist(cultivationPestEventPest);
  }

}
