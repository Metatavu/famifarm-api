package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.PlantingEventData;

/**
 * Translator for planting events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PlantingEventTranslator extends AbstractEventTranslator<PlantingEventData, PlantingEvent> {

  @Override
  protected EventType getType() {
    return EventType.PLANTING;
  }

  @Override
  protected PlantingEventData translateEventData(PlantingEvent event) {
    if (event == null) {
      return null;
    }
    
    PlantingEventData result = new PlantingEventData();
    result.setGutterCount(event.getGutterCount());
    result.setGutterHoleCount(event.getGutterHoleCount());
    result.setProductionLineId(event.getProductionLine() != null ? event.getProductionLine().getId() : null);
    result.setTrayCount(event.getTrayCount());
    result.setWorkerCount(event.getWorkerCount());

    return result;
  }
  
}
