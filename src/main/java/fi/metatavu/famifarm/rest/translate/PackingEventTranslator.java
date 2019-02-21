package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.PackingEvent;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.PackingEventData;

/**
 * Translator for packing events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PackingEventTranslator extends AbstractEventTranslator<PackingEventData, PackingEvent> {

  @Override
  protected EventType getType() {
    return EventType.PACKING;
  }

  @Override
  protected PackingEventData translateEventData(PackingEvent event) {
    if (event == null) {
      return null;
    }
    
    PackingEventData result = new PackingEventData();
    result.setPackageSizeId(event.getPackageSize() != null ? event.getPackageSize().getId() : null);
    result.setPackedAmount(event.getPackedAmount());
    
    return result;
  }
  
}
