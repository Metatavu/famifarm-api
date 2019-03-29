package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.SowingEventData;

/**
 * Translator for sowing events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SowingEventTranslator extends AbstractEventTranslator<SowingEventData, SowingEvent> {

  @Override
  protected EventType getType() {
    return EventType.SOWING;
  }

  @Override
  protected SowingEventData translateEventData(SowingEvent event) {
    if (event == null) {
      return null;
    }
    
    SowingEventData result = new SowingEventData();
    result.setAmount(event.getAmount());
    result.setPotType(event.getPotType());
    result.setProductionLineId(event.getProductionLine() != null ? event.getProductionLine().getId() : null);
    result.setSeedBatchId(event.getSeedBatch() != null ? event.getSeedBatch().getId() : null);
    return result;
  }
  
}
