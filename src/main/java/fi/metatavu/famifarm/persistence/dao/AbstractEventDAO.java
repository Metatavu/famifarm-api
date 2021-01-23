package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import fi.metatavu.famifarm.persistence.model.Product;
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
   * Updates product
   *
   * @param product product
   * @param lastModifier modifier
   * @return updated event
   */
  public T updateProduct(T event, Product product, UUID lastModifierId) {
    event.setLastModifierId(lastModifierId);
    event.setProduct(product);
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

  /**
   * Updates remaining units count
   * 
   * @param event event
   * @param remainingUnits remaining units
   * @return updated event
   */
  public T updateRemainingUnits(T event, Integer remainingUnits) {
    event.setRemainingUnits(remainingUnits);
    return persist(event);
  }

  /**
   * Updates event's additional information
   * 
   * @param event event
   * @param additionalInformation additional information
   * @return updated event
   */
  public T updateAdditionalInformation(T event, String additionalInformation, UUID lastModifierId) {
    event.setAdditionalInformation(additionalInformation);
    event.setLastModifierId(lastModifierId);
    return persist(event);
  }
  
}
