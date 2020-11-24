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
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.EventCountController;
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
  private LocalizedValueController localizedValueController;
  
  @Inject
  private PackingController packingController;

  @Inject
  private EventCountController eventCountController;

  @Override
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.yield.yieldTitle"));
      
      int productIndex = 0;
      int harvestedIndex = 1;
      int inBoxesIndex = 2;
      int yieldIndex = 3;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.yield.yieldTitle")); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime));  
      xlsxBuilder.setCellValue(sheetId, 3, productIndex, localesController.getString(locale, "reports.yield.yieldProduct"));
      xlsxBuilder.setCellValue(sheetId, 3, harvestedIndex, localesController.getString(locale, "reports.yield.yieldHarvested"));
      xlsxBuilder.setCellValue(sheetId, 3, inBoxesIndex, localesController.getString(locale, "reports.yield.yieldInBoxes")); 
      xlsxBuilder.setCellValue(sheetId, 3, yieldIndex, localesController.getString(locale, "reports.yield.yieldPercentage"));
      
      // Values
      
      List<Event> events = eventController.listByStartTimeAfterAndStartTimeBefore(parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
      List<Packing> packings = packingController.listPackings(null, null, null, null, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
      Map<UUID, ReportRow> rowLookup = new HashMap<>();
      events.stream().forEach(event -> {
        Product product = event.getBatch().getProduct();
        if (!rowLookup.containsKey(product.getId())) {
          rowLookup.put(
            product.getId(),
            new ReportRow(localizedValueController.getValue(product.getName(), locale),
            eventCountController.countUnitsByProductAndEventType(events, product, EventType.HARVEST),
            eventCountController.countPackedUnitsByProduct(packings, product))
          );
        }
      });

      int rowIndex = 4;
      List<ReportRow> rows = new ArrayList<>(rowLookup.values());
      Collections.sort(rows);
      
      for (ReportRow row : rows) {
        double totalHarvestedAmount = row.getHarvestedCount();
        double totalAmountInBoxes = row.getPackedCount();
        double yield = getYield(totalHarvestedAmount, totalAmountInBoxes);
        
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, row.getProductName());
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
   * Get yield percentage
   * 
   * @param totalHarvestedAmount totalHarvestedAmount
   * @param totalAmountInBoxes totalAmountInBoxes
   * @return yield
   */
  private double getYield(double totalHarvestedAmount, double totalAmountInBoxes) {
    if (totalHarvestedAmount == 0 || totalAmountInBoxes == 0) {
      return 0d;
    }
    
    return (totalAmountInBoxes * 100) / totalHarvestedAmount; 
  }

  /**
   * Inner class representing single row in report
   */
  private class ReportRow implements Comparable<ReportRow>{

    public ReportRow(String productName, Double harvestedCount, Double packedCount) {
      this.productName = productName;
      this.harvestedCount = harvestedCount;
      this.packedCount = packedCount;
    }

    private Double harvestedCount;

    private Double packedCount;

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

    public Double getHarvestedCount() {
      return harvestedCount;
    }

    public void setHarvestedCount(Double harvestedCount) {
      this.harvestedCount = harvestedCount;
    }

    public Double getPackedCount() {
      return packedCount;
    }

    public void setPackedCount(Double packedCount) {
      this.packedCount = packedCount;
    }
  }
}
