package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.rest.model.Event.TypeEnum;
import fi.metatavu.famifarm.rest.model.WastageEventData;

/**
 * Translator for wastage events
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class WastageEventTranslator extends AbstractEventTranslator<WastageEventData, WastageEvent> {

  @Override
  protected TypeEnum getType() {
    return TypeEnum.WASTEAGE;
  }

  @Override
  protected WastageEventData translateEventData(WastageEvent event) {
    if (event == null) {
      return null;
    }
    
    WastageEventData result = new WastageEventData();
    result.setAmount(event.getAmount());
    result.setDescription(event.getDescription());
    result.setReasonId(event.getWastageReason().getId());

    return result;
  }
}
