package fi.metatavu.famifarm.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.ProductAllowedHarvestType;
import fi.metatavu.famifarm.persistence.model.ProductAllowedHarvestType_;
import fi.metatavu.famifarm.rest.model.HarvestEventType;

/**
 * DAO class for allowed product harvest types
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class ProductAllowedHarvestTypeDAO extends AbstractDAO<ProductAllowedHarvestType> {

  /**
   * Creates new allowed harvest type for product
   * 
   * @param id id
   * @param harvestType harvest type
   * @param product product
   * @return created allowed harvest type
   */
  public ProductAllowedHarvestType create(UUID id, HarvestEventType harvestType, Product product) {
    ProductAllowedHarvestType allowedType = new ProductAllowedHarvestType();
    allowedType.setId(id);
    allowedType.setHarvestType(harvestType);
    allowedType.setProduct(product);
    return persist(allowedType);
  }

  /**
   * Lists allowed harvest types by product
   * 
   * @param product product
   * @return allowed harvest types by product
   */
  public List<ProductAllowedHarvestType> listByProduct(Product product) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProductAllowedHarvestType> criteria = criteriaBuilder.createQuery(ProductAllowedHarvestType.class);
    Root<ProductAllowedHarvestType> root = criteria.from(ProductAllowedHarvestType.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ProductAllowedHarvestType_.product), product));
    
    return entityManager.createQuery(criteria).getResultList();
  }
}
