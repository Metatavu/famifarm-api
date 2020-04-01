package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Event_;

/**
 * Generic DAO class for events
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class EventDAO extends AbstractEventDAO<Event> {

  /**
   * Lists events by batch optionally limited by first and max results
   * 
   * @param batch batch to retrieve events from
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of events filtered by batch
   */
  public List<Event> listByBatch(Batch batch, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Event> criteria = criteriaBuilder.createQuery(Event.class);
    Root<Event> root = criteria.from(Event.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Event_.batch), batch));
    
    TypedQuery<Event> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  /**
   * Lists events between dates
   * 
   * @param createdBefore created before
   * @param createdAfter created after
   * @return list of events
   */
  public List<Event> listByCreatedAfterAndCreatedBefore(OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Event> criteria = criteriaBuilder.createQuery(Event.class);
    Root<Event> root = criteria.from(Event.class);
    criteria.select(root);

    List<Predicate> restrictions = new ArrayList<>();

    if (createdBefore != null) {
      restrictions.add(criteriaBuilder.lessThanOrEqualTo(root.get(Event_.createdAt), createdBefore));
    }

    if (createdAfter != null) {
      restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.createdAt), createdAfter));
    }

    criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
    TypedQuery<Event> query = entityManager.createQuery(criteria);

    return query.getResultList();
  }

  /**
   * Lists events between dates
   * 
   * @param startBefore start before
   * @param startAfter start after
   * @return list of events
   */
  public List<Event> listByStartTimeAfterAndStartTimeBefore(OffsetDateTime startBefore, OffsetDateTime startAfter) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Event> criteria = criteriaBuilder.createQuery(Event.class);
    Root<Event> root = criteria.from(Event.class);
    criteria.select(root);

    List<Predicate> restrictions = new ArrayList<>();

    if (startBefore != null) {
      restrictions.add(criteriaBuilder.lessThanOrEqualTo(root.get(Event_.startTime), startBefore));
    }

    if (startAfter != null) {
      restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.startTime), startAfter));
    }

    criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
    TypedQuery<Event> query = entityManager.createQuery(criteria);

    return query.getResultList();
  }

  /**
   * Lists events by batch optionally limited by first and max results. Sorts result by descending start time 
   * 
   * @param batch batch to retrieve events from
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of events filtered by batch
   */
  public List<Event> listByBatchSortByStartTimeDesc(Batch batch, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Event> criteria = criteriaBuilder.createQuery(Event.class);
    Root<Event> root = criteria.from(Event.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Event_.batch), batch));
    criteria.orderBy(criteriaBuilder.desc(root.get(Event_.startTime)));
    
    TypedQuery<Event> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }
    
    

    return query.getResultList();
  }

  /**
   * Lists events by batch optionally limited by first and max results. Sorts result by ascending start time 
   * 
   * @param batch batch to retrieve events from
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of events filtered by batch
   */
  public List<Event> listByBatchSortByStartTimeAsc(Batch batch, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Event> criteria = criteriaBuilder.createQuery(Event.class);
    Root<Event> root = criteria.from(Event.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Event_.batch), batch));
    criteria.orderBy(criteriaBuilder.asc(root.get(Event_.startTime)));
    
    TypedQuery<Event> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }
    
    return query.getResultList();
  }

}
