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

import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.Facility;
import fi.metatavu.famifarm.rest.model.PackingState;
import fi.metatavu.famifarm.rest.model.PackingType;

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
     * @param creatorId creator id
     * @param facility required facility param
     * @param product product
     * @param id id
     * @param packageSize package size
     * @param packedCount packed count
     * @param packingState packing status
     * @param time time of packing
     * @return packing
     */
    public Packing create(UUID creatorId, Facility facility, Product product, UUID id, PackageSize packageSize, Integer packedCount, PackingState packingState, OffsetDateTime time, Campaign campaign, PackingType type) {
      Packing packing = new Packing();
      packing.setCreatorId(creatorId);
      packing.setId(id);
      packing.setFacility(facility);
      packing.setProduct(product);
      packing.setPackingState(packingState);
      packing.setCreatorId(creatorId);
      packing.setLastModifierId(creatorId);
      packing.setPackageSize(packageSize);
      packing.setPackedCount(packedCount);
      packing.setTime(time);
      packing.setCampaign(campaign);
      packing.setType(type);
      return persist(packing);
    }

  /**
   * Updates packing campaign
   *
   * @param packing packing to be updated
   * @param campaign new campaign
   * @param lastModifierId modifier id
   *
   * @return update packing
   */
    public Packing updateCampaign(Packing packing, Campaign campaign, UUID lastModifierId) {
      packing.setLastModifierId(lastModifierId);
      packing.setCampaign(campaign);
      return persist(packing);
    }

  /**
   * Updates packing type
   *
   * @param packing packing to be updated
   * @param type packing type
   * @param lastModifierId modifier id
   *
   * @return update packing
   */
  public Packing updateType(Packing packing, PackingType type, UUID lastModifierId) {
    packing.setLastModifierId(lastModifierId);
    packing.setType(type);
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
     * @param facility required parameter
     * @param product
     * @param campaign
     * @param state
     * @param timeBefore
     * @param timeAfter
     * @return packings
     */
    public List<Packing> list(Integer firstResult, Integer maxResults, Facility facility, Product product, Campaign campaign, PackingState state, OffsetDateTime timeBefore, OffsetDateTime timeAfter) {
      EntityManager entityManager = getEntityManager();
      
      CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Packing> criteria = criteriaBuilder.createQuery(Packing.class);
      Root<Packing> root = criteria.from(Packing.class);
      
      criteria.select(root);
      
      List<Predicate> restrictions = new ArrayList<>();
      restrictions.add(criteriaBuilder.equal(root.get(Packing_.facility), facility));
      if (product != null) {
        restrictions.add(criteriaBuilder.equal(root.get(Packing_.product), product));
      }

      if (campaign != null) {
        restrictions.add(criteriaBuilder.equal(root.get(Packing_.campaign), campaign));
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

