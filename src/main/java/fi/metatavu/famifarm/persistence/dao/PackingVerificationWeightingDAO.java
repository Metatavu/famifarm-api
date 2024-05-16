package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.PackingVerificationWeighting;
import fi.metatavu.famifarm.persistence.model.PackingVerificationWeighting_;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for PackingVerificationWeighting
 */
@ApplicationScoped
public class PackingVerificationWeightingDAO extends AbstractDAO<PackingVerificationWeighting> {

  /**
   * Creates new PackingVerificationWeighing
   *
   * @param id      id
   * @param packing packing
   * @param weight  weight
   * @param time    time
   * @return created PackingVerificationWeighing
   */
  public PackingVerificationWeighting create(
    UUID id,
    Packing packing,
    Float weight,
    OffsetDateTime time
  ) {
    PackingVerificationWeighting packingVerificationWeighting = new PackingVerificationWeighting();
    packingVerificationWeighting.setId(id);
    packingVerificationWeighting.setPacking(packing);
    packingVerificationWeighting.setWeight(weight);
    packingVerificationWeighting.setTime(time);
    return persist(packingVerificationWeighting);
  }

  /**
   * Lists PackingVerificationWeightings by Packing
   *
   * @param packing packing filter
   * @return PackingVerificationWeighting list
   */
  public List<PackingVerificationWeighting> listByPacking(Packing packing) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PackingVerificationWeighting> criteria = criteriaBuilder.createQuery(PackingVerificationWeighting.class);
    Root<PackingVerificationWeighting> root = criteria.from(PackingVerificationWeighting.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PackingVerificationWeighting_.packing), packing));
    TypedQuery<PackingVerificationWeighting> query = entityManager.createQuery(criteria);
    return query.getResultList();
  }
}
