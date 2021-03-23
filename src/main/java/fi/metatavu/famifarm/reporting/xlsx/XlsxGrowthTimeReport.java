package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Report for growth time
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class XlsxGrowthTimeReport extends AbstractXlsxReport {
  @Inject
  private LocalesController localesController;
  
  @Inject
  private EventController eventController;
  
  @Inject
  private LocalizedValueController localizedValueController;

  @Override
  @SuppressWarnings ("squid:S3776")
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.growth_time.title"));
      
      int lineIndex = 0;
      int productIndex = 1;
      int packingDateIndex = 2;
      int sowingDateIndex = 3;
      int averageWeightIndex = 4;
      int growthTimeIndex = 5;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.growth_time.title")); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime)); 
      xlsxBuilder.setCellValue(sheetId, 3, lineIndex, localesController.getString(locale, "reports.growth_time.lineHeader"));
      xlsxBuilder.setCellValue(sheetId, 3, productIndex, localesController.getString(locale, "reports.growth_time.productHeader")); 
      xlsxBuilder.setCellValue(sheetId, 3, packingDateIndex, localesController.getString(locale, "reports.growth_time.packagingDateHeader")); 
      xlsxBuilder.setCellValue(sheetId, 3, sowingDateIndex, localesController.getString(locale, "reports.growth_time.sowingDateHeader"));
      xlsxBuilder.setCellValue(sheetId, 3, averageWeightIndex, localesController.getString(locale, "reports.growth_time.averageWeightHeader")); 
      xlsxBuilder.setCellValue(sheetId, 3, growthTimeIndex, localesController.getString(locale, "reports.growth_time.growthTimeHeader"));
      
      // Values

      int rowIndex = 4;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); 
      List<Event> events = eventController.listByStartTimeAfterAndStartTimeBefore(parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
      List<HarvestEvent> harvestEvents = events
        .stream()
        .filter(e -> e.getType().equals(EventType.HARVEST))
        .map(e -> (HarvestEvent) e)
        .collect(Collectors.toList());
      
      for (HarvestEvent event: harvestEvents) {
        String harvestDateString = event.getEndTime() != null ? event.getEndTime().format(formatter) : "TIETO PUUTTUU";
        String sowingDateString = event.getSowingDate() != null ? event.getSowingDate().format(formatter) : "TIETO PUUTTUU";
        xlsxBuilder.setCellValue(sheetId, rowIndex, lineIndex, event.getProductionLine().getLineNumber());
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(event.getProduct().getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, packingDateIndex, harvestDateString);
        xlsxBuilder.setCellValue(sheetId, rowIndex, sowingDateIndex, sowingDateString);
        xlsxBuilder.setCellValue(sheetId, rowIndex, averageWeightIndex, getAverageWeight(events, event.getProduct()));
        xlsxBuilder.setCellValue(sheetId, rowIndex, growthTimeIndex, getGrowthTime(event));
        rowIndex++;
      }
      
      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  private Long getGrowthTime(HarvestEvent event) {
    if (event.getEndTime() == null || event.getSowingDate() == null) {
      return -1l;
    }
    Date packingDate = Date.from(event.getEndTime().toInstant());
    Date sowingDate = Date.from(event.getSowingDate().toInstant());
    return (Long) (packingDate.getTime() - sowingDate.getTime()) / (1000*60*60*24);
  }

  private double getAverageWeight(List<Event> events, Product product) {
    if (product == null) {
      return -1d;
    }
    UUID productId = product.getId();
    return events
      .stream()
      .filter( e -> {
        return EventType.CULTIVATION_OBSERVATION.equals(e.getType()) 
          && e.getProduct() != null 
          && productId.equals(e.getProduct().getId());
      })
      .filter(e -> ((CultivationObservationEvent) e).getWeight() != null)
      .collect(Collectors.averagingDouble(e -> {
        return ((CultivationObservationEvent) e).getWeight();
      }));
  }

}
