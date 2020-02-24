package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.batches.BatchController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packing.PackingController;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Report for yield
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class XlsxYieldReport extends AbstractXlsxReport {
  @Inject
  private LocalesController localesController;
  
  @Inject
  private EventController eventController;
  
  @Inject
  private BatchController batchController;
  
  @Inject
  private LocalizedValueController localizedValueController;
  
  @Inject
  private PackingController packingController;

  @Override
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.yield.yieldTitle"));
      
      int teamIndex = 0;
      int productIndex = 1;
      int dateIndex = 2;
      int harvestedIndex = 3;
      int inBoxesIndex = 4;
      int yieldIndex = 5;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.yield.yieldTitle")); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime)); 
      xlsxBuilder.setCellValue(sheetId, 3, teamIndex, localesController.getString(locale, "reports.yield.yieldTeam")); 
      xlsxBuilder.setCellValue(sheetId, 3, productIndex, localesController.getString(locale, "reports.yield.yieldProduct"));
      xlsxBuilder.setCellValue(sheetId, 3, dateIndex, localesController.getString(locale, "reports.yield.yieldDate"));
      xlsxBuilder.setCellValue(sheetId, 3, harvestedIndex, localesController.getString(locale, "reports.yield.yieldHarvested"));
      xlsxBuilder.setCellValue(sheetId, 3, inBoxesIndex, localesController.getString(locale, "reports.yield.yieldInBoxes")); 
      xlsxBuilder.setCellValue(sheetId, 3, yieldIndex, localesController.getString(locale, "reports.yield.yieldPercentage"));
      
      // Values
      
      List<Batch> batches = batchController.listBatches(null, null, null, null, null, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")), null, null);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      int rowIndex = 4;
      
      for (Batch batch : batches) {
        List<Event> events = eventController.listByBatchSortByStartTimeAsc(batch, null, null); 
        
        Product product = batch.getProduct();
        List<Packing> packings = packingController.listPackings(null, null, product.getId(), null, null, null);
        String dateString = getDateString(events, formatter);
        String team = getTeam(events, locale);
        double totalHarvestedAmount = getTotalHarvestedAmount(events);
        double totalAmountInBoxes = getTotalAmountInBoxes(packings);
        double yield = getYield(totalHarvestedAmount, totalAmountInBoxes);
        
        xlsxBuilder.setCellValue(sheetId, rowIndex, teamIndex, team);
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, dateString);
        xlsxBuilder.setCellValue(sheetId, rowIndex, harvestedIndex, Double.toString(totalHarvestedAmount));
        xlsxBuilder.setCellValue(sheetId, rowIndex, inBoxesIndex, Double.toString(totalAmountInBoxes));
        xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex,  Double.toString(yield));
        rowIndex++;
      }
      
      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }
  
  /**
   * Get date
   * 
   * @param events events
   * @param formatter formatter
   * @return date string
   */
  private String getDateString(List<Event> events, DateTimeFormatter formatter) {
    OffsetDateTime date = null;
    
    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = (HarvestEvent) event;
        if (harvestEvent.getEndTime() != null) {
          date = harvestEvent.getEndTime();
        }
      }
    }
    
    if (date != null) {
      return date.format(formatter);
    }
    
    return "";
  }

  /**
   * Get weighted average gutter hole count
   * 
   * @param events
   * @return weighted average gutter hole count
   */
  private double getAverageGutterHoleCount(List<Event> events) {
    double totalWeightedSize = 0;
    double totalGutterCount = 0;
    
    for (Event event : events) {
      if (event.getType() == EventType.PLANTING) {
        PlantingEvent plantingEvent = (PlantingEvent) event;
        totalWeightedSize += (plantingEvent.getGutterHoleCount() * plantingEvent.getGutterCount());
        totalGutterCount += plantingEvent.getGutterCount();
      }
    }

    if (totalWeightedSize == 0 || totalGutterCount == 0) {
      return 0;
    }

    return totalWeightedSize / totalGutterCount;
  }

  /**
   * Get total harvested amount
   * 
   * @param events
   * @return total sowed amount
   */
  private double getTotalHarvestedAmount(List<Event> events) {
    double amount = 0;
    double gutterHoleCount = getAverageGutterHoleCount(events);

    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = (HarvestEvent) event;
        amount += (harvestEvent.getGutterCount() * gutterHoleCount);
      }
    }
    
    return amount;
  }
  
  /**
   * Get total amount in boxes
   * 
   * @param events events
   * @param totalHarvestedAmount totalHarvestedAmount
   * @return total amount in boxes
   */
  private double getTotalAmountInBoxes(List<Packing> packings) {
    double amount = 0;

    for (Packing packing : packings) {
      amount += packing.getPackedCount();
    }
    
    return amount;
  }
  
  /**
   * Get yield percentage
   * 
   * @param totalHarvestedAmount totalHarvestedAmount
   * @param totalAmountInBoxes totalAmountInBoxes
   * @return yield
   */
  private double getYield(double totalHarvestedAmount, double totalAmountInBoxes) {
    if (totalHarvestedAmount == 0) {
      return 0l;
    }
    
    return (totalAmountInBoxes * 100) / totalHarvestedAmount; 
  }
  
  /**
   * Get team
   * 
   * @param events events
   * @return team
   */
  private String getTeam(List<Event> events, Locale locale) {
    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = (HarvestEvent) event;
        if (harvestEvent.getProductionLine().getDefaultTeam() != null) {
          return localizedValueController.getValue(harvestEvent.getProductionLine().getDefaultTeam().getName(), locale);
        }
        break;
      }
    }
    return "";
  }
}
