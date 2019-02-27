package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import fi.metatavu.famifarm.batches.BatchController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.HarvestEventController;
import fi.metatavu.famifarm.events.SowingEventController;
import fi.metatavu.famifarm.events.WastageEventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.CellType;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum;

/**
 * Report for yield
 * 
 * @author Ville Koivukangas
 */
public class XlsxYieldReport extends AbstractXlsxReport {
  @Inject
  private LocalesController localesController;
  
  @Inject
  private EventController eventController;
  
  @Inject
  private WastageEventController wastageEventController;
  
  @Inject
  private HarvestEventController harvestEventController;
  
  @Inject
  private SowingEventController sowingEventController;
  
  @Inject
  private BatchController batchController;
  
  @Inject
  private LocalizedValueController localizedValueController;
  
  private long yield = 0;
  
  private int totalSowedAmount = 0;
  
  private int totalHarvestedAmount = 0;
  
  private int totalAmountInBoxes = 0;
  
  private String team = "-";
  
  private String dateString = "";

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
        setDateString(events, formatter);
        setTeam(events, locale);
        setTotalSowedAmount(events);
        setTotalHarvestedAmount(events);
        setTotalAmounInBoxes(events);
        setYield();
        
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
   * Set date
   * 
   * @param events events
   * @param formatter formatter
   */
  private void setDateString(List<Event> events, DateTimeFormatter formatter) {
    OffsetDateTime date = null;
    
    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = harvestEventController.findHarvestEventById(event.getId());
        if (harvestEvent.getEndTime() != null) {
          date = harvestEvent.getEndTime();
        }
      }
    }
    
    dateString = date.format(formatter);
  }
  
  /**
   * Set total sowed amount
   * 
   * @param events events
   */
  private void setTotalSowedAmount(List<Event> events) {
    totalSowedAmount = 0;
    for (Event event : events) {
      if (event.getType() == EventType.SOWING) {
        SowingEvent sowingEvent = sowingEventController.findSowingEventById(event.getId());
        
        if (sowingEvent != null) {
          totalSowedAmount += sowingEvent.getAmount() * getCellTypeAmount(sowingEvent.getCellType());
        }
      }
    }
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
   * Set total harvested amount
   * 
   * @param events
   */
  private void setTotalHarvestedAmount(List<Event> events) {
    int remainingUnitsOnLastPacking = 0;
    
    for (Event event : events) {
      if (event.getType() == EventType.PACKING) {
        remainingUnitsOnLastPacking = event.getRemainingUnits();
      }
    }
    
    totalHarvestedAmount = totalSowedAmount - remainingUnitsOnLastPacking;
  }
  
  /**
   * Set total amount in boxes
   * 
   * @param events
   */
  private void setTotalAmounInBoxes(List<Event> events) {
    int remainingUnitsOnLastBoxing = 0;
    int totalWastageAmount = 0;
    
    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = harvestEventController.findHarvestEventById(event.getId());
        if (harvestEvent.getHarvestType() == TypeEnum.BOXING) {
          remainingUnitsOnLastBoxing = event.getRemainingUnits();
        }
      } else if (event.getType() == EventType.WASTEAGE) {
        WastageEvent wastageEvent = wastageEventController.findWastageEventById(event.getId());
        totalWastageAmount += wastageEvent.getAmount();
      }
    }
    totalAmountInBoxes = (totalSowedAmount - remainingUnitsOnLastBoxing) - totalWastageAmount;
  }
  
  /**
   * Set yield percentage
   * 
   */
  private void setYield() {
    yield = (totalAmountInBoxes * 100) / totalHarvestedAmount; 
  }
  
  /**
   * Set team id
   * 
   * @param events events
   */
  private void setTeam(List<Event> events, Locale locale) {
    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = harvestEventController.findHarvestEventById(event.getId());
        if (harvestEvent.getProductionLine().getDefaultTeam() != null) {
          team = localizedValueController.getValue(harvestEvent.getProductionLine().getDefaultTeam().getName(), locale);
        }
        break;
      }
    }
  }
}
