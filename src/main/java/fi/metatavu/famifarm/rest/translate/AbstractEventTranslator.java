package fi.metatavu.famifarm.rest.translate;

import fi.metatavu.famifarm.rest.model.Event;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Abstract translator class for events
 * 
 * @author Antti Leppä
 *
 * @param <R> response data type
 * @param <J> JPA entity
 */
public abstract class AbstractEventTranslator <R, J extends fi.metatavu.famifarm.persistence.model.Event> extends AbstractTranslator {
  
  /**
   * Translates event from JPA into REST entity
   * 
   * @param event event
   * @return translated event
   */
  public Event translateEvent(J event) {
    if (event == null) {
      return null;
    }
    
    Event result = new Event();
    result.setId(event.getId());
    result.setBatchId(event.getBatch() != null ? event.getBatch().getId() : null);
    result.setData(translateEventData(event));
    result.setEndTime(event.getEndTime());
    result.setStartTime(event.getStartTime());
    result.setType(getType());
    result.setUserId(event.getCreatorId());
    result.setAdditionalInformation(event.getAdditionalInformation());

    return result;
  }
  
  /**
   * Returns event type
   * 
   * @return event type
   */
  protected abstract EventType getType();

  /**
   * Translates event data
   * 
   * @param event event
   * @return event data
   */
  protected abstract R translateEventData(J event);

}
