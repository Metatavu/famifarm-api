package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.BatchDAO;
import fi.metatavu.famifarm.persistence.dao.SowingEventDAO;
import fi.metatavu.famifarm.persistence.dao.SowingEventSeedBatchDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.SeedBatch;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.SowingEventSeedBatch;
import fi.metatavu.famifarm.rest.model.PotType;

/**
 * Controller for sowing events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SowingEventController {

  @Inject
  private BatchDAO batchDAO;
  
  @Inject
  private SowingEventDAO sowingEventDAO;

  @Inject
  private SowingEventSeedBatchDAO sowingEventSeedBatchDAO;
  
  /**
   * Update sowingEvent
   *
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param potType pot type
   * @param amount amount
   * @param additionalInformation additional information
   * @param creatorId creator id
   * @return updated sowingEvent
   */
  @SuppressWarnings ("squid:S00107")
  public SowingEvent createSowingEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Collection<SeedBatch> seedBatches, PotType potType, Integer amount, String additionalInformation, UUID creatorId) {
    SowingEvent sowingEvent = sowingEventDAO.create(UUID.randomUUID(), batch, startTime, endTime, productionLine, potType, amount, 0, additionalInformation, creatorId, creatorId);
    int scaledDifference = getPotTypeAmount(potType) * amount;
    batchDAO.updateTotalSowed(batch, batch.getTotalSowed() + scaledDifference);
    setSowingEventSeedBatches(sowingEvent, seedBatches);
    return sowingEvent;
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
   * Returns sowing event by id
   * 
   * @param sowingEventId id
   * @return sowing event or null if not found
   */
  public SowingEvent findSowingEventById(UUID sowingEventId) {
    return sowingEventDAO.findById(sowingEventId);
  }
  
  /**
   * Returns list of sowing events
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of sowing events
   */
  public List<SowingEvent> listSowingEvents(Integer firstResult, Integer maxResults) {
    return sowingEventDAO.listAll(firstResult, maxResults);
  }
  
  /**
   * Lists sowing events by batch 
   * 
   * @param batch batch
   * @return sowing events by batch 
   */
  public List<SowingEvent> listBatchSowingEvents(Batch batch) {
    return sowingEventDAO.listByBatch(batch);
  }

  /**
   * Update sowingEvent
   *
   * @param sowingEvent sowing event
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param seedBatches seedBatches
   * @param potType pot type
   * @param amount amount
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated sowingEvent
   */
  @SuppressWarnings ("squid:S00107")
  public SowingEvent updateSowingEvent(SowingEvent sowingEvent, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Collection<SeedBatch> seedBatches, PotType potType, Integer amount, String additionalInformation, UUID modifier) {
    sowingEventDAO.updateBatch(sowingEvent, batch, modifier);
    sowingEventDAO.updateStartTime(sowingEvent, startTime, modifier);
    sowingEventDAO.updateEndTime(sowingEvent, endTime, modifier);
    sowingEventDAO.updateProductionLine(sowingEvent, productionLine, modifier);
    sowingEventDAO.updatePotType(sowingEvent, potType, modifier);
    sowingEventDAO.updateAmount(sowingEvent, amount, modifier);
    sowingEventDAO.updateAdditionalInformation(sowingEvent, additionalInformation, modifier);

    int difference = amount - sowingEvent.getAmount();
    int scaledDifference = getPotTypeAmount(potType) * difference;
    batchDAO.updateTotalSowed(batch, batch.getTotalSowed() + scaledDifference);

    setSowingEventSeedBatches(sowingEvent, seedBatches);
    
    return sowingEvent;
  }
  
  /**
   * Deletes an sowing event
   * 
   * @param sowingEvent sowing event to be deleted
   */
  public void deleteSowingEvent(SowingEvent sowingEvent) {
    batchDAO.listByActiveBatch(sowingEvent).stream().forEach(batch -> batchDAO.updateActiveEvent(batch, null));
    sowingEventSeedBatchDAO.listBySowingEvent(sowingEvent).forEach(sowingEventSeedBatchDAO::delete);
    sowingEventDAO.delete(sowingEvent);
  }

  /**
   * Lists seed batches by event
   * 
   * @param sowingEvent sowing event
   * @return seed batches
   */
  public List<SeedBatch> listSowingEventSeedBatches(SowingEvent sowingEvent) {
    return sowingEventSeedBatchDAO.listSeedBatchesBySowingEvent(sowingEvent);
  }
  
  /**
   * Updates sowing event seed batches into the database
   * 
   * @param sowingEvent sowing event
   * @param seedBatches seed batches
   */
  private void setSowingEventSeedBatches(SowingEvent sowingEvent, Collection<SeedBatch> seedBatches) {
    Map<UUID, SowingEventSeedBatch> seedBatchMap = sowingEventSeedBatchDAO.listBySowingEvent(sowingEvent).stream()
      .collect(Collectors.toMap(sowingEventSeedBatch -> sowingEventSeedBatch.getSeedBatch().getId(), sowingEventSeedBatch -> sowingEventSeedBatch));  
     
    Set<UUID> existingSeedBatchIds = seedBatchMap.keySet();
    
    for (SeedBatch seedBatch : seedBatches) {
      if (!existingSeedBatchIds.remove(seedBatch.getId())) {
        sowingEventSeedBatchDAO.create(UUID.randomUUID(), sowingEvent, seedBatch);
      }
    }
    
    for (UUID removedSeedBatchId : existingSeedBatchIds) {
      sowingEventSeedBatchDAO.delete(seedBatchMap.get(removedSeedBatchId));
    }
  }

}
