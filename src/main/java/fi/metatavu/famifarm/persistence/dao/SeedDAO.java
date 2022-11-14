package fi.metatavu.famifarm.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * DAO class for seeds
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SeedDAO extends AbstractDAO<Seed> {

  /**
   * Creates new seed
   *
   * @param facility facility
   * @param name name
   * @return created seed
   * @param lastModifier modifier
   */
  public Seed create(UUID id, Facility facility, LocalizedEntry name, UUID creatorId, UUID lastModifierId) {
    Seed seed = new Seed();
    seed.setFacility(facility);
    seed.setName(name);
    seed.setId(id);
    seed.setCreatorId(creatorId);
    seed.setLastModifierId(lastModifierId);
    return persist(seed);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifier modifier
   * @return updated seed
   */
  public Seed updateName(Seed seed, LocalizedEntry name, UUID lastModifierId) {
    seed.setLastModifierId(lastModifierId);
    seed.setName(name);
    return persist(seed);
  }

  /**
   * Lists seeds by facility
   *
   * @param facility required facility filter
   * @param firstResult first result (optional)
   * @param maxResults mac results (optional)
   * @return filtered seed list
   */
  public List<Seed> list(Facility facility, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Seed> criteria = criteriaBuilder.createQuery(Seed.class);
    Root<Seed> root = criteria.from(Seed.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Seed_.facility), facility));
    criteria.orderBy(criteriaBuilder.desc(root.get(Seed_.createdAt)));

    TypedQuery<Seed> query = entityManager.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }
}
