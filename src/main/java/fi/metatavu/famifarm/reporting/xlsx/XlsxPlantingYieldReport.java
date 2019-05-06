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
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.PotType;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Report for planting yield
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class XlsxPlantingYieldReport extends AbstractXlsxReport {
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
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.plantingYield.plantingYieldTitle"));
      
      int teamIndex = 0;
      int productIndex = 1;
      int dateIndex = 2;
      int inGuttersIndex = 3;
      int fromCellsIndex = 4;
      int yieldIndex = 5;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.plantingYield.plantingYieldTitle")); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime)); 
      xlsxBuilder.setCellValue(sheetId, 3, teamIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldTeam")); 
      xlsxBuilder.setCellValue(sheetId, 3, productIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldProduct"));
      xlsxBuilder.setCellValue(sheetId, 3, dateIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldDate"));
      xlsxBuilder.setCellValue(sheetId, 3, inGuttersIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldInGutters"));
      xlsxBuilder.setCellValue(sheetId, 3, fromCellsIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldFromCells")); 
      xlsxBuilder.setCellValue(sheetId, 3, yieldIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldPercentage"));
      
      // Values
      
      List<Batch> batches = batchController.listBatches(null, null, null, null, null, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")), null, null);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      int rowIndex = 4;
      
      for (Batch batch : batches) {
        List<Event> events = eventController.listByBatchSortByStartTimeAsc(batch, null, null); 
        
        Product product = batch.getProduct();
        String dateString = getDateString(events, formatter);
        String team = getTeam(events, locale);
        int totalPlantedAmount = getTotalPlantedAmount(events);
        int totalAmountInGutters = getTotalAmounInGutters(events);
        long yield = getYield(totalPlantedAmount, totalAmountInGutters);
        
        xlsxBuilder.setCellValue(sheetId, rowIndex, teamIndex, team);
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, dateString);
        xlsxBuilder.setCellValue(sheetId, rowIndex, inGuttersIndex, totalAmountInGutters);
        xlsxBuilder.setCellValue(sheetId, rowIndex, fromCellsIndex, totalPlantedAmount);
        xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex,  yield);
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
      if (event.getType() == EventType.PLANTING) {
        PlantingEvent plantingEvent = (PlantingEvent) event;
        if (plantingEvent.getEndTime() != null) {
          date = plantingEvent.getEndTime();
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
   * @param events
   * @return total sowed amount
   */
  private int getTotalPlantedAmount(List<Event> events) {
    int amount = 0;
    
    SowingEvent sowingEvent = (SowingEvent) events.stream().filter(event -> event.getType() == EventType.SOWING).findFirst().orElse(null);
    if (sowingEvent == null) {
      return 0;
    }
    
    PotType potType = sowingEvent.getPotType();

    for (Event event : events) {
      if (event.getType() == EventType.PLANTING) {
        PlantingEvent plantingEvent = (PlantingEvent) event;
        amount += plantingEvent.getTrayCount() * getPotTypeAmount(potType);
      }
    }
    
    return amount;
  }
  
  /**
   * Get cell type as int
   * 
   * @param potType, potType
   * @return amount
   */
  private int getPotTypeAmount(PotType potType) {
    if (PotType.SMALL == potType) {
      return 54;
    }
    return 35;
  }
  
  /**
   * Get total amount in gutters
   * 
   * @param events events
   * @return total amount in gutters
   */
  private int getTotalAmounInGutters(List<Event> events) {
    int amount = 0;
    
    for (Event event : events) {
      if (event.getType() == EventType.PLANTING) {
        PlantingEvent plantingEvent = (PlantingEvent) event;
        amount += plantingEvent.getGutterHoleCount() * plantingEvent.getGutterCount();
      }
    }
    
    return amount;
  }
  
  /**
   * Get yield percentage
   * 
   * @param totalPlantedAmount totalSowedAmount
   * @param totalAmountInGutters totalAmountInGutters
   * @return yield
   */
  private long getYield(int totalPlantedAmount, int totalAmountInGutters) {
    if (totalAmountInGutters == 0) {
      return 0l;
    }
    
    return (totalAmountInGutters * 100) / totalPlantedAmount; 
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
