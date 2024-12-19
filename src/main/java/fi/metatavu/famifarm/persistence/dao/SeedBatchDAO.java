package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * DAO class for seed batches
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class SeedBatchDAO extends AbstractDAO<SeedBatch> {

  /**
   * Creates new seed batch
   *
   * @param id id
   * @param code code
   * @param seed seed
   * @param time time
   * @param lastModifier modifier
   * @return created seed
   */
  public SeedBatch create(UUID id, String code, Seed seed, OffsetDateTime time, UUID creatorId, UUID lastModifierId) {
    SeedBatch seedBatch = new SeedBatch();
    seedBatch.setId(id);
    seedBatch.setCode(code);
    seedBatch.setSeed(seed);
    seedBatch.setTime(time);
    seedBatch.setCreatorId(creatorId);
    seedBatch.setLastModifierId(lastModifierId);
    seedBatch.setActive(true);
    return persist(seedBatch);
  }

  /**
   * Updates code
   *
   * @param code code
   * @param lastModifier modifier
   * @return updated seed batch
   */
  public SeedBatch updateCode(SeedBatch seedBatch, String code, UUID lastModifierId) {
    seedBatch.setLastModifierId(lastModifierId);
    seedBatch.setCode(code);
    return persist(seedBatch);
  }

  /**
   * Updates seed
   *
   * @param seed seed
   * @param lastModifier modifier
   * @return updated seed batch
   */
  public SeedBatch updateSeed(SeedBatch seedBatch, Seed seed, UUID lastModifierId) {
    seedBatch.setLastModifierId(lastModifierId);
    seedBatch.setSeed(seed);
    return persist(seedBatch);
  }

  /**
   * Updates time
   *
   * @param time time
   * @param lastModifier modifier
   * @return updated seed batch
   */
  public SeedBatch updateTime(SeedBatch seedBatch, OffsetDateTime time, UUID lastModifierId) {
    seedBatch.setLastModifierId(lastModifierId);
    seedBatch.setTime(time);
    return persist(seedBatch);
  }
  
  /**
   * Lists seed batches
   *
   * @param facility facility
   * @param firstResult first result
   * @param maxResults max results
   * @param active if true or null, list only active seed batches
   * @return seed batches that match the criteria
   */
  public List<SeedBatch> listAll (Facility facility, Integer firstResult, Integer maxResults, Boolean active) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SeedBatch> criteria = criteriaBuilder.createQuery(SeedBatch.class);
    Root<SeedBatch> root = criteria.from(SeedBatch.class);
    
    criteria.select(root);
    criteria.orderBy(criteriaBuilder.desc(root.get(SeedBatch_.createdAt)));
    List<Predicate> restrictions = new ArrayList<>();

    if (facility != null) {
      root.fetch(SeedBatch_.seed, JoinType.LEFT);
      restrictions.add(criteriaBuilder.equal(root.get(SeedBatch_.seed).get(Seed_.facility), facility));
    }

    if (active == null || active){
      restrictions.add(criteriaBuilder.equal(root.get(SeedBatch_.active), true));
    }
    
    criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
    
    TypedQuery<SeedBatch> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }
    
    return query.getResultList(); 
  }
  
  /**
   * Updates seed batch state (active/passive)
   * 
   * @param seedBatch
   * @param active
   * @param lastModifierId
   * @return
   */
  public SeedBatch updateActive(SeedBatch seedBatch, boolean active, UUID lastModifierId) {
    seedBatch.setLastModifierId(lastModifierId);
    seedBatch.setActive(active);
    return persist(seedBatch);
  }

}
