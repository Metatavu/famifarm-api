package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.PackagingFilmBatch;
import fi.metatavu.famifarm.persistence.model.PackagingFilmBatch_;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for packaging film batches
 *
 * @author Katja Danilova
 */
@ApplicationScoped
public class PackagingFilmBatchDAO extends AbstractDAO<PackagingFilmBatch> {

  /**
   * Creates new packaging film batch
   *
   * @param id             id
   * @param facility      facility
   * @param name           name
   * @param isActive      isActive
   * @param arrivalTime  arrivalTime
   * @param creatorId     creator
   * @param lastModifierId modifier
   * @return created packaging film batch
   */
  public PackagingFilmBatch create(
    UUID id,
    Facility facility,
    String name,
    Boolean isActive,
    OffsetDateTime arrivalTime,
    UUID creatorId,
    UUID lastModifierId
  ) {
    PackagingFilmBatch packagingFilmBatch = new PackagingFilmBatch();
    packagingFilmBatch.setId(id);
    packagingFilmBatch.setFacility(facility);
    packagingFilmBatch.setName(name);
    packagingFilmBatch.setActive(isActive);
    packagingFilmBatch.setArrivalTime(arrivalTime);
    packagingFilmBatch.setCreatorId(creatorId);
    packagingFilmBatch.setLastModifierId(lastModifierId);
    return persist(packagingFilmBatch);
  }

  /**
   * Updates name
   *
   * @param packagingFilmBatch    packagingFilmBatch
   * @param name           name
   * @param lastModifierId modifier
   * @return updated packagingFilmBatch
   */
  public PackagingFilmBatch updateName(
    PackagingFilmBatch packagingFilmBatch,
    String name,
    UUID lastModifierId
  ) {
    packagingFilmBatch.setLastModifierId(lastModifierId);
    packagingFilmBatch.setName(name);
    return persist(packagingFilmBatch);
  }

  /**
   * Updates isActive
   *
   * @param packagingFilmBatch    packagingFilmBatch
   * @param isActive      isActive
   * @param lastModifierId modifier
   * @return updated packagingFilmBatch
   */
  public PackagingFilmBatch updateIsActive(
    PackagingFilmBatch packagingFilmBatch,
    Boolean isActive,
    UUID lastModifierId
  ) {
    packagingFilmBatch.setLastModifierId(lastModifierId);
    packagingFilmBatch.setActive(isActive);
    return persist(packagingFilmBatch);
  }

  /**
   * Updates arrivalTime
   *
   * @param packagingFilmBatch    packagingFilmBatch
   * @param arrivalTime  arrivalTime
   * @param lastModifierId modifier
   * @return updated packagingFilmBatch
   */
  public PackagingFilmBatch updateArrivalTime(
    PackagingFilmBatch packagingFilmBatch,
    OffsetDateTime arrivalTime,
    UUID lastModifierId
  ) {
    packagingFilmBatch.setLastModifierId(lastModifierId);
    packagingFilmBatch.setArrivalTime(arrivalTime);
    return persist(packagingFilmBatch);
  }

  /**
   * Lists packaging film batches
   *
   * @param firstResult  index of the first result, null to get results from the beginning
   * @param maxResults   maximum number of results, null for no limit
   * @param facility    facility
   * @param active      active status
   * @return list of packaging film batches
   */
  public List<PackagingFilmBatch> list(Integer firstResult, Integer maxResults, Facility facility, Boolean active) {
    EntityManager em = getEntityManager();

    CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
    javax.persistence.criteria.CriteriaQuery<PackagingFilmBatch> criteria = criteriaBuilder.createQuery(PackagingFilmBatch.class);
    javax.persistence.criteria.Root<PackagingFilmBatch> root = criteria.from(PackagingFilmBatch.class);
    criteria.select(root);

    List<Predicate> restrictions = new ArrayList<>();
    if (facility != null) {
      restrictions.add(criteriaBuilder.equal(root.get(PackagingFilmBatch_.facility), facility));
    }

    if (active != null) {
      restrictions.add(criteriaBuilder.equal(root.get(PackagingFilmBatch_.active), active));
    }
    criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
    criteria.orderBy(criteriaBuilder.desc(root.get(PackagingFilmBatch_.createdAt)));
    TypedQuery<PackagingFilmBatch> query = em.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }
}
