package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Batch_;
import fi.metatavu.famifarm.persistence.model.Event_;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.rest.BatchListStatus;
import fi.metatavu.famifarm.rest.model.BatchPhase;

/**
 * DAO class for batches
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class BatchDAO extends AbstractDAO<Batch> {

  /**
   * Creates new batch
   *
   * @param id id
   * @param product product
   * @param phase phase
   * @param creatorId creatorId
   * @param lastModifierId lastModifierId
   * @return created seed
   */
  public Batch create(UUID id, Product product, BatchPhase phase, UUID creatorId, UUID lastModifierId) {
    Batch batch = new Batch();
    batch.setId(id);
    batch.setProduct(product);
    batch.setPhase(phase);
    batch.setCreatorId(creatorId);
    batch.setLastModifierId(lastModifierId);
    batch.setTotalSowed(0);
    return persist(batch);
  }
  
  /**
   * Lists batches
   *
   * @param createdBefore created before (optional)
   * @param createdAfter created after (optional)
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of batches
   */
  @SuppressWarnings ("squid:S00107")
  public List<Batch> list(Product product, BatchPhase phase, BatchListStatus status, OffsetDateTime createdBefore, OffsetDateTime createdAfter, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Batch> criteria = criteriaBuilder.createQuery(Batch.class);
    Root<Batch> root = criteria.from(Batch.class);
    
    criteria.select(root);
    
    List<Predicate> restrictions = new ArrayList<>();
    
    if (product != null) {
      restrictions.add(criteriaBuilder.equal(root.get(Batch_.product), product));
    }
    
    if (phase != null) {
      restrictions.add(criteriaBuilder.equal(root.get(Batch_.phase), phase));
    }

    if (createdBefore != null) {
      restrictions.add(criteriaBuilder.lessThanOrEqualTo(root.get(Batch_.createdAt), createdBefore));
    }

    if (createdAfter != null) {
      restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Batch_.createdAt), createdAfter));
    }

    if (status != null) {
      Join<Batch, Event> activeEventJoin = root.join(Batch_.activeEvent);
      if (status == BatchListStatus.CLOSED || status == BatchListStatus.NEGATIVE) {
        Expression<Integer> totalSowed = root.get(Batch_.totalSowed);
        Expression<Integer> borderValue = criteriaBuilder.toInteger(criteriaBuilder.neg(criteriaBuilder.abs(criteriaBuilder.prod(totalSowed, 0.05))));

        if (status == BatchListStatus.CLOSED) {
          restrictions.add(criteriaBuilder.lessThanOrEqualTo(activeEventJoin.get(Event_.remainingUnits), 0));
          restrictions.add(criteriaBuilder.greaterThanOrEqualTo(activeEventJoin.get(Event_.remainingUnits), borderValue));
        }

        if (status == BatchListStatus.NEGATIVE) {
          restrictions.add(criteriaBuilder.lessThan(activeEventJoin.get(Event_.remainingUnits), borderValue));
        }
      }

      if (status == BatchListStatus.OPEN) {
        restrictions.add(criteriaBuilder.greaterThan(activeEventJoin.get(Event_.remainingUnits), 0));
      }
    }
    
    criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
    criteria.orderBy(criteriaBuilder.desc(root.get(Batch_.createdAt)));
    
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
   * Updates phase
   *
   * @param batch batch
   * @param phase phase
   * @param lastModifier modifier
   * @return updated batch
   */
  public Batch updatePhase(Batch batch, BatchPhase phase, UUID lastModifierId) {
    batch.setLastModifierId(lastModifierId);
    batch.setPhase(phase);
    return persist(batch);
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

  /**
   * Updates batches creation date
   * 
   * @param batch batch
   * @param createdAt created at
   * @return updated batch
   */
  public Batch updateCreatedAt(Batch batch, OffsetDateTime createdAt) {
    batch.setCreatedAt(createdAt);
    return persist(batch);
  }

  /**
   * Updates total sowed amount of the batch
   *
   * @param batch batch to update
   * @param totalSowed new amount of sowed seeds
   *
   * @return updated batch
   */
  public Batch updateTotalSowed(Batch batch, int totalSowed) {
    batch.setTotalSowed(totalSowed);
    return persist(batch);
  }

}
