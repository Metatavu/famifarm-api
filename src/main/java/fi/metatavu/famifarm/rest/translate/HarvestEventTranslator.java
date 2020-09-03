package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.HarvestEventData;

/**
 * Translator for harvest events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class HarvestEventTranslator extends AbstractEventTranslator<HarvestEventData, HarvestEvent> {

  @Override
  protected EventType getType() {
    return EventType.HARVEST;
  }

  @Override
  protected HarvestEventData translateEventData(HarvestEvent event) {
    if (event == null) {
      return null;
    }
    
    HarvestEventData result = new HarvestEventData();
    result.setProductionLineId(event.getProductionLine() != null ? event.getProductionLine().getId() : null);
    result.setType(event.getHarvestType());
    result.setGutterCount(event.getGutterCount());
    
    return result;
  }
  
}
