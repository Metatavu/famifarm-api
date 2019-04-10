package fi.metatavu.famifarm.reporting.xlsx;

import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Harvested report
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class XlsxHarvestedReport extends XlsxEventCountReport {

  @Inject
  private LocalesController localesController;
  
  @Override
  protected Double countUnits(List<Event> events) {
    Double count = 0d;
    Double gutterHoleCount = getAverageGutterHoleCount(events);

    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = (HarvestEvent) event;
        count += (harvestEvent.getGutterCount() * gutterHoleCount);
      }
    }
    
    return count;
  }

  @Override
  protected String getTitle(Locale locale) {
    return localesController.getString(locale, "reports.harvested.title");
  }

    /**
   * Get weighted average gutter hole count
   * 
   * @param events
   * @return weighted average gutter hole count
   */
  private Double getAverageGutterHoleCount(List<Event> events) {
    Double totalWeightedSize = 0d;
    Double totalGutterCount = 0d;
    
    for (Event event : events) {
      if (event.getType() == EventType.PLANTING) {
        PlantingEvent plantingEvent = (PlantingEvent) event;
        totalWeightedSize += (plantingEvent.getGutterHoleCount() * plantingEvent.getGutterCount());
        totalGutterCount += plantingEvent.getGutterCount();
      }
    }

    if (totalWeightedSize == 0 || totalGutterCount == 0) {
      return 0d;
    }

    return totalWeightedSize / totalGutterCount;
  }

}
