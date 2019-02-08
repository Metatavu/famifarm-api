package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.rest.model.Event.TypeEnum;
import fi.metatavu.famifarm.rest.model.HarvestEventData;

/**
 * Translator for harvest events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class HarvestEventTranslator extends AbstractEventTranslator<HarvestEventData, HarvestEvent> {

  @Override
  protected TypeEnum getType() {
    return TypeEnum.HARVEST;
  }

  @Override
  protected HarvestEventData translateEventData(HarvestEvent event) {
    if (event == null) {
      return null;
    }
    
    HarvestEventData result = new HarvestEventData();
    result.setProductionLineId(event.getProductionLine() != null ? event.getProductionLine().getId() : null);
    result.setTeamId(event.getTeam() != null ? event.getTeam().getId() : null);
    result.setType(event.getHarvestType());
    
    return result;
  }
  
}
