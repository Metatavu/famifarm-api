package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.SowingEventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.SeedBatch;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.rest.model.CellType;

/**
 * Controller for sowing events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SowingEventController {
  
  @Inject
  private SowingEventDAO sowingEventDAO;  
  
  /**
   * Update sowingEvent
   *
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param gutterNumber gutterNumber
   * @param seedBatch seedBatch
   * @param cellType cellType
   * @param amount amount
   * @param modifier modifier
   * @return updated sowingEvent
   */
  public SowingEvent createSowingEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Integer gutterNumber, SeedBatch seedBatch, CellType cellType, Double amount, UUID creatorId) {
    return sowingEventDAO.create(UUID.randomUUID(), batch, startTime, endTime, productionLine, gutterNumber, seedBatch, cellType, amount, creatorId, creatorId);
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
   * Update sowingEvent
   *
   * @param sowingEvent sowing event
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param productionLine productionLine
   * @param gutterNumber gutterNumber
   * @param seedBatch seedBatch
   * @param cellType cellType
   * @param amount amount
   * @param modifier modifier
   * @return updated sowingEvent
   */
  public SowingEvent updateSowingEvent(SowingEvent sowingEvent, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Integer gutterNumber, SeedBatch seedBatch, CellType cellType, Double amount, UUID modifier) {
    sowingEventDAO.updateBatch(sowingEvent, batch, modifier);
    sowingEventDAO.updateStartTime(sowingEvent, startTime, modifier);
    sowingEventDAO.updateEndTime(sowingEvent, endTime, modifier);
    sowingEventDAO.updateProductionLine(sowingEvent, productionLine, modifier);
    sowingEventDAO.updateGutterNumber(sowingEvent, gutterNumber, modifier);
    sowingEventDAO.updateSeedBatch(sowingEvent, seedBatch, modifier);
    sowingEventDAO.updateCellType(sowingEvent, cellType, modifier);
    sowingEventDAO.updateAmount(sowingEvent, amount, modifier);
    return sowingEvent;
  }
  
  /**
   * Deletes an sowing event
   * 
   * @param sowingEvent sowing event to be deleted
   */
  public void deleteSowingEvent(SowingEvent sowingEvent) {
    sowingEventDAO.delete(sowingEvent);
  }

}
