package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import fi.metatavu.famifarm.batches.BatchController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.SowingEventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.reporting.ReportException;

/**
 * Report for growth time
 * 
 * @author Ville Koivukangas
 */
public class XlsWastageReport extends AbstractXlsxReport {
  @Inject
  private LocalesController localesController;
  
  @Inject
  private EventController eventController;
  
  @Inject
  private SowingEventController sowingEventController;
  
  @Inject
  private BatchController batchController;

  @Override
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    Map<UUID, String> userCache = new HashMap<>();
    
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.growth_time.title"));
      
      int teamIndex = 0;
      int lineIndex = 1;
      int productIndex = 2;
      int packagingDateIndex = 3;
      int sowingDateIndex = 4;
      int averageWeightIndex = 5;
      int growthTimeIndex = 6;
      int daysIndex = 7;
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, teamIndex, localesController.getString(locale, "reports.growth_time.teamHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, lineIndex, localesController.getString(locale, "reports.growth_time.lineHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, productIndex, localesController.getString(locale, "reports.growth_time.productHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, packagingDateIndex, localesController.getString(locale, "reports.growth_time.packagingDateHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, sowingDateIndex, localesController.getString(locale, "reports.growth_time.sowingDateHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, averageWeightIndex, localesController.getString(locale, "reports.growth_time.averageWeightHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, growthTimeIndex, localesController.getString(locale, "reports.growth_time.growthTimeHeader")); 
      xlsxBuilder.setCellValue(sheetId, 0, daysIndex, localesController.getString(locale, "reports.growth_time.daysHeader")); 
      
      // Values
      
      List<Batch> batches = batchController.listBatches(null, null, null, null);
      for (Batch batch : batches) {
        List<Event> events = eventController.listEvents(batch, null, null);
        
        for (Event event : events) {
          switch (event.getType()) {
          case SOWING:
            System.out.println("SOWING!");
            SowingEvent sowingEvent = sowingEventController.findSowingEventById(event.getId());
            System.out.println(sowingEvent.getGutterNumber());
            break;
          default:
            System.out.println("Nicht sowing");
          }
        }
        
        System.out.println(events);
      }
      
      
      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }
}
