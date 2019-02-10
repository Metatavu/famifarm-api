package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.persistence.model.WastageReason;

/**
 * DAO for wastage events
 * 
 * @author Heikki Kurhinen
 */
public class WastageEventDAO extends AbstractEventDAO<WastageEvent> {

  /**
   * Creates new wastage event
   * 
   * @param id id
   * @param amount amount
   * @param batch batch
   * @param wastageReason wastage reason
   * @param description description
   * @param startTime start time
   * @param endTime end time
   * @param creatorId creator id
   * @param lastModifierId last modfier id
   * @return created wastage event
   */
  @SuppressWarnings ("squid:S00107")
  public WastageEvent create(UUID id, Integer amount, Batch batch, WastageReason wastageReason, String description, OffsetDateTime startTime, OffsetDateTime endTime, UUID creatorId, UUID lastModifierId) {
    WastageEvent wastageEvent = new WastageEvent();
    wastageEvent.setAmount(amount);
    wastageEvent.setBatch(batch);
    wastageEvent.setWastageReason(wastageReason);
    wastageEvent.setDescription(description);
    wastageEvent.setId(id);
    wastageEvent.setStartTime(startTime);
    wastageEvent.setEndTime(endTime);
    wastageEvent.setCreatorId(creatorId);
    wastageEvent.setLastModifierId(lastModifierId);

    return persist(wastageEvent);
  }

  /**
   * Updates amount
   * 
   * @param wastageEvent wastage event to update
   * @param amount new amount
   * @param lastModifierId last modifier id
   * @return updated wastage event
   */
  public WastageEvent updateAmount(WastageEvent wastageEvent, Integer amount, UUID lastModifierId) {
    wastageEvent.setAmount(amount);
    wastageEvent.setLastModifierId(lastModifierId);
    return persist(wastageEvent);
  }

  /**
   * Updates wastage reason
   * 
   * @param wastageEvent wastage event to update
   * @param wastageReason new wastage reason
   * @param lastModifierId last modifier id
   * @return updated wastage event
   */
  public WastageEvent updateWastageReason(WastageEvent wastageEvent, WastageReason wastageReason, UUID lastModifierId) {
    wastageEvent.setWastageReason(wastageReason);
    wastageEvent.setLastModifierId(lastModifierId);
    return persist(wastageEvent);
  }

  /**
   * Updates description
   * 
   * @param wastageEvent wastage event to update
   * @param description new description
   * @param lastModifierId last modifier id
   * @return updated wastage event
   */
  public WastageEvent updateDescription(WastageEvent wastageEvent, String description, UUID lastModifierId) {
    wastageEvent.setDescription(description);
    wastageEvent.setLastModifierId(lastModifierId);
    return persist(wastageEvent);
  }
}