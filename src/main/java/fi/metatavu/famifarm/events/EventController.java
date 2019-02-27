package fi.metatavu.famifarm.events;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.EventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;

/**
 * Controller for events
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class EventController {
  
  @Inject
  private EventDAO eventDAO;
  
  /**
   * Finds event by id
   * 
   * @param id id
   * @return found event or null if not found
   */
  public Event findEventById(UUID id) {
    return eventDAO.findById(id);
  }
  
  /**
   * Lists events
   * 
   * @param batch filter results by batch (optional)
   * @param firstResult first result
   * @param maxResults max results
   * @return list events
   */
  public List<Event> listEvents(Batch batch, Integer firstResult, Integer maxResults) {
    if (batch != null) {
      return eventDAO.listByBatch(batch, firstResult, maxResults);
    }

    return eventDAO.listAll(firstResult, maxResults);
  }
  
  /**
   * List by batch and order by start time
   * 
   * @param batch batch
   * @return list of events
   */
  public List<Event> listByBatchSortByStartTimeAsc(Batch batch, Integer firstResult, Integer maxResults) {
    return eventDAO.listByBatchSortByStartTimeAsc(batch, firstResult, maxResults);
  }

  /**
   * Updates remaining units count
   * 
   * @param event event
   * @param remainingUnits remaining units
   * @return updated event
   */
  public Event updateRemainingUnits(Event event, Integer remainingUnits) {
    return eventDAO.updateRemainingUnits(event, remainingUnits);
  }
  
  /**
   * Finds last event in a batch
   * 
   * @param batch batch
   * @return found event or null if not found
   */
  public Event findLastEventByBatch(Batch batch) {
    List<Event> events = eventDAO.listByBatchSortByStartTimeDesc(batch, null, 1);
    if (events.isEmpty()) {
      return null;
    }
    
    return events.get(0);
  }

}
