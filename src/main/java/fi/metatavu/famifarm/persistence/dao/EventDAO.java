package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * Generic DAO class for events
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class EventDAO extends AbstractEventDAO<Event> {

  /**
   * Lists events by product optionally limited by first and max results
   * 
   * @param product product to retrieve events from
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of events filtered by product
   */
  public List<Event> listByProduct(Product product, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Event> criteria = criteriaBuilder.createQuery(Event.class);
    Root<Event> root = criteria.from(Event.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Event_.product), product));
    
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
   * Lists for rest api
   *
   * @param facility product facility
   * @param product product
   * @param createdAfter created after
   * @param createdBefore created before
   * @param firstResult first result
   * @param eventType event type
   * @param maxResults max results
   *
   * @return list of events
   */
  public List<Event> listForRestApi(Facility facility, Product product, OffsetDateTime startAfter, OffsetDateTime startBefore, Integer firstResult, EventType eventType, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Event> criteria = criteriaBuilder.createQuery(Event.class);
    Root<? extends Event>root = getRoot(criteria, eventType);
    criteria.select(root);

    List<Predicate> restrictions = new ArrayList<>();

    if (facility != null) {
      root.fetch(Event_.product, JoinType.LEFT);
      restrictions.add(criteriaBuilder.equal(root.get(Event_.product).get(Product_.facility), facility));
    }

    if (product != null) {
      restrictions.add(criteriaBuilder.equal(root.get(Event_.product), product));
    }

    if (startBefore != null) {
      restrictions.add(criteriaBuilder.lessThanOrEqualTo(root.get(Event_.startTime), startBefore));
    }

    if (startAfter != null) {
      restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.startTime), startAfter));
    }

    criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
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
   * Gets root based on the required event type
   *
   * @param criteria criteria
   * @param eventType eventType
   * @return root of correct event object
   */
  private Root<? extends Event> getRoot(CriteriaQuery<Event> criteria, EventType eventType){
    if (eventType != null) {
      if (eventType.equals(EventType.HARVEST)) {
        return criteria.from(HarvestEvent.class);
      } else if (eventType.equals(EventType.SOWING)) {
        return criteria.from(SowingEvent.class);
      } else if (eventType.equals(EventType.CULTIVATION_OBSERVATION)) {
        return criteria.from(CultivationObservationEvent.class);
      } else if (eventType.equals(EventType.PLANTING)) {
        return criteria.from(PlantingEvent.class);
      } else if (eventType.equals(EventType.TABLE_SPREAD)) {
        return criteria.from(TableSpreadEvent.class);
      } else if (eventType.equals(EventType.WASTAGE)) {
        return criteria.from(WastageEvent.class);
      }
    }

    return criteria.from(Event.class);
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
   * @param facility facility
   * @param startBefore start before
   * @param startAfter start after
   * @return list of events
   */
  public List<Event> listByFacilityAndStartTimeAfterAndStartTimeBefore(Facility facility, OffsetDateTime startBefore, OffsetDateTime startAfter) {
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

    if (facility != null) {
        restrictions.add(criteriaBuilder.equal(root.get(Event_.product).get(Product_.facility), facility));
    }

    criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
    TypedQuery<Event> query = entityManager.createQuery(criteria);
    return query.getResultList();
  }

  /**
   * Lists events by product optionally limited by first and max results. Sorts result by descending start time 
   * 
   * @param product product to retrieve events from
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of events filtered by product
   */
  public List<Event> listByProductSortByStartTimeDesc(Product product, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Event> criteria = criteriaBuilder.createQuery(Event.class);
    Root<Event> root = criteria.from(Event.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Event_.product), product));
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
   * Lists events by product optionally limited by first and max results. Sorts result by ascending start time 
   * 
   * @param product product to retrieve events from
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of events filtered by product
   */
  public List<Event> listByProductSortByStartTimeAsc(Product product, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Event> criteria = criteriaBuilder.createQuery(Event.class);
    Root<Event> root = criteria.from(Event.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Event_.product), product));
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
