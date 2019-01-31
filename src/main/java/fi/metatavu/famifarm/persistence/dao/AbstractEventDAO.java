package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;

/**
 * Abstract base class for event DAO classes
 * 
 * @author Antti Leppä
 *
 * @param <T> event type
 */
public abstract class AbstractEventDAO <T extends Event> extends AbstractDAO<T> {

  /**
   * Updates batch
   *
   * @param batch batch
   * @param lastModifier modifier
   * @return updated event
   */
  public T updateBatch(T event, Batch batch, UUID lastModifierId) {
    event.setLastModifierId(lastModifierId);
    event.setBatch(batch);
    return persist(event);
  }

  /**
   * Updates startTime
   *
   * @param startTime startTime
   * @param lastModifier modifier
   * @return updated event
   */
  public T updateStartTime(T event, OffsetDateTime startTime, UUID lastModifierId) {
    event.setLastModifierId(lastModifierId);
    event.setStartTime(startTime);
    return persist(event);
  }

  /**
   * Updates endTime
   *
   * @param endTime endTime
   * @param lastModifier modifier
   * @return updated event
   */
  public T updateEndTime(T event, OffsetDateTime endTime, UUID lastModifierId) {
    event.setLastModifierId(lastModifierId);
    event.setEndTime(endTime);
    return persist(event);
  }
  
}
