package fi.metatavu.famifarm.persistence.dao;

import java.util.ArrayList;
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
 * DAO class for package sizes
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class ProductDAO extends AbstractDAO<Product> {

  /**
   * Creates new product
   *
   * @param id id
   * @param name name
   * @param isSubcontractorProduct is subcontractor product
   * @param isEndProduct is end product
   * @param isRawMaterial is raw material
   * @param salesWeight sales weight
   * @param facility facility
   * @param creatorId creator
   * @param lastModifierId modifier
   *
   * @return created seed
   */
  public Product create(UUID id, LocalizedEntry name, boolean isSubcontractorProduct, boolean active, boolean isEndProduct, boolean isRawMaterial, double salesWeight, Facility facility, UUID creatorId, UUID lastModifierId) {
    Product product = new Product();
    product.setId(id);
    product.setName(name);
    product.setIsSubcontractorProduct(isSubcontractorProduct);
    product.setIsActive(active);
    product.setIsEndProduct(isEndProduct);
    product.setIsRawMaterial(isRawMaterial);
    product.setSalesWeight(salesWeight);
    product.setFacility(facility);
    product.setCreatorId(creatorId);
    product.setLastModifierId(lastModifierId);
    return persist(product);
  }

  /**
   * Updates name
   *
   * @param product product
   * @param name name
   * @param lastModifierId modifier
   * @return updated product
   */
  public Product updateName(Product product, LocalizedEntry name, UUID lastModifierId) {
    product.setLastModifierId(lastModifierId);
    product.setName(name);
    return persist(product);
  }

  /**
   * Updates is active
   *
   * @param product product
   * @param isActive is active
   * @param lastModifierId modifier
   * @return updated product
   */
  public Product updateIsActive(Product product, Boolean isActive, UUID lastModifierId) {
    product.setIsActive(isActive);
    product.setLastModifierId(lastModifierId);
    return persist(product);
  }

  /**
   * Updates isSubcontractorProduct-field
   *
   * @param product a product to update
   * @param isSubcontractorProduct a new value
   * @param lastModifierId an id of a user who is modifying this product
   * @return updated product
   */
  public Product updateIsSubcontractorProduct(Product product, boolean isSubcontractorProduct, UUID lastModifierId) {
    product.setLastModifierId(lastModifierId);
    product.setIsSubcontractorProduct(isSubcontractorProduct);
    return persist(product);
  }

  /**
   * Updates isEndProduct
   *
   * @param product a product to update
   * @param isEndProduct a new value
   * @param lastModifierId an id of a user who is modifying this product
   * @return updated product
   */
  public Product updateIsEndProduct(Product product, boolean isEndProduct, UUID lastModifierId) {
    product.setLastModifierId(lastModifierId);
    product.setIsEndProduct(isEndProduct);
    return persist(product);
  }

  /**
   * Updates isRawMaterial
   *
   * @param product a product to update
   * @param isRawMaterial a new value
   * @param lastModifierId an id of a user who is modifying this product
   * @return updated product
   */
  public Product updateIsRawMaterial(Product product, boolean isRawMaterial, UUID lastModifierId) {
    product.setLastModifierId(lastModifierId);
    product.setIsRawMaterial(isRawMaterial);
    return persist(product);
  }

  /**
   * Updates salesWeight
   *
   * @param product a product to update
   * @param salesWeight a new value
   * @param lastModifierId an id of a user who is modifying this product
   * @return updated product
   */
  public Product updateSalesWeight(Product product, Double salesWeight, UUID lastModifierId) {
    product.setLastModifierId(lastModifierId);
    product.setSalesWeight(salesWeight);
    return persist(product);
  }

  public List<Product> list(Facility facility, Integer firstResult, Integer maxResults, Boolean includeSubcontractorProducts, Boolean includeInActiveProducts, Boolean filterEndProducts, Boolean filterByRawMaterials) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
    Root<Product> root = criteria.from(Product.class);
    criteria.select(root);
    criteria.orderBy(criteriaBuilder.desc(root.get(Product_.createdAt)));

    List<Predicate> restrictions = new ArrayList<>();
    restrictions.add(criteriaBuilder.equal(root.get(Product_.facility), facility));

    if (includeSubcontractorProducts == null || !includeSubcontractorProducts) {
      restrictions.add(criteriaBuilder.equal(root.get(Product_.isSubcontractorProduct), false));
    }

    if (includeInActiveProducts == null || !includeInActiveProducts) {
      restrictions.add(criteriaBuilder.equal(root.get(Product_.isActive), true));
    }

    if (filterEndProducts != null && filterEndProducts) {
      restrictions.add(criteriaBuilder.equal(root.get(Product_.isEndProduct), true));
    }

    if (filterByRawMaterials != null && filterByRawMaterials) {
      restrictions.add(criteriaBuilder.equal(root.get(Product_.isRawMaterial), true));
    }

    criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
    TypedQuery<Product> query = entityManager.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

}
