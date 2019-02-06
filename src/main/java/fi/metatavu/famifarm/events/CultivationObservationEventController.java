package fi.metatavu.famifarm.events;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.CultivationObservationEventActionDAO;
import fi.metatavu.famifarm.persistence.dao.CultivationObservationEventDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEventAction;
import fi.metatavu.famifarm.persistence.model.PerformedCultivationAction;

/**
 * Controller for cultivation observation events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class CultivationObservationEventController {
  
  @Inject
  private CultivationObservationEventDAO cultivationObservationEventDAO;  
  
  @Inject
  private CultivationObservationEventActionDAO cultivationObservationEventActionDAO;  
  
  /**
   * Create cultivationActionEvent
   *
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param weight weight
   * @param luminance luminance
   * @param pests pests
   * @param modifier modifier
   * @return created cultivationActionEvent
   */
  public CultivationObservationEvent createCultivationActionEvent(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Double weight, Double luminance, String pests, List<PerformedCultivationAction> actions, UUID creatorId) {
    CultivationObservationEvent event = cultivationObservationEventDAO.create(UUID.randomUUID(), weight, luminance, pests, batch, startTime, endTime, creatorId, creatorId);
    
    if (actions != null) {
      actions.stream().forEach(action -> cultivationObservationEventActionDAO.create(UUID.randomUUID(), event, action));
    }
    
    return event;
  }
  
  /**
   * Returns event by id
   * 
   * @param cultivationActionEventId id
   * @return event or null if not found
   */
  public CultivationObservationEvent findCultivationActionEventById(UUID cultivationActionEventId) {
    return cultivationObservationEventDAO.findById(cultivationActionEventId);
  }
  
  /**
   * Returns list of events
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of events
   */
  public List<CultivationObservationEvent> listCultivationActionEvents(Integer firstResult, Integer maxResults) {
    return cultivationObservationEventDAO.listAll(firstResult, maxResults);
  }

  /**
   * Update cultivationActionEvent
   *
   * @param cultivationActionEvent event
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @param weight weight
   * @param luminance luminance
   * @param pests pests
   * @param modifier modifier
   * @return updated cultivationActionEvent
   */
  public CultivationObservationEvent updateCultivationActionEvent(CultivationObservationEvent cultivationActionEvent, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Double weight, Double luminance, String pests, List<PerformedCultivationAction> actions, UUID modifier) {
    cultivationObservationEventDAO.updateBatch(cultivationActionEvent, batch, modifier);
    cultivationObservationEventDAO.updateStartTime(cultivationActionEvent, startTime, modifier);
    cultivationObservationEventDAO.updateEndTime(cultivationActionEvent, endTime, modifier);
    cultivationObservationEventDAO.updateWeight(cultivationActionEvent, weight, modifier);
    cultivationObservationEventDAO.updateLuminance(cultivationActionEvent, luminance, modifier);
    cultivationObservationEventDAO.updatePests(cultivationActionEvent, pests, modifier);
    
    List<CultivationObservationEventAction> eventActions = cultivationObservationEventActionDAO.listByEvent(cultivationActionEvent);

    Map<UUID, CultivationObservationEventAction> eventActionMap = eventActions.stream()
      .collect(Collectors.toMap(eventAction -> eventAction.getAction().getId(), eventAction -> eventAction));  
    
    Set<UUID> existingIds = new HashSet<>(eventActionMap.keySet());
    
    for (PerformedCultivationAction action : actions) {
      if (!existingIds.contains(action.getId())) {
        cultivationObservationEventActionDAO.create(UUID.randomUUID(), cultivationActionEvent, action);
      } else {
        existingIds.remove(action.getId());
      }
    }
   
    for (UUID removedId : existingIds) {
      cultivationObservationEventActionDAO.delete(eventActionMap.get(removedId));
    }
    
    return cultivationActionEvent;
  }
  
  /**
   * Deletes an event
   * 
   * @param cultivationActionEvent event to be deleted
   */
  public void deleteCultivationActionEvent(CultivationObservationEvent cultivationActionEvent) {
    cultivationObservationEventActionDAO.listByEvent(cultivationActionEvent).stream()
      .forEach(cultivationObservationEventActionDAO::delete);    

    cultivationObservationEventDAO.delete(cultivationActionEvent);
  }

  /**
   * Lists performed cultivation action ids by event
   * 
   * @param event event
   * @return performed cultivation action ids
   */
  public List<UUID> listEventPerformedActionIds(CultivationObservationEvent event) {
    return cultivationObservationEventActionDAO.listPerformedActionIdsByEvent(event);
  }
  
}
