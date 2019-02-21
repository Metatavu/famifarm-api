package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.math3.analysis.function.Add;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.reporting.ReportException;

/**
 * Simple report demonstrating reporting features
 * 
 * @author Antti Leppä
 */
public class XlsxExampleReport extends AbstractXlsxReport {

  @Inject
  private LocalesController localesController;
  
  @Inject
  private EventController eventController;

  @Override
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    Map<UUID, String> userCache = new HashMap<>();
    
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.xls_example.sheet1"));
      
      int batchIndex = 0;
      int startTimeIndex = 1;
      int endTimeIndex = 2;
      int creatorIndex = 3;
      int typeIndex = 4;
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, batchIndex, localesController.getString(locale, "reports.xls_example.batchHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, startTimeIndex, localesController.getString(locale, "reports.xls_example.startHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, endTimeIndex, localesController.getString(locale, "reports.xls_example.endHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, creatorIndex, localesController.getString(locale, "reports.xls_example.creatorHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, typeIndex, localesController.getString(locale, "reports.xls_example.typeHeader")); 
      
      // Values
      
      List<Event> events = eventController.listEvents(null, 0, 100);
      for (int i = 0; i < events.size(); i++) {
        int rowIndex = i + 1;
        Event event = events.get(i);
        xlsxBuilder.setCellValue(sheetId, rowIndex, batchIndex, getFormattedBatch(locale, event.getBatch()));
        xlsxBuilder.setCellValue(sheetId, rowIndex, startTimeIndex, event.getStartTime());
        xlsxBuilder.setCellValue(sheetId, rowIndex, endTimeIndex, event.getEndTime());
        xlsxBuilder.setCellValue(sheetId, rowIndex, creatorIndex, getFormattedUser(event.getCreatorId(), userCache));
        xlsxBuilder.setCellValue(sheetId, rowIndex, typeIndex, event.getType().name());
        
      }
      
      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }
  
}
