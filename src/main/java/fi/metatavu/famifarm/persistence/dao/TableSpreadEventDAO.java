package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.TableSpreadEvent;

/**
 * DAO class for TableSpreadEvent
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class TableSpreadEventDAO extends AbstractEventDAO<TableSpreadEvent> {

  /**
   * Creates new tableSpreadEvent
   * 
   * @param id id
   * @param trayCount trayCount
   * @param product product
   * @param startTime event start time
   * @param endTime event end time
   * @param remainingUnits remaining units
   * @param creatorId creator id
   * @param lastModifierId last modifier id
   * @return
   */
  @SuppressWarnings ("squid:S00107")
  public TableSpreadEvent create(UUID id, Integer trayCount, Product product, OffsetDateTime startTime, OffsetDateTime endTime, Integer remainingUnits, String additionalInformation, UUID creatorId, UUID lastModifierId) {
    TableSpreadEvent tableSpreadEvent = new TableSpreadEvent();
    tableSpreadEvent.setTrayCount(trayCount);
    tableSpreadEvent.setRemainingUnits(remainingUnits);
    tableSpreadEvent.setId(id);
    tableSpreadEvent.setProduct(product);
    tableSpreadEvent.setStartTime(startTime);
    tableSpreadEvent.setEndTime(endTime);
    tableSpreadEvent.setId(id);
    tableSpreadEvent.setCreatorId(creatorId);
    tableSpreadEvent.setLastModifierId(lastModifierId);
    tableSpreadEvent.setAdditionalInformation(additionalInformation);
    return persist(tableSpreadEvent);
  }

  /**
   * Updates trayCount
   *
   * @param trayCount trayCount
   * @param lastModifier modifier
   * @return updated tableSpreadEvent
   */
  public TableSpreadEvent updateTrayCount(TableSpreadEvent tableSpreadEvent, Integer trayCount, UUID lastModifierId) {
    tableSpreadEvent.setLastModifierId(lastModifierId);
    tableSpreadEvent.setTrayCount(trayCount);
    return persist(tableSpreadEvent);
  }

}
