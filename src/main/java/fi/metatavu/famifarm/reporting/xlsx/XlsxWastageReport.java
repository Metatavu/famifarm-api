package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * Report for growth time
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class XlsxWastageReport extends AbstractXlsxReport {
  @Inject
  private LocalesController localesController;
  
  @Inject
  private EventController eventController;
  
  @Inject
  private LocalizedValueController localizedValueController;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    Map<UUID, String> userCache = new HashMap<>();
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.wastage.title"));
      
      int dateIndex = 0;
      int lineIndex = 1;
      int workerIndex = 2;
      int productIndex = 3;
      int phaseIndex = 4;
      int reasonIndex = 5;
      int additionalInformationIndex = 6;
      int amountIndex = 7;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.wastage.title")); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime)); 
      xlsxBuilder.setCellValue(sheetId, 3, lineIndex, localesController.getString(locale, "reports.wastage.line"));
      xlsxBuilder.setCellValue(sheetId, 3, dateIndex, localesController.getString(locale, "reports.wastage.wastageDate")); 
      xlsxBuilder.setCellValue(sheetId, 3, workerIndex, localesController.getString(locale, "reports.wastage.worker")); 
      xlsxBuilder.setCellValue(sheetId, 3, productIndex, localesController.getString(locale, "reports.wastage.product")); 
      xlsxBuilder.setCellValue(sheetId, 3, phaseIndex, localesController.getString(locale, "reports.wastage.phase"));
      xlsxBuilder.setCellValue(sheetId, 3, reasonIndex, localesController.getString(locale, "reports.wastage.wastageReason"));
      xlsxBuilder.setCellValue(sheetId, 3, additionalInformationIndex, localesController.getString(locale, "reports.wastage.additionalInformation"));
      xlsxBuilder.setCellValue(sheetId, 3, amountIndex, localesController.getString(locale, "reports.wastage.wastageAmount"));
      
      // Values
      
      int rowIndex = 4;
      
      List<Event> events = eventController.listByFacilityAndStartTimeAfterAndStartTimeBefore(facility, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); 
      
      for (int j = 0; j < events.size(); j++) {
        Event event = events.get(j);
        
        if (event.getType() == EventType.WASTAGE) {
          WastageEvent wastageEvent = (WastageEvent) event;
          OffsetDateTime endTime = wastageEvent.getEndTime();
          
          xlsxBuilder.setCellValue(sheetId, rowIndex, lineIndex, wastageEvent.getProductionLine() != null ? wastageEvent.getProductionLine().getLineNumber() : "");
          xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, endTime.format(formatter));
          xlsxBuilder.setCellValue(sheetId, rowIndex, workerIndex, getFormattedUser(event.getCreatorId(), userCache));
          xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(event.getProduct().getName(), locale));
          xlsxBuilder.setCellValue(sheetId, rowIndex, phaseIndex, wastageEvent.getPhase() != null ? wastageEvent.getPhase().toString() : "");
          xlsxBuilder.setCellValue(sheetId, rowIndex, reasonIndex, wastageEvent.getWastageReason() != null ? localizedValueController.getValue(wastageEvent.getWastageReason().getReason(), locale) : "");
          xlsxBuilder.setCellValue(sheetId, rowIndex, additionalInformationIndex, wastageEvent.getAdditionalInformation());
          xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, wastageEvent.getAmount() != null ? wastageEvent.getAmount() : 0);
          rowIndex++;
        }
        
      }
      
      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }
}
