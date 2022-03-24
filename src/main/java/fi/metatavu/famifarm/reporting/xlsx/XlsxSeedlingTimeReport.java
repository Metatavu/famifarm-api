package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Report for seedling time
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class XlsxSeedlingTimeReport extends AbstractXlsxReport {

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
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.seedling_time.title"));
      
      int lineIndex = 0;
      int productIndex = 1;
      int plantingDateIndex = 2;
      int sowingDateIndex = 3;
      int seedlingTimeIndex = 4;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.seedling_time.title")); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime)); 
      xlsxBuilder.setCellValue(sheetId, 3, lineIndex, localesController.getString(locale, "reports.seedling_time.lineHeader"));
      xlsxBuilder.setCellValue(sheetId, 3, productIndex, localesController.getString(locale, "reports.seedling_time.productHeader")); 
      xlsxBuilder.setCellValue(sheetId, 3, plantingDateIndex, localesController.getString(locale, "reports.seedling_time.plantingDateHeader")); 
      xlsxBuilder.setCellValue(sheetId, 3, sowingDateIndex, localesController.getString(locale, "reports.seedling_time.sowingDateHeader"));
      xlsxBuilder.setCellValue(sheetId, 3, seedlingTimeIndex, localesController.getString(locale, "reports.seedling_time.seedlingTimeHeader"));
      
      // Values

      int rowIndex = 4;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); 
      List<Event> events = eventController.listByStartTimeAfterAndStartTimeBefore(parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
      List<PlantingEvent> plantingEvents = events
        .stream()
        .filter(e -> e.getType().equals(EventType.PLANTING))
        .map(e -> (PlantingEvent) e)
        .collect(Collectors.toList());
      
      for (PlantingEvent event: plantingEvents) {
        String plantingDateString = event.getEndTime() != null ? event.getEndTime().format(formatter) : "TIETO PUUTTUU";
        String sowingDateString = event.getSowingDate() != null ? event.getSowingDate().format(formatter) : "TIETO PUUTTUU";
        xlsxBuilder.setCellValue(sheetId, rowIndex, lineIndex, event.getProductionLine().getLineNumber());
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(event.getProduct().getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, plantingDateIndex, plantingDateString);
        xlsxBuilder.setCellValue(sheetId, rowIndex, sowingDateIndex, sowingDateString);
        xlsxBuilder.setCellValue(sheetId, rowIndex, seedlingTimeIndex, getSeedlingTime(event));
        rowIndex++;
      }
      
      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  private Long getSeedlingTime(PlantingEvent event) {
    if (event.getEndTime() == null || event.getSowingDate() == null) {
      return -1l;
    }
    Date plantingDate = Date.from(event.getEndTime().toInstant());
    Date sowingDate = Date.from(event.getSowingDate().toInstant());
    return (Long) (plantingDate.getTime() - sowingDate.getTime()) / (1000*60*60*24);
  }
}
