package fi.metatavu.famifarm.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.WastageReason;
import fi.metatavu.famifarm.persistence.model.WastageReason_;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * DAO class for wastage reason
 *
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class WastageReasonDAO extends AbstractDAO<WastageReason> {

  /**
   * Creates new wastage reason
   *
   * @param reason reason
   * @param lastModifierId modifier
   * @param facility facility
   * @return created wastage reason
   */
  public WastageReason create(UUID id, LocalizedEntry reason, UUID creatorId, UUID lastModifierId, Facility facility) {
    WastageReason wastageReason = new WastageReason();
    wastageReason.setReason(reason);
    wastageReason.setId(id);
    wastageReason.setCreatorId(creatorId);
    wastageReason.setLastModifierId(lastModifierId);
    wastageReason.setFacility(facility);
    return persist(wastageReason);
  }

  /**
   * Updates reason
   *
   * @param wastageReason wastage reason
   * @param reason reason
   * @param lastModifierId modifier
   * @return updated wastage reason
   */
  public WastageReason updateReason(WastageReason wastageReason, LocalizedEntry reason, UUID lastModifierId) {
    wastageReason.setLastModifierId(lastModifierId);
    wastageReason.setReason(reason);
    return persist(wastageReason);
  }

  /**
   * Lists all wastage reasons by first result, maxResults and facility
   *
   * @param firstResult first result
   * @param maxResults max results
   * @param facility facility
   * @return List of WastageReasons
   */
  public List<WastageReason> listAll(Integer firstResult, Integer maxResults, Facility facility) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<WastageReason> criteria = criteriaBuilder.createQuery(WastageReason.class);
    Root<WastageReason> root = criteria.from(WastageReason.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(WastageReason_.FACILITY), facility));
    criteria.orderBy(criteriaBuilder.desc(root.get(WastageReason_.createdAt)));

    TypedQuery<WastageReason> query = entityManager.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

}
