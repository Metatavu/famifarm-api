package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.batches.BatchController;
import fi.metatavu.famifarm.events.CultivationObservationEventController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.SowingEventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
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
  private SowingEventController sowingEventController;
  
  @Inject
  private CultivationObservationEventController cultivationObservationEventController;
  
  @Inject
  private BatchController batchController;
  
  @Inject
  private LocalizedValueController localizedValueController;
  
  private float totalWeight = 0;
  
  private int amountOfCultivationObservations = 0;
  
  private Event lastPackingEvent = null;
  
  private Event firstSowingEvent = null;

  @Override
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.wastage.title"));
      
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
      
      List<Batch> batches = batchController.listBatches(null, null, null, null, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")), null, null);
      int rowIndex = 4;
      
      for (Batch batch : batches) {
        List<Event> events = eventController.listEvents(batch, null, null);
        Product product = batch.getProduct();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); 
        
        lastPackingEvent = null;
        firstSowingEvent = null;
        totalWeight = 0;
        amountOfCultivationObservations = 0;
        double averageWeight = 0;
        
        for (Event event: events) {
          switch (event.getType()) {
            case CULTIVATION_OBSERVATION:
              handleCultivationObservationEvent(event);
              break;
            case PACKING:
              handlePackingEvent(event);
              break;
            case SOWING:
              handleSowingEvent(event);
              break;
            default:
              break;
          }
        }
        
        if (totalWeight > 0 && amountOfCultivationObservations > 0) {
          averageWeight = totalWeight / amountOfCultivationObservations;
        }
        
        if (lastPackingEvent != null && firstSowingEvent != null) {
          SowingEvent sowingEvent = sowingEventController.findSowingEventById(firstSowingEvent.getId());
          
          Date packingDate = Date.from(lastPackingEvent.getEndTime().toInstant());
          Date sowingDate = Date.from(firstSowingEvent.getStartTime().toInstant());
          int days = (int) (packingDate.getTime() - sowingDate.getTime()) / (1000*60*60*24);

          xlsxBuilder.setCellValue(sheetId, rowIndex, lineIndex, sowingEvent.getProductionLine().getLineNumber());
          xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
          xlsxBuilder.setCellValue(sheetId, rowIndex, packingDateIndex, lastPackingEvent.getEndTime().format(formatter));
          xlsxBuilder.setCellValue(sheetId, rowIndex, sowingDateIndex, firstSowingEvent.getStartTime().format(formatter));
          xlsxBuilder.setCellValue(sheetId, rowIndex, averageWeightIndex, Double.toString(averageWeight));
          xlsxBuilder.setCellValue(sheetId, rowIndex, growthTimeIndex, Integer.toString(days));
          rowIndex++;
        }
      }
      
      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }
  
  /**
   * Handle packing event
   * 
   * @param event
   */
  private void handlePackingEvent(Event event) {
    if (lastPackingEvent == null) {
      lastPackingEvent = event;
    } else {
      if (event.getEndTime().isAfter(lastPackingEvent.getEndTime())) {
        lastPackingEvent = event; 
      }
    }
  }
  /**
   * Handle sowing event
   * @param event
   */
  private void handleSowingEvent(Event event) {
    if (firstSowingEvent == null) {
      firstSowingEvent = event;
    } else {
      if (event.getStartTime().isBefore(firstSowingEvent.getStartTime())) {
        firstSowingEvent = event; 
      }
    }
  }
  
  /**
   * Handle cultivation observation event
   * @param event
   */
  private void handleCultivationObservationEvent(Event event) {
    if (event.getType() == EventType.CULTIVATION_OBSERVATION) {
      CultivationObservationEvent cultivationObservationEvent = cultivationObservationEventController.findCultivationActionEventById(event.getId());
      totalWeight += cultivationObservationEvent.getWeight();
      amountOfCultivationObservations++;
    }
  }
}
