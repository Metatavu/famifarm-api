package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.WastageEventData;

/**
 * Translator for wastage events
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class WastageEventTranslator extends AbstractEventTranslator<WastageEventData, WastageEvent> {

  @Override
  protected EventType getType() {
    return EventType.WASTEAGE;
  }

  @Override
  protected WastageEventData translateEventData(WastageEvent event) {
    if (event == null) {
      return null;
    }
    
    WastageEventData result = new WastageEventData();
    result.setAmount(event.getAmount());
    result.setReasonId(event.getWastageReason().getId());
    result.setPhase(event.getPhase());
    
    return result;
  }
}
