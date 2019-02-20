package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Batch_;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Event_;
import fi.metatavu.famifarm.persistence.model.Product;

/**
 * DAO class for batches
 * 
 * @author Ville Koivukangas
 */
public class BatchDAO extends AbstractDAO<Batch> {

  /**
   * Creates new batch
   *
   * @param id id
   * @param product product
   * @param creatorId creatorId
   * @param lastModifierId lastModifierId
   * @return created seed
   */
  public Batch create(UUID id, Product product, UUID creatorId, UUID lastModifierId) {
    Batch batch = new Batch();
    batch.setId(id);
    batch.setProduct(product);
    batch.setCreatorId(creatorId);
    batch.setLastModifierId(lastModifierId);
    return persist(batch);
  }
  
  /**
   * Lists batches where active event's remaining units is less than given value
   * 
   * @param remainingUnits remaining units
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of batches
   */
  public List<Batch> listByRemainingUnitsLessThan(int remainingUnits, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Batch> criteria = criteriaBuilder.createQuery(Batch.class);
    Root<Batch> root = criteria.from(Batch.class);
    Join<Batch, Event> activeEventJoin = root.join(Batch_.activeEvent);
    
    criteria.select(root);
    criteria.where(criteriaBuilder.lessThan(activeEventJoin.get(Event_.remainingUnits), remainingUnits));
    
    TypedQuery<Batch> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }
  
  /**
   * Lists batches where active event's remaining units is greater than given value
   * 
   * @param remainingUnits remaining units
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of batches
   */
  public List<Batch> listByRemainingUnitsGreaterThan(int remainingUnits, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Batch> criteria = criteriaBuilder.createQuery(Batch.class);
    Root<Batch> root = criteria.from(Batch.class);
    Join<Batch, Event> activeEventJoin = root.join(Batch_.activeEvent);
    
    criteria.select(root);
    criteria.where(criteriaBuilder.greaterThan(activeEventJoin.get(Event_.remainingUnits), remainingUnits));
    
    TypedQuery<Batch> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }
  
  /**
   * Lists batches where active event's remaining units equal given value
   * 
   * @param remainingUnits remaining units
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of batches
   */
  public List<Batch> listByRemainingUnitsEquals(int remainingUnits, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Batch> criteria = criteriaBuilder.createQuery(Batch.class);
    Root<Batch> root = criteria.from(Batch.class);
    Join<Batch, Event> activeEventJoin = root.join(Batch_.activeEvent);
    
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(activeEventJoin.get(Event_.remainingUnits), remainingUnits));
    
    TypedQuery<Batch> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  /**
   * Lists batches by active event
   * 
   * @param event event
   * @return list of batches
   */
  public List<Batch> listByActiveBatch(Event event) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Batch> criteria = criteriaBuilder.createQuery(Batch.class);
    Root<Batch> root = criteria.from(Batch.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Batch_.activeEvent), event));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  /**
   * Updates product
   *
   * @param batch batch
   * @param product product
   * @param lastModifier modifier
   * @return updated batch
   */
  public Batch updateProduct(Batch batch, Product product, UUID lastModifierId) {
    batch.setLastModifierId(lastModifierId);
    batch.setProduct(product);
    return persist(batch);
  }
  
  /**
   * List batches between created times
   * 
   * @param firstResult firstResult
   * @param maxResults maxResults
   * @param createdBefore createdBefore
   * @param createdAfter createdAfter
   * @return list of batches
   */
  public List<Batch> listByCreatedBetween(Integer firstResult, Integer maxResults, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Batch> criteria = criteriaBuilder.createQuery(Batch.class);
    Root<Batch> root = criteria.from(Batch.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.between(root.get(Batch_.CREATED_AT), createdAfter, createdBefore));
    TypedQuery<Batch> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }
  
  /**
   * List batches created before given time
   * 
   * @param firstResult firstResult
   * @param maxResults maxResults
   * @param createdBefore createdBefore
   * @return list of batches
   */
  public List<Batch> listByCreatedBefore(Integer firstResult, Integer maxResults, OffsetDateTime createdBefore) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Batch> criteria = criteriaBuilder.createQuery(Batch.class);
    Root<Batch> root = criteria.from(Batch.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.lessThanOrEqualTo(root.get(Batch_.CREATED_AT), createdBefore));
    TypedQuery<Batch> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }
  
  /**
   * List batches created after given time
   * 
   * @param firstResult firstResult
   * @param maxResults maxResults
   * @param createdAfter createdAfter
   * @return list of batches
   */
  public List<Batch> listByCreatedAfter(Integer firstResult, Integer maxResults, OffsetDateTime createdAfter) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Batch> criteria = criteriaBuilder.createQuery(Batch.class);
    Root<Batch> root = criteria.from(Batch.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.greaterThanOrEqualTo(root.get(Batch_.CREATED_AT), createdAfter));
    TypedQuery<Batch> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  /**
   * Updates batches active event 
   * 
   * @param batch batch
   * @param activeEvent active event
   * @return updated batch
   */
  public Batch updateActiveEvent(Batch batch, Event activeEvent) {
    batch.setActiveEvent(activeEvent);
    return persist(batch);
  }

}
