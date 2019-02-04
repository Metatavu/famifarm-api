package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.TableSpreadEventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.TableSpreadEvent;

/**
 * Controller for sowing events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class TableSpreadEventController {
  
  @Inject
  private TableSpreadEventDAO tableSpreadEventDAO;  
  
  /**
   * Update tableSpreadEvent
   *
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param tableCount tableCount
   * @param location location
   * @param modifier modifier
   * @return updated tableSpreadEvent
   */
  public TableSpreadEvent createTableSpreadEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer tableCount, String location, UUID creatorId) {
    return tableSpreadEventDAO.create(UUID.randomUUID(), tableCount, location, batch, startTime, endTime, creatorId, creatorId);
  }
  
  /**
   * Returns sowing event by id
   * 
   * @param tableSpreadEventId id
   * @return sowing event or null if not found
   */
  public TableSpreadEvent findTableSpreadEventById(UUID tableSpreadEventId) {
    return tableSpreadEventDAO.findById(tableSpreadEventId);
  }
  
  /**
   * Returns list of sowing events
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of sowing events
   */
  public List<TableSpreadEvent> listTableSpreadEvents(Integer firstResult, Integer maxResults) {
    return tableSpreadEventDAO.listAll(firstResult, maxResults);
  }

  /**
   * Update tableSpreadEvent
   *
   * @param tableSpreadEvent sowing event
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param tableCount tableCount
   * @param location location
   * @param modifier modifier
   * @return updated tableSpreadEvent
   */
  public TableSpreadEvent updateTableSpreadEvent(TableSpreadEvent tableSpreadEvent, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer tableCount, String location, UUID modifier) {
    tableSpreadEventDAO.updateBatch(tableSpreadEvent, batch, modifier);
    tableSpreadEventDAO.updateStartTime(tableSpreadEvent, startTime, modifier);
    tableSpreadEventDAO.updateEndTime(tableSpreadEvent, endTime, modifier);
    tableSpreadEventDAO.updateTableCount(tableSpreadEvent, tableCount, modifier);
    tableSpreadEventDAO.updateLocation(tableSpreadEvent, location, modifier);
    return tableSpreadEvent;
  }
  
  /**
   * Deletes an sowing event
   * 
   * @param tableSpreadEvent sowing event to be deleted
   */
  public void deleteTableSpreadEvent(TableSpreadEvent tableSpreadEvent) {
    tableSpreadEventDAO.delete(tableSpreadEvent);
  }

}
