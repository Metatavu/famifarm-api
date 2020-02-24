package fi.metatavu.famifarm.batches;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.persistence.dao.BatchDAO;
import fi.metatavu.famifarm.persistence.dao.EventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.PackingEvent;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.rest.model.BatchPhase;
import fi.metatavu.famifarm.rest.model.PotType;

/**
 * Controller for seed batches
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class BatchController {

  @Inject
  private BatchDAO batchDAO;

  @Inject
  private EventDAO eventDAO;

  @Inject
  private EventController eventController;

  /**
   * Creates new batch
   * 
   * @param product product
   * @param phase phase
   * @param userId userId
   * @return created batch
   */
  public Batch createBatch(Product product, BatchPhase phase, UUID userId) {
    return batchDAO.create(UUID.randomUUID(), product, phase, userId, userId);
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
   * @param product filter by product
   * @param batchPhase phase
   * @param remainingUnitsGreaterThan remaining units greater than (optional)
   * @param remainingUnitsLessThan remaining units less than (optional)
   * @param remainingUnitsEqual remaining units equals (optional)
   * @param createdBefore created before (optional)
   * @param createdAfter created after (optional)
   * @param firstResult first result (optional)
   * @param maxResults max results (optional)
   * @return List of batches
   */
  @SuppressWarnings ("squid:S00107")
  public List<Batch> listBatches(Product product, BatchPhase batchPhase, Integer remainingUnitsGreaterThan, Integer remainingUnitsLessThan, Integer remainingUnitsEqual, OffsetDateTime createdBefore, OffsetDateTime createdAfter, Integer firstResult, Integer maxResults) {
    return batchDAO.list(product, batchPhase, remainingUnitsGreaterThan, remainingUnitsLessThan, remainingUnitsEqual, createdBefore, createdAfter, firstResult, maxResults);
  }
  
  /**
   * Updates batch
   * 
   * @param batch batch
   * @param product product
   * @param lastModifierId lastModifierId
   * @return updated batch
   */
  public Batch updateBatch(Batch batch, Product product, BatchPhase phase, UUID lastModifierId) {
    batchDAO.updateProduct(batch, product, lastModifierId);
    batchDAO.updatePhase(batch, phase, lastModifierId);
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
    eventDAO.listByBatch(batch, null, null).stream().forEach(eventController::deleteEvent);
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
    List<HarvestEvent> harvestEvents = new ArrayList<>();
    List<PlantingEvent> plantingEvents = new ArrayList<>();
    
    for (Event event : eventDAO.listByBatchSortByStartTimeAsc(batch, null, null)) {
      if (event instanceof SowingEvent) {
        sowingEvents.add((SowingEvent) event); 
      } else if (event instanceof WastageEvent) {
        wasteageEvents.add((WastageEvent) event);
      } else if (event instanceof HarvestEvent) {
        harvestEvents.add((HarvestEvent) event);
      } else if (event instanceof PlantingEvent) {
        plantingEvents.add((PlantingEvent) event);
      }
      
      eventDAO.updateRemainingUnits(event, countRemainingUnits(sowingEvents, wasteageEvents, harvestEvents, plantingEvents));  
    }
  }

  /**
   * Updates batch creation date to the same as the earliest sowing event
   * 
   * @param batch batch
   */
  public void refreshCreationDate(Batch batch) {
    Event sowingEvent = eventDAO.listByBatchSortByStartTimeAsc(batch, null, null)
      .stream()
      .filter(event -> event instanceof SowingEvent)
      .findFirst()
      .orElse(null);

    if (sowingEvent == null) {
      return;
    }

    batchDAO.updateCreatedAt(batch, sowingEvent.getStartTime());
  }

  /**
   * Get number of plants in tray depending on pot type
   * 
   * @param potType, potType
   * @return amount
   */
  private int getPotTypeAmount(PotType potType) {
    if (PotType.SMALL == potType) {
      return 54;
    }
    return 35;
  }

  /**
   * Calculates remaining units by given event lists
   * 
   * @param sowingEvents sowing events
   * @param wasteageEvents wastage events
   * @param packingEvents packing events
   * @return count
   */
  private Integer countRemainingUnits(List<SowingEvent> sowingEvents, List<WastageEvent> wasteageEvents, List<HarvestEvent> harvestEvents, List<PlantingEvent> plantingEvents) {
    Integer count = 0;
    Integer gutterHoleCount = getAverageGutterHoleCount(plantingEvents);
    for (SowingEvent event : sowingEvents) {
      count += (getPotTypeAmount(event.getPotType()) * event.getAmount());
    }
    
    for (WastageEvent event : wasteageEvents) {
      count -= event.getAmount();
    }

    for (HarvestEvent event : harvestEvents) {
      count -= (event.getGutterCount() * gutterHoleCount);
    }
    
    return count;
  }
  
  /**
   * Get weighted average gutter hole count
   * 
   * @param events
   * @return weighted average gutter hole count
   */
  private static Integer getAverageGutterHoleCount(List<PlantingEvent> events) {
    Double totalWeightedSize = 0d;
    Double totalGutterCount = 0d;
    
    for (PlantingEvent plantingEvent : events) {

        totalWeightedSize += (plantingEvent.getGutterHoleCount() * plantingEvent.getGutterCount());
        totalGutterCount += plantingEvent.getGutterCount();

    }

    if (totalWeightedSize == 0 || totalGutterCount == 0) {
      return 0;
    }

    return (int) Math.round(totalWeightedSize / totalGutterCount);
  }

}
