package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
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
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.EventCountController;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;

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
  private LocalizedValueController localizedValueController;

  @Inject
  private EventCountController eventCountController;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.plantingYield.plantingYieldTitle"));

      int productIndex = 0;
      int inGuttersIndex = 1;
      int fromCellsIndex = 2;
      int yieldIndex = 3;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.plantingYield.plantingYieldTitle")); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime)); 
      xlsxBuilder.setCellValue(sheetId, 3, productIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldProduct"));
      xlsxBuilder.setCellValue(sheetId, 3, inGuttersIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldInGutters"));
      xlsxBuilder.setCellValue(sheetId, 3, fromCellsIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldFromCells")); 
      xlsxBuilder.setCellValue(sheetId, 3, yieldIndex, localesController.getString(locale, "reports.plantingYield.plantingYieldPercentage"));
      
      // Values

      List<Event> events = eventController.listByStartTimeAfterAndStartTimeBefore(parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
      Map<UUID, ReportRow> rowLookup = new HashMap<>();
      events.stream().forEach(event -> {
        Product product = event.getProduct();
        if (!rowLookup.containsKey(product.getId())) {
          rowLookup.put(
            product.getId(),
            new ReportRow(localizedValueController.getValue(product.getName(), locale),
            eventCountController.countUnitsByProductAndEventType(events, product, EventType.SOWING),
            eventCountController.countUnitsByProductAndEventType(events, product, EventType.PLANTING)
          ));
        }
      });

      int rowIndex = 4;
      List<ReportRow> rows = new ArrayList<>(rowLookup.values());
      Collections.sort(rows);

      for (ReportRow row : rows) {

        Double totalPlantedAmount = row.getSpreadAmount();
        Double totalAmountInGutters = row.getAmountInGutters();
        Double yield = getYield(totalPlantedAmount, totalAmountInGutters);

        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, row.getProductName());
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
   * Get yield percentage
   * 
   * @param totalPlantedAmount totalSowedAmount
   * @param totalAmountInGutters totalAmountInGutters
   * @return yield
   */
  private double getYield(Double totalPlantedAmount, Double totalAmountInGutters) {
    if (totalAmountInGutters == 0 || totalPlantedAmount == 0) {
      return 0d;
    }
    
    return (totalAmountInGutters * 100) / totalPlantedAmount; 
  }

  /**
   * Inner class representing single row in report
   */
  private class ReportRow implements Comparable<ReportRow>{

    public ReportRow(String productName, Double spreadAmount, Double amountInGutters) {
      this.productName = productName;
      this.spreadAmount = spreadAmount;
      this.amountInGutters = amountInGutters;
    }

    private Double spreadAmount;

    private Double amountInGutters;

    private String productName;

    /**
     * @return the productName
     */
    public String getProductName() {
      return productName;
    }

    @Override
    public int compareTo(ReportRow other) {
      return this.productName.compareToIgnoreCase(other.getProductName());
    }

    public Double getSpreadAmount() {
      return spreadAmount;
    }

    public Double getAmountInGutters() {
      return amountInGutters;
    }
  }

}
