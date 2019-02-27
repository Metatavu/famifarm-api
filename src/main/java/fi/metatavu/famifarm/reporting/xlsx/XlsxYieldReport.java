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
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.CellType;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum;

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

  @Override
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.wastage.title"));
      
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
      
      List<Batch> batches = batchController.listBatches(null, null, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      int rowIndex = 4;
      
      for (Batch batch : batches) {
        List<Event> events = eventController.listByBatchSortByStartTimeAsc(batch, null, null); 
        
        Product product = batch.getProduct();
        String dateString = getDateString(events, formatter);
        String team = getTeam(events, locale);
        int totalSowedAmount = getTotalSowedAmount(events);
        int totalHarvestedAmount = getTotalHarvestedAmount(events);
        int totalAmountInBoxes = getTotalAmounInBoxes(events, totalHarvestedAmount);
        long yield = getYield(totalHarvestedAmount, totalAmountInBoxes);
        
        xlsxBuilder.setCellValue(sheetId, rowIndex, teamIndex, team);
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, dateString);
        xlsxBuilder.setCellValue(sheetId, rowIndex, harvestedIndex, Integer.toString(totalHarvestedAmount));
        xlsxBuilder.setCellValue(sheetId, rowIndex, inBoxesIndex, Integer.toString(totalAmountInBoxes));
        xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex,  Long.toString(yield));
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
   * Get total sowed amount
   * 
   * @param events events
   * @return total sowed amount
   */
  private int getTotalSowedAmount(List<Event> events) {
    int totalSowedAmount = 0;
    for (Event event : events) {
      if (event.getType() == EventType.SOWING) {
        SowingEvent sowingEvent = (SowingEvent) event;
        totalSowedAmount += sowingEvent.getAmount() * getCellTypeAmount(sowingEvent.getCellType());
      }
    }
    return totalSowedAmount;
  }
  
  /**
   * Get cell type as int
   * 
   * @param cellType, cellType
   * @return amount
   */
  private int getCellTypeAmount(CellType cellType) {
    if (CellType.SMALL == cellType) {
      return 54;
    }
    return 35;
  }
  
  /**
   * Get total harvested amount
   * 
   * @param events
   * @return total sowed amount
   */
  private int getTotalHarvestedAmount(List<Event> events) {
    int amount = 0;
    
    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = (HarvestEvent) event;
        amount += harvestEvent.getAmount();
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
  private int getTotalAmounInBoxes(List<Event> events, int totalHarvestedAmount) {
    int amount = 0;
    
    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = (HarvestEvent) event;
        if (harvestEvent.getHarvestType() == TypeEnum.BOXING) {
          amount += harvestEvent.getAmount();
        }
      }
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
  private long getYield(int totalHarvestedAmount, int totalAmountInBoxes) {
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
