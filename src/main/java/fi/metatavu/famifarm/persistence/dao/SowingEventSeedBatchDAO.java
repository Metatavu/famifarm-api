package fi.metatavu.famifarm.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.SeedBatch;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.SowingEventSeedBatch;
import fi.metatavu.famifarm.persistence.model.SowingEventSeedBatch_;

/**
 * DAO class for sowing event seed batch
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SowingEventSeedBatchDAO extends AbstractDAO<SowingEventSeedBatch> {

  /**
   * Creates new sowing event seed batch
   *
   * @param sowingEvent sowing event
   * @param seedBatch seed batch
   * @param lastModifier modifier
   */
  public SowingEventSeedBatch create(UUID id, SowingEvent sowingEvent, SeedBatch seedBatch) {
    SowingEventSeedBatch sowingEventSeedBatch = new SowingEventSeedBatch();
    sowingEventSeedBatch.setSowingEvent(sowingEvent);
    sowingEventSeedBatch.setSeedBatch(seedBatch);
    sowingEventSeedBatch.setId(id);
    return persist(sowingEventSeedBatch);
  }

  /**
   * Lists sowing event seed batches by event
   * 
   * @param sowingEvent sowing event
   * @return sowing event seed batches
   */
  public List<SowingEventSeedBatch> listBySowingEvent(SowingEvent sowingEvent) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SowingEventSeedBatch> criteria = criteriaBuilder.createQuery(SowingEventSeedBatch.class);
    Root<SowingEventSeedBatch> root = criteria.from(SowingEventSeedBatch.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(SowingEventSeedBatch_.sowingEvent), sowingEvent));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists seed batches by event
   * 
   * @param sowingEvent sowing event
   * @return seed batches
   */
  public List<SeedBatch> listSeedBatchesBySowingEvent(SowingEvent sowingEvent) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SeedBatch> criteria = criteriaBuilder.createQuery(SeedBatch.class);
    Root<SowingEventSeedBatch> root = criteria.from(SowingEventSeedBatch.class);
    criteria.select(root.get(SowingEventSeedBatch_.seedBatch));
    criteria.where(criteriaBuilder.equal(root.get(SowingEventSeedBatch_.sowingEvent), sowingEvent));
    
    return entityManager.createQuery(criteria).getResultList();
  }

}
