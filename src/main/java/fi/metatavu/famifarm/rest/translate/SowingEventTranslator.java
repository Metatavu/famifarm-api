package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.rest.model.Event.TypeEnum;
import fi.metatavu.famifarm.rest.model.SowingEventData;

/**
 * Translator for sowing events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SowingEventTranslator extends AbstractEventTranslator<SowingEventData, SowingEvent> {

  @Override
  protected TypeEnum getType() {
    return TypeEnum.SOWING;
  }

  @Override
  protected SowingEventData translateEventData(SowingEvent event) {
    if (event == null) {
      return null;
    }
    
    SowingEventData result = new SowingEventData();
    result.setAmount(event.getAmount());
    result.setCellType(event.getCellType());
    result.setGutterNumber(event.getGutterNumber());
    result.setProductionLineId(event.getProductionLine() != null ? event.getProductionLine().getId() : null);
    result.setSeedBatchId(event.getSeedBatch() != null ? event.getSeedBatch().getId() : null);
    return result;
  }
  
}