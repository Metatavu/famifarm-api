package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.events.CultivationObservationEventController;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.rest.model.CultivationObservationEventData;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Translator for cultivationObservation events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class CultivationObservationEventTranslator extends AbstractEventTranslator<CultivationObservationEventData, CultivationObservationEvent> {
  
  @Inject
  private CultivationObservationEventController cultivationObservationEventController;
  
  @Override
  protected EventType getType() {
    return EventType.CULTIVATION_OBSERVATION;
  }

  @Override
  protected CultivationObservationEventData translateEventData(CultivationObservationEvent event) {
    if (event == null) {
      return null;
    }
    
    CultivationObservationEventData result = new CultivationObservationEventData();
    result.setLuminance(event.getLuminance());
    result.setPerformedActionIds(cultivationObservationEventController.listEventPerformedActionIds(event));
    result.setPestIds(cultivationObservationEventController.listEventPestIds(event));
    result.setWeight(event.getWeight());
    
    return result;
  }
  
}
