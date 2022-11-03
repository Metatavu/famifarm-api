package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.PackageSize_;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for package sizes
 *
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class PackageSizeDAO extends AbstractDAO<PackageSize> {

  /**
   * Creates new package size
   *
   * @param id             id
   * @param name           name
   * @param size           size
   * @param facility
   * @param lastModifierId modifier
   * @return creatorId seed
   */
  public PackageSize create(UUID id, LocalizedEntry name, Integer size, Facility facility, UUID creatorId, UUID lastModifierId) {
    PackageSize packageSize = new PackageSize();
    packageSize.setId(id);
    packageSize.setName(name);
    packageSize.setSize(size);
    packageSize.setFacility(facility);
    packageSize.setCreatorId(creatorId);
    packageSize.setLastModifierId(lastModifierId);
    return persist(packageSize);
  }

  /**
   * Updates name
   *
   * @param packageSize    packageSize
   * @param name           name
   * @param lastModifierId modifier
   * @return updated packageSize
   */
  public PackageSize updateName(PackageSize packageSize, LocalizedEntry name, UUID lastModifierId) {
    packageSize.setLastModifierId(lastModifierId);
    packageSize.setName(name);
    return persist(packageSize);
  }


  /**
   * Updates size
   *
   * @param packageSize  packageSize
   * @param size         size
   * @param lastModifierId modifier
   * @return updated packageSize
   */
  public PackageSize updateSize(PackageSize packageSize, Integer size, UUID lastModifierId) {
    packageSize.setLastModifierId(lastModifierId);
    packageSize.setSize(size);
    return persist(packageSize);
  }

  /**
   * Lists by facility
   *
   * @param facility    facility
   * @param firstResult first result
   * @param maxResults  max results
   * @return list of package size options
   */
  public List<PackageSize> list(Facility facility, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PackageSize> criteria = criteriaBuilder.createQuery(PackageSize.class);
    Root<PackageSize> root = criteria.from(PackageSize.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PackageSize_.facility), facility));

    TypedQuery<PackageSize> query = entityManager.createQuery(criteria);
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }
}
