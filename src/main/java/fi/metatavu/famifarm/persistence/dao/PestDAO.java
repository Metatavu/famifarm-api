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
 * DAO class for pest
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PestDAO extends AbstractDAO<Pest> {

  /**
   * Creates new pest
   *
   * @param name name
   * @param lastModifier modifier
   * @return created pest
   */
  public Pest create(UUID id, LocalizedEntry name, Facility facility, UUID creatorId, UUID lastModifierId) {
    Pest pest = new Pest();
    pest.setName(name);
    pest.setFacility(facility);
    pest.setId(id);
    pest.setCreatorId(creatorId);
    pest.setLastModifierId(lastModifierId);
    return persist(pest);
  }

  /**
   * Updates name
   *
   * @param pest pest
   * @param name name
   * @param lastModifier modifier
   * @return updated pest
   */
  public Pest updateName(Pest pest, LocalizedEntry name, UUID lastModifierId) {
    pest.setLastModifierId(lastModifierId);
    pest.setName(name);
    return persist(pest);
  }

  /**
   * Lists pests by facility
   *
   * @param facility not null facility
   * @param firstResult firstResult
   * @param maxResults maxResults
   * @return pests
   */
  public List<Pest> list(Facility facility, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Pest> criteria = criteriaBuilder.createQuery(Pest.class);
    Root<Pest> root = criteria.from(Pest.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Pest_.facility), facility));

    TypedQuery<Pest> query = entityManager.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }
}
