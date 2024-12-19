package fi.metatavu.famifarm.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * DAO class for performed cultivation action
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class PerformedCultivationActionDAO extends AbstractDAO<PerformedCultivationAction> {

  /**
   * Creates new performed cultivation action
   *
   * @param name name
   * @param lastModifier modifier
   * @return created performed cultivation action
   */
  public PerformedCultivationAction create(UUID id, LocalizedEntry name, Facility facility, UUID creatorId, UUID lastModifierId) {
    PerformedCultivationAction performedCultivationAction = new PerformedCultivationAction();
    performedCultivationAction.setName(name);
    performedCultivationAction.setId(id);
    performedCultivationAction.setFacility(facility);
    performedCultivationAction.setCreatorId(creatorId);
    performedCultivationAction.setLastModifierId(lastModifierId);
    return persist(performedCultivationAction);
  }

  /**
   * Updates name
   *
   * @param performedCultivationAction performed cultivation action
   * @param name name
   * @param lastModifier modifier
   * @return updated performed cultivation action
   */
  public PerformedCultivationAction updateName(PerformedCultivationAction performedCultivationAction, LocalizedEntry name, UUID lastModifierId) {
    performedCultivationAction.setLastModifierId(lastModifierId);
    performedCultivationAction.setName(name);
    return persist(performedCultivationAction);
  }

  /**
   * Lists actions
   *
   * @param facility required
   * @param firstResult optional
   * @param maxResults optional
   * @return action list
   */
  public List<PerformedCultivationAction> list(Facility facility, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PerformedCultivationAction> criteria = criteriaBuilder.createQuery(PerformedCultivationAction.class);
    Root<PerformedCultivationAction> root = criteria.from(PerformedCultivationAction.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PerformedCultivationAction_.facility), facility));
    criteria.orderBy(criteriaBuilder.desc(root.get(PerformedCultivationAction_.createdAt)));

    TypedQuery<PerformedCultivationAction> query = entityManager.createQuery(criteria);
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }
}
