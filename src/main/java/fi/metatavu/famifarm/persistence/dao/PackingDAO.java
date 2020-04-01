package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
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

import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.Packing_;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.rest.model.PackingState;

/**
 * DAO class for Packing
 * 
 * @author simeon
 *
 */

@ApplicationScoped
public class PackingDAO extends AbstractDAO<Packing>{
    /**
     * Creates new packing
     * 
     * @param creatorId
     * @param productId
     * @param id
     * @param lastModifierId
     * @param packageSize
     * @param packedCount
     * @param packingState
     * @param startTime
     * @return packing
     */
    public Packing create(UUID creatorId, Product product, UUID id, UUID lastModifierId, PackageSize packageSize, Integer packedCount, PackingState packingState, OffsetDateTime time) {
      Packing packing = new Packing();
      packing.setCreatorId(creatorId);
      packing.setId(id);
      packing.setProduct(product);
      packing.setPackingState(packingState);
      packing.setLastModifierId(lastModifierId);
      packing.setPackageSize(packageSize);
      packing.setPackedCount(packedCount);
      packing.setTime(time);
      return persist(packing);
    }
    
    /**
     * Updates package size
     * 
     * @param packing
     * @param packageSize
     * @param lastModifierId
     * @return updated packing
     */
    public Packing updatePackageSize(Packing packing, PackageSize packageSize, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setPackageSize(packageSize);
      return persist(packing);
    }
    
    /**
     * Updates packed count
     * 
     * @param packing
     * @param packedCount
     * @param lastModifierId
     * @return updated packing
     */
    public Packing updatePackedCount(Packing packing, Integer packedCount, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setPackedCount(packedCount);
      return persist(packing);
    }
    
    /**
     * Updates package state
     * 
     * @param packing
     * @param packageState
     * @return updated packing
     */
    public Packing updatePackingState(Packing packing, PackingState packingState, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setPackingState(packingState);
      return persist(packing);
    }
    
    /**
     * Updates time
     *
     * @param time
     * @param lastModifier modifier
     * @return updated packing
     */
    public Packing updateTime(Packing packing, OffsetDateTime time, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setTime(time);
      return persist(packing);
    }
    
    /**
     * Updates product
     * 
     * @param packing
     * @param product
     * @param lastModifierId
     * @return updated packing
     */
    public Packing updateProduct(Packing packing, Product product, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setProduct(product);
      return persist(packing);
    }
    
    /**
     * List all packings that match given criteria
     * 
     * @param firstResult
     * @param maxResults
     * @param productId
     * @param state
     * @param timeBefore
     * @param timeAfter
     * @return packings
     */
    public List<Packing> list(Integer firstResult, Integer maxResults, Product product, PackingState state, OffsetDateTime timeBefore, OffsetDateTime timeAfter) {
      EntityManager entityManager = getEntityManager();
      
      CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Packing> criteria = criteriaBuilder.createQuery(Packing.class);
      Root<Packing> root = criteria.from(Packing.class);
      
      criteria.select(root);
      
      List<Predicate> restrictions = new ArrayList<>();
      
      if (product != null) {
        restrictions.add(criteriaBuilder.equal(root.get(Packing_.product), product));
      }
      
      if (state != null) {
        restrictions.add(criteriaBuilder.equal(root.get(Packing_.packingState), state));
      }
      
      if (timeBefore != null) {
        restrictions.add(criteriaBuilder.lessThanOrEqualTo(root.get(Packing_.time), timeBefore));
      }

      if (timeAfter != null) {
        restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Packing_.time), timeAfter));
      }
      
      criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
      criteria.orderBy(criteriaBuilder.desc(root.get(Packing_.createdAt)));
      
      TypedQuery<Packing> query = entityManager.createQuery(criteria);
      
      if (firstResult != null) {
        query.setFirstResult(firstResult);
      }
      
      if (maxResults != null) {
        query.setMaxResults(maxResults);
      }
      
      return query.getResultList(); 
    }

}

