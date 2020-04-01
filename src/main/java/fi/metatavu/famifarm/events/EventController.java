package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import fi.metatavu.famifarm.persistence.dao.EventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.TableSpreadEvent;
import fi.metatavu.famifarm.persistence.model.WastageEvent;

/**
 * Controller for events
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class EventController {

  @Inject
  private Logger logger;

  @Inject
  private SowingEventController sowingEventController;

  @Inject
  private TableSpreadEventController tableSpreadEventController;

  @Inject
  private CultivationObservationEventController cultivationObservationEventController;

  @Inject
  private HarvestEventController harvestEventController;

  @Inject
  private PlantingEventController plantingEventController;

  @Inject
  private WastageEventController wastageEventController;

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
   * Lists events between dates
   * 
   * @param createdBefore created before
   * @param createdAfter created after
   * @return list of events
   */
  public List<Event> listByCreatedAfterAndCreatedBefore(OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    return eventDAO.listByCreatedAfterAndCreatedBefore(createdBefore, createdAfter);
  }
  
  /**
   * Lists events between dates
   * 
   * @param startBefore start before
   * @param startAfter start after
   * @return list of events
   */
  public List<Event> listByStartTimeAfterAndStartTimeBefore(OffsetDateTime startBefore, OffsetDateTime startAfter) {
    return eventDAO.listByStartTimeAfterAndStartTimeBefore(startBefore, startAfter);
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

  /**
   * Deletes event and related objects using correct controller 
   * 
   * @param event event to be deleted
   */
  public void deleteEvent(Event event) {
    switch (event.getType()) {
      case CULTIVATION_OBSERVATION:
        cultivationObservationEventController.deleteCultivationActionEvent((CultivationObservationEvent) event);
      break;
      case SOWING:
        sowingEventController.deleteSowingEvent((SowingEvent) event);
      break;
      case TABLE_SPREAD:
        tableSpreadEventController.deleteTableSpreadEvent((TableSpreadEvent) event);
      break;
      case HARVEST:
        harvestEventController.deleteHarvestEvent((HarvestEvent) event);
      break;
      case PLANTING:
        plantingEventController.deletePlantingEvent((PlantingEvent) event);
      break;
      case WASTAGE:
        wastageEventController.deleteWastageEvent((WastageEvent) event);
      break;
      default:
        logger.error("Cannot delete event with unknown type {}", event.getType());
      break;
    }
  }

}
