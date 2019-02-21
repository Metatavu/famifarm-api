package fi.metatavu.famifarm.batches;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.BatchDAO;
import fi.metatavu.famifarm.persistence.dao.EventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.PackingEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.WastageEvent;

/**
 * Controller for seed batches
 * 
 * @author Ville Koivukangas
 *
 */
public class BatchController {

  @Inject
  private BatchDAO batchDAO;

  @Inject
  private EventDAO eventDAO;

  /**
   * Creates new batch
   * 
   * @param product product
   * @param userId userId
   * @return created batch
   */
  public Batch createBatch(Product product, UUID userId) {
    return batchDAO.create(UUID.randomUUID(), product, userId, userId);
  }

  /**
   * Finds batch by id
   * 
   * @param batchId batchId
   * @return batch
   */
  public Batch findBatch(UUID batchId) {
    return batchDAO.findById(batchId);
  }

  /**
   * Lists batches
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @param createdBefore created before
   * @param createdAfter created after
   * @return list of batches
   */
  public List<Batch> listBatches(Integer firstResult, Integer maxResults, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    return batchDAO.listByCreatedTimes(firstResult, maxResults, createdBefore, createdAfter);
  }

  /**
   * Lists closed batches
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @param createdBefore created before
   * @param createdAfter created after
   * @return list of batches
   */
  public List<Batch> listClosedBatches(Integer firstResult, Integer maxResults, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    return batchDAO.listByRemainingUnitsEquals(0, firstResult, maxResults, createdBefore, createdAfter);
  }

  /**
   * Lists open batches
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @param createdBefore created before
   * @param createdAfter created after
   * @return list of batches
   */
  public List<Batch> listOpenBatches(Integer firstResult, Integer maxResults, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    return batchDAO.listByRemainingUnitsGreaterThan(0, firstResult, maxResults, createdBefore, createdAfter);
  }

  /**
   * Lists negative batches
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @param createdBefore created before
   * @param createdAfter created after
   * @return list of batches
   */
  public List<Batch> listNegativeBatches(Integer firstResult, Integer maxResults, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    return batchDAO.listByRemainingUnitsLessThan(0, firstResult, maxResults, createdBefore, createdAfter);
  }
  
  /**
   * Updates batch
   * 
   * @param batch batch
   * @param product product
   * @param lastModifierId lastModifierId
   * @return updated batch
   */
  public Batch updateBatch(Batch batch, Product product, UUID lastModifierId) {
    batchDAO.updateProduct(batch, product, lastModifierId);
    return batch;
  }

  /**
   * Update batch active event
   * 
   * @param batch batch
   * @param activeEvent active event
   * @return updated batch
   */
  public Batch updateBatchActiveEvent(Batch batch, Event activeEvent) {
    batchDAO.updateActiveEvent(batch, activeEvent);
    return batch;
  }

  /**
   * Deletes batch
   * 
   * @param batch batch to be deleted
   */
  public void deleteBatch(Batch batch) {
    batchDAO.delete(batch);
  }

  /**
   * Updates remaining units within a batch
   * 
   * @param batch batch
   */
  public void updateRemainingUnits(Batch batch) {
    List<SowingEvent> sowingEvents = new ArrayList<>();
    List<WastageEvent> wasteageEvents = new ArrayList<>();
    List<PackingEvent> packingEvents = new ArrayList<>();
    
    for (Event event : eventDAO.listByBatchSortByStartTimeAsc(batch, null, null)) {
      if (event instanceof SowingEvent) {
        sowingEvents.add((SowingEvent) event); 
      } else if (event instanceof WastageEvent) {
        wasteageEvents.add((WastageEvent) event);
      } else if (event instanceof PackingEvent) {
        packingEvents.add((PackingEvent) event);
      }
      
      eventDAO.updateRemainingUnits(event, countRemainingUnits(sowingEvents, wasteageEvents, packingEvents));  
    }
  }
  
  /**
   * Calculates remaining units by given event lists
   * 
   * @param sowingEvents sowing events
   * @param wasteageEvents wastage events
   * @param packingEvents packing events
   * @return count
   */
  private Integer countRemainingUnits(List<SowingEvent> sowingEvents, List<WastageEvent> wasteageEvents, List<PackingEvent> packingEvents) {
    Integer count = 0;
    
    for (SowingEvent event : sowingEvents) {
      count += event.getAmount();
    }
    
    for (WastageEvent event : wasteageEvents) {
      count -= event.getAmount();
    }

    for (PackingEvent event : packingEvents) {
      count -= event.getPackedAmount();
    }
    
    return count;
  }

}
