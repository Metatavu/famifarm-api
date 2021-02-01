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
import fi.metatavu.famifarm.persistence.dao.CultivationObservationEventPestDAO;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEventAction;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEventPest;
import fi.metatavu.famifarm.persistence.model.PerformedCultivationAction;
import fi.metatavu.famifarm.persistence.model.Pest;
import fi.metatavu.famifarm.persistence.model.Product;

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

  @Inject
  private CultivationObservationEventPestDAO cultivationObservationEventPestDAO;
  
  /**
   * Create cultivationActionEvent
   *
   * @param product product
   * @param startTime startTime
   * @param endTime endTime
   * @param weight weight
   * @param luminance luminance
   * @param pests pests
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return created cultivationActionEvent
   */
  @SuppressWarnings ("squid:S00107")
  public CultivationObservationEvent createCultivationActionEvent(Product product, OffsetDateTime startTime, OffsetDateTime endTime, Double weight, Double luminance, List<Pest> pests, List<PerformedCultivationAction> actions, String additionalInformation, UUID creatorId) {
    CultivationObservationEvent event = cultivationObservationEventDAO.create(UUID.randomUUID(), weight, luminance, product, startTime, endTime, 0, additionalInformation, creatorId, creatorId);
    
    if (actions != null) {
      actions.stream().forEach(action -> cultivationObservationEventActionDAO.create(UUID.randomUUID(), event, action));
    }
    
    if (pests != null) {
      pests.stream().forEach(pest -> cultivationObservationEventPestDAO.create(UUID.randomUUID(), event, pest));
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
   * @param product product
   * @param startTime startTime
   * @param endTime endTime
   * @param weight weight
   * @param luminance luminance
   * @param pests pests
   * @param additionalInformation additional information
   * @param modifier modifier
   * @return updated cultivationActionEvent
   */
  @SuppressWarnings ("squid:S00107")
  public CultivationObservationEvent updateCultivationActionEvent(CultivationObservationEvent cultivationActionEvent, Product product, OffsetDateTime startTime, OffsetDateTime endTime, Double weight, Double luminance, List<Pest> pests, List<PerformedCultivationAction> actions, String additionalInformation, UUID modifier) {
    cultivationObservationEventDAO.updateProduct(cultivationActionEvent, product, modifier);
    cultivationObservationEventDAO.updateStartTime(cultivationActionEvent, startTime, modifier);
    cultivationObservationEventDAO.updateEndTime(cultivationActionEvent, endTime, modifier);
    cultivationObservationEventDAO.updateWeight(cultivationActionEvent, weight, modifier);
    cultivationObservationEventDAO.updateLuminance(cultivationActionEvent, luminance, modifier);
    cultivationObservationEventDAO.updateAdditionalInformation(cultivationActionEvent, additionalInformation, modifier);
    
    List<CultivationObservationEventAction> eventActions = cultivationObservationEventActionDAO.listByEvent(cultivationActionEvent);

    Map<UUID, CultivationObservationEventAction> eventActionMap = eventActions.stream()
      .collect(Collectors.toMap(eventAction -> eventAction.getAction().getId(), eventAction -> eventAction));  
    
    Set<UUID> existingActionIds = new HashSet<>(eventActionMap.keySet());
    
    for (PerformedCultivationAction action : actions) {
      if (!existingActionIds.contains(action.getId())) {
        cultivationObservationEventActionDAO.create(UUID.randomUUID(), cultivationActionEvent, action);
      } else {
        existingActionIds.remove(action.getId());
      }
    }
   
    for (UUID removedId : existingActionIds) {
      cultivationObservationEventActionDAO.delete(eventActionMap.get(removedId));
    }

    List<CultivationObservationEventPest> eventPests = cultivationObservationEventPestDAO.listByEvent(cultivationActionEvent);

    Map<UUID, CultivationObservationEventPest> eventPestMap = eventPests.stream()
      .collect(Collectors.toMap(eventPest -> eventPest.getPest().getId(), eventPest -> eventPest));  
    
    Set<UUID> existingPestIds = new HashSet<>(eventPestMap.keySet());
    
    for (Pest pest : pests) {
      if (!existingPestIds.contains(pest.getId())) {
        cultivationObservationEventPestDAO.create(UUID.randomUUID(), cultivationActionEvent, pest);
      } else {
        existingPestIds.remove(pest.getId());
      }
    }
   
    for (UUID removedId : existingPestIds) {
      cultivationObservationEventPestDAO.delete(eventPestMap.get(removedId));
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
    
    cultivationObservationEventPestDAO.listByEvent(cultivationActionEvent).stream()
      .forEach(cultivationObservationEventPestDAO::delete);    

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

  /**
   * Lists pest ids by event
   * 
   * @param event event
   * @return performed cultivation action ids
   */
  public List<UUID> listEventPestIds(CultivationObservationEvent event) {
    return cultivationObservationEventActionDAO.listPestIdsByEvent(event);
  }
  
}
