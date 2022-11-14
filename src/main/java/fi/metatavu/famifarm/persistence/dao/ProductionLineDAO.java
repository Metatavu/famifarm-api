package fi.metatavu.famifarm.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.ProductionLine_;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * DAO class for seed batches
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class ProductionLineDAO extends AbstractDAO<ProductionLine> {

  /**
   * Creates new seed production line
   *
   * @param id id
   * @param facility facility
   * @param lineNumber lineNumber
   * @param defaultGutterHoleCount default gutter hole count
   * @param creatorId creatorId
   * @param lastModifierId lastModifierId
   * @return created production line
   */
  public ProductionLine create(UUID id, Facility facility, String lineNumber, Integer defaultGutterHoleCount, UUID creatorId, UUID lastModifierId) {
    ProductionLine productionLine = new ProductionLine();
    productionLine.setId(id);
    productionLine.setFacility(facility);
    productionLine.setLineNumber(lineNumber);
    productionLine.setDefaultGutterHoleCount(defaultGutterHoleCount);
    productionLine.setCreatorId(creatorId);
    productionLine.setLastModifierId(lastModifierId);
    return persist(productionLine);
  }

  /**
   * Lists production lines sorted by line number
   */
  public List<ProductionLine> listSortByLineNumber(Facility facility, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProductionLine> criteria = criteriaBuilder.createQuery(ProductionLine.class);
    Root<ProductionLine> root = criteria.from(ProductionLine.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ProductionLine_.facility), facility));
    criteria.orderBy(criteriaBuilder.asc(root.get(ProductionLine_.lineNumber)));
    
    TypedQuery<ProductionLine> query = entityManager.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  /**
   * Updates line number
   *
   * @param productionLine productionLine
   * @param lineNumber lineNumber
   * @param lastModifier modifier
   * @return updated production line
   */
  public ProductionLine updateLineNumber(ProductionLine productionLine, String lineNumber, UUID lastModifierId) {
    productionLine.setLastModifierId(lastModifierId);
    productionLine.setLineNumber(lineNumber);
    return persist(productionLine);
  }

  /**
   * Updates defaultGutterHoleCount
   *
   * @param productionLine productionLine
   * @param defaultGutterHoleCount defaultGutterHoleCount
   * @param lastModifier modifier
   * @return updated production line
   */
  public ProductionLine updateDefaultGutterHoleCount(ProductionLine productionLine, Integer defaultGutterHoleCount, UUID lastModifierId) {
    productionLine.setLastModifierId(lastModifierId);
    productionLine.setDefaultGutterHoleCount(defaultGutterHoleCount);
    return persist(productionLine);
  }
}
