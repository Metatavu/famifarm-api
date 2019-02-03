package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.TableSpreadEvent;
import fi.metatavu.famifarm.rest.model.Event.TypeEnum;
import fi.metatavu.famifarm.rest.model.TableSpreadEventData;

/**
 * Translator for tableSpread events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class TableSpreadEventTranslator extends AbstractEventTranslator<TableSpreadEventData, TableSpreadEvent> {

  @Override
  protected TypeEnum getType() {
    return TypeEnum.SOWING;
  }

  @Override
  protected TableSpreadEventData translateEventData(TableSpreadEvent event) {
    if (event == null) {
      return null;
    }
    
    TableSpreadEventData result = new TableSpreadEventData();
    result.setLocation(event.getLocation());
    result.setTableCount(event.getTableCount());

    return result;
  }
  
}
