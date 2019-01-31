package fi.metatavu.famifarm.events;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.EventDAO;
import fi.metatavu.famifarm.persistence.model.Event;

/**
 * Controller for events
 * 
 * @author Antti Leppä
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
   * @param firstResult first result
   * @param maxResults max results
   * @return list events
   */
  public List<Event> listEvents(Integer firstResult, Integer maxResults) {
    return eventDAO.listAll(firstResult, maxResults);
  }

  /**
   * Deletes an event
   * 
   * @param event event
   */
  public void deleteEvent(Event event) {
    eventDAO.delete(event);
  }

}
