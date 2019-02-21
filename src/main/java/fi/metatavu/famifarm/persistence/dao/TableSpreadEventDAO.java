package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Batch;
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
   * @param tableCount tableCount
   * @param location location
   * @param batch batch
   * @param startTime event start time
   * @param endTime event end time
   * @param remainingUnits remaining units
   * @param creatorId creator id
   * @param lastModifierId last modifier id
   * @return
   */
  @SuppressWarnings ("squid:S00107")
  public TableSpreadEvent create(UUID id, Integer tableCount, String location, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer remainingUnits, String additionalInformation, UUID creatorId, UUID lastModifierId) {
    TableSpreadEvent tableSpreadEvent = new TableSpreadEvent();
    tableSpreadEvent.setTableCount(tableCount);
    tableSpreadEvent.setRemainingUnits(remainingUnits);
    tableSpreadEvent.setLocation(location);
    tableSpreadEvent.setId(id);
    tableSpreadEvent.setBatch(batch);
    tableSpreadEvent.setStartTime(startTime);
    tableSpreadEvent.setEndTime(endTime);
    tableSpreadEvent.setId(id);
    tableSpreadEvent.setCreatorId(creatorId);
    tableSpreadEvent.setLastModifierId(lastModifierId);
    tableSpreadEvent.setAdditionalInformation(additionalInformation);
    return persist(tableSpreadEvent);
  }

  /**
   * Updates tableCount
   *
   * @param tableCount tableCount
   * @param lastModifier modifier
   * @return updated tableSpreadEvent
   */
  public TableSpreadEvent updateTableCount(TableSpreadEvent tableSpreadEvent, Integer tableCount, UUID lastModifierId) {
    tableSpreadEvent.setLastModifierId(lastModifierId);
    tableSpreadEvent.setTableCount(tableCount);
    return persist(tableSpreadEvent);
  }

  /**
   * Updates location
   *
   * @param location location
   * @param lastModifier modifier
   * @return updated tableSpreadEvent
   */
  public TableSpreadEvent updateLocation(TableSpreadEvent tableSpreadEvent, String location, UUID lastModifierId) {
    tableSpreadEvent.setLastModifierId(lastModifierId);
    tableSpreadEvent.setLocation(location);
    return persist(tableSpreadEvent);
  }

}
