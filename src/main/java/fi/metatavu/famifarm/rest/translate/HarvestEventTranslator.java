package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.HarvestBasketDAO;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.HarvestBasket;
import fi.metatavu.famifarm.rest.model.HarvestEventData;

import java.util.stream.Collectors;

/**
 * Translator for harvest events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class HarvestEventTranslator extends AbstractEventTranslator<HarvestEventData, HarvestEvent> {

  @Inject
  public HarvestBasketDAO harvestBasketDAO;

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
    result.setGutterHoleCount(event.getGutterHoleCount());
    result.setSowingDate(event.getSowingDate());
    result.setCuttingHeight(event.getCuttingHeight());

    result.setBaskets(harvestBasketDAO.listByHarvestEvent(event).stream().map(harvestBasketEntity -> {
      HarvestBasket harvestBasket = new HarvestBasket();
      harvestBasket.setWeight(harvestBasketEntity.getWeight());
      return harvestBasket;
    }).collect(Collectors.toList()));
    
    return result;
  }
  
}
