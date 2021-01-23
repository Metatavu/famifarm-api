package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.TableSpreadEventDAO;
import fi.metatavu.famifarm.persistence.model.Product;
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
   * @param product product
   * @param startTime startTime
   * @param endTime endTime
   * @param trayCount trayCount
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated tableSpreadEvent
   */
  @SuppressWarnings ("squid:S00107")
  public TableSpreadEvent createTableSpreadEvent(Product product, OffsetDateTime startTime, OffsetDateTime endTime, Integer trayCount, String additionalInformation, UUID creatorId) {
    return tableSpreadEventDAO.create(UUID.randomUUID(), trayCount, product, startTime, endTime, 0, additionalInformation, creatorId, creatorId);
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
   * @param product product
   * @param startTime startTime
   * @param endTime endTime
   * @param trayCount trayCount
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated tableSpreadEvent
   */
  @SuppressWarnings ("squid:S00107")
  public TableSpreadEvent updateTableSpreadEvent(TableSpreadEvent tableSpreadEvent, Product product, OffsetDateTime startTime, OffsetDateTime endTime, Integer trayCount, String additionalInformation, UUID modifier) {
    tableSpreadEventDAO.updateProduct(tableSpreadEvent, product, modifier);
    tableSpreadEventDAO.updateStartTime(tableSpreadEvent, startTime, modifier);
    tableSpreadEventDAO.updateEndTime(tableSpreadEvent, endTime, modifier);
    tableSpreadEventDAO.updateTrayCount(tableSpreadEvent, trayCount, modifier);
    tableSpreadEventDAO.updateAdditionalInformation(tableSpreadEvent, additionalInformation, modifier);
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
