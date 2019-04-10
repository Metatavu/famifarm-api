package fi.metatavu.famifarm.reporting.xlsx;

import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.TableSpreadEvent;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.PotType;

/**
 * Table Spread report
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class XlsxSpreadReport extends XlsxEventCountReport {

  @Inject
  private LocalesController localesController;
  
  @Override
  protected Double countUnits(List<Event> events) {
    Double count = 0d;
    
    SowingEvent sowingEvent = (SowingEvent) events.stream().filter(event -> event.getType() == EventType.SOWING).findFirst().orElse(null);
    if (sowingEvent == null) {
      return 0d;
    }
    
    PotType potType = sowingEvent.getPotType();

    for (Event event : events) {
      if (event.getType() == EventType.TABLE_SPREAD) {
        TableSpreadEvent tableSpreadEvent = (TableSpreadEvent) event;
        count += (tableSpreadEvent.getTrayCount() * getPotTypeAmount(potType));
      }
    }
    
    return count;
  }

  @Override
  protected String getTitle(Locale locale) {
    return localesController.getString(locale, "reports.spread.title");
  }

    /**
   * Get tray type as int
   * 
   * @param potType, potType
   * @return amount
   */
  private int getPotTypeAmount(PotType potType) {
    if (PotType.SMALL == potType) {
      return 54;
    }
    return 35;
  }

}
