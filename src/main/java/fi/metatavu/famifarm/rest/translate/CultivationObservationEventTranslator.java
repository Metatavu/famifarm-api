package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.events.CultivationObservationEventController;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.rest.model.Event.TypeEnum;
import fi.metatavu.famifarm.rest.model.CultivationObservationEventData;

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
  protected TypeEnum getType() {
    return TypeEnum.CULTIVATION_OBSERVATION;
  }

  @Override
  protected CultivationObservationEventData translateEventData(CultivationObservationEvent event) {
    if (event == null) {
      return null;
    }
    
    CultivationObservationEventData result = new CultivationObservationEventData();
    result.setLuminance(event.getLuminance());
    result.setPerformedActionIds(cultivationObservationEventController.listEventPerformedActionIds(event));
    result.setPests(event.getPests());
    result.setWeight(event.getWeight());
    
    return result;
  }
  
}
