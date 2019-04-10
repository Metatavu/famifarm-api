package fi.metatavu.famifarm.reporting.xlsx;

import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.PackingEvent;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Sowed report
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class XlsxPackedReport extends XlsxEventCountReport {

  @Inject
  private LocalesController localesController;
  
  @Override
  protected Double countUnits(List<Event> events) {
    Double count = 0d;
    for (Event event : events) {
      if (event.getType() == EventType.PACKING) {
        PackingEvent packingEvent = (PackingEvent) event;
        count += (packingEvent.getPackedCount() * packingEvent.getPackageSize().getSize());
      }
    }

    return count;
  }

  @Override
  protected String getTitle(Locale locale) {
    return localesController.getString(locale, "reports.packed.title");
  }

}
