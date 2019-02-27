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

import javax.inject.Inject;

import fi.metatavu.famifarm.batches.BatchController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.WastageEventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Report for growth time
 * 
 * @author Ville Koivukangas
 */
public class XlsxWastageReport extends AbstractXlsxReport {
  @Inject
  private LocalesController localesController;
  
  @Inject
  private EventController eventController;
  
  @Inject
  private WastageEventController wastageEventController;
  
  @Inject
  private BatchController batchController;
  
  @Inject
  private LocalizedValueController localizedValueController;

  @Override
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    Map<UUID, String> userCache = new HashMap<>();
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.wastage.title"));
      
      int dateIndex = 0;
      int lineIndex = 1;
      int workerIndex = 2;
      int productIndex = 3;
      int reasonIndex = 4;
      int amountIndex = 5;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.wastage.title")); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime)); 
      xlsxBuilder.setCellValue(sheetId, 3, lineIndex, localesController.getString(locale, "reports.wastage.line")); 
      xlsxBuilder.setCellValue(sheetId, 3, dateIndex, localesController.getString(locale, "reports.wastage.wastageDate")); 
      xlsxBuilder.setCellValue(sheetId, 3, workerIndex, localesController.getString(locale, "reports.wastage.worker")); 
      xlsxBuilder.setCellValue(sheetId, 3, productIndex, localesController.getString(locale, "reports.wastage.product")); 
      xlsxBuilder.setCellValue(sheetId, 3, reasonIndex, localesController.getString(locale, "reports.wastage.wastageReason")); 
      xlsxBuilder.setCellValue(sheetId, 3, amountIndex, localesController.getString(locale, "reports.wastage.wastageAmount"));
      
      // Values
      
      List<Batch> batches = batchController.listBatches(null, null, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
      int rowIndex = 4;
      
      for (int i = 0; i < batches.size(); i++) {
        Batch batch = batches.get(i);
        List<Event> events = eventController.listEvents(batch, null, null);
        Product product = batch.getProduct();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");     
        for (int j = 0; j < events.size(); j++) {
          Event event = events.get(j);
          
          if (event.getType() == EventType.WASTAGE) {
            WastageEvent wastageEvent = wastageEventController.findWastageEventById(event.getId());
            OffsetDateTime endTime = wastageEvent.getEndTime();
            
            xlsxBuilder.setCellValue(sheetId, rowIndex, lineIndex, wastageEvent.getProductionLine().getLineNumber());
            xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, endTime.format(formatter));
            xlsxBuilder.setCellValue(sheetId, rowIndex, workerIndex, getFormattedUser(event.getCreatorId(), userCache));
            xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
            xlsxBuilder.setCellValue(sheetId, rowIndex, reasonIndex, localizedValueController.getValue(wastageEvent.getWastageReason().getReason(), locale));
            xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex,  wastageEvent.getAmount().toString());
          }
          rowIndex++;
        }
      }
      
      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }
}
