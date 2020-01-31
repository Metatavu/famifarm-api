package fi.metatavu.famifarm.rest.translate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.events.SowingEventController;
import fi.metatavu.famifarm.persistence.model.SeedBatch;
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
  
  @Inject
  private SowingEventController sowingEventController;

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
    
    List<UUID> seedBatchIds = sowingEventController.listSowingEventSeedBatches(event).stream()
      .map(SeedBatch::getId)
      .collect(Collectors.toList());
    
    result.setSeedBatchIds(seedBatchIds);
    
    return result;
  }
  
}
