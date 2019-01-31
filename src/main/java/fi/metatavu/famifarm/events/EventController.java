package fi.metatavu.famifarm.events;

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
   * Deletes an event
   * 
   * @param event event
   */
  public void deleteEvent(Event event) {
    eventDAO.delete(event);
  }

}
