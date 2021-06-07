package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.time.OffsetDateTime;
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
      int wastageFromProductionLineIndex = 4;
      int wastageFromStorageIndex = 5;
      int totalYieldIndex = 6;
      
      OffsetDateTime fromTimeOffset = parseDate(parameters.get("fromTime"));
      OffsetDateTime toTimeOffset = parseDate(parameters.get("toTime"));

      Date fromTime = Date.from(fromTimeOffset.toInstant());
      Date toTime = Date.from(toTimeOffset.toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.yield.yieldTitle")); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime));  
      xlsxBuilder.setCellValue(sheetId, 3, productIndex, localesController.getString(locale, "reports.yield.yieldProduct"));
      xlsxBuilder.setCellValue(sheetId, 3, harvestedIndex, localesController.getString(locale, "reports.yield.yieldHarvested"));
      xlsxBuilder.setCellValue(sheetId, 3, inBoxesIndex, localesController.getString(locale, "reports.yield.yieldInBoxes")); 
      xlsxBuilder.setCellValue(sheetId, 3, yieldIndex, localesController.getString(locale, "reports.yield.yieldPercentage"));
      xlsxBuilder.setCellValue(sheetId, 3, wastageFromProductionLineIndex, localesController.getString(locale, "reports.yield.yieldWastageFromProductionLine"));
      xlsxBuilder.setCellValue(sheetId, 3, wastageFromStorageIndex, localesController.getString(locale, "reports.yield.yieldWastageFromStorage"));
      xlsxBuilder.setCellValue(sheetId, 3, totalYieldIndex, localesController.getString(locale, "reports.yield.yieldTotalPercentage"));
      
      // Values
      
      List<Event> events = eventController.listByStartTimeAfterAndStartTimeBefore(toTimeOffset, fromTimeOffset);
      List<Packing> packings = packingController.listPackings(null, null, null, null, null, toTimeOffset, fromTimeOffset);
      Map<UUID, ReportRow> rowLookup = new HashMap<>();
      events.stream().forEach(event -> {
        Product product = event.getProduct();
        if (!rowLookup.containsKey(product.getId())) {
          rowLookup.put(
            product.getId(),
            new ReportRow(
              localizedValueController.getValue(product.getName(), locale),
              eventCountController.countUnitsByProductAndEventType(events, product, EventType.HARVEST),
              eventCountController.countPackedUnitsByProduct(packings, product),
              eventCountController.countUnitsByProductAndEventType(events, product, EventType.WASTAGE),
              eventCountController.countWastedPackedUnitsByProduct(product, toTimeOffset, fromTimeOffset)
            )
          );
        }
      });

      int rowIndex = 4;
      List<ReportRow> rows = new ArrayList<>(rowLookup.values());
      Collections.sort(rows);
      
      for (ReportRow row : rows) {
        double totalHarvestedAmount = row.getHarvestedCount();
        double totalAmountInBoxes = row.getPackedCount();
        double wastageFromProductionLine = -1 * row.getWastageFromProductionLine();
        double wastageFromStorage = -1 * row.getWastageFromStorage();

        String harvestedCellAdress = xlsxBuilder.getCellAddress(sheetId, rowIndex, harvestedIndex);
        String inBoxesCellAdress = xlsxBuilder.getCellAddress(sheetId, rowIndex, inBoxesIndex);
        String wastageFromStorageCellAdress = xlsxBuilder.getCellAddress(sheetId, rowIndex, wastageFromStorageIndex);
        String wastageFromProductionLineCellAdress = xlsxBuilder.getCellAddress(sheetId, rowIndex, wastageFromProductionLineIndex);
        
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, row.getProductName());
        xlsxBuilder.setCellValue(sheetId, rowIndex, harvestedIndex, totalHarvestedAmount);
        xlsxBuilder.setCellValue(sheetId, rowIndex, inBoxesIndex, totalAmountInBoxes);
        xlsxBuilder.setCellFormula(sheetId, rowIndex, yieldIndex,  String.format("%s/%s", inBoxesCellAdress, harvestedCellAdress));
        xlsxBuilder.setCellValue(sheetId, rowIndex, wastageFromProductionLineIndex,  wastageFromProductionLine);
        xlsxBuilder.setCellValue(sheetId, rowIndex, wastageFromStorageIndex,  wastageFromStorage);
        xlsxBuilder.setCellFormula(sheetId, rowIndex, totalYieldIndex,  
          String.format(
            "(%s+%s)/(%s-%s)",
            inBoxesCellAdress,
            wastageFromStorageCellAdress,
            harvestedCellAdress,
            wastageFromProductionLineCellAdress));

        rowIndex++;
      }

      
      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  /**
   * Inner class representing single row in report
   */
  private class ReportRow implements Comparable<ReportRow>{

    public ReportRow(String productName,
      Double harvestedCount,
      Double packedCount,
      Double wastageFromProductionLine,
      Double wastageFromStorage) {

      this.productName = productName;
      this.harvestedCount = harvestedCount;
      this.packedCount = packedCount;
      this.wastageFromProductionLine = wastageFromProductionLine;
      this.wastageFromStorage = wastageFromStorage;
    }

    private Double harvestedCount;

    private Double packedCount;

    private Double wastageFromProductionLine;

    private Double wastageFromStorage;

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

    public Double getPackedCount() {
      return packedCount;
    }

    public Double getWastageFromProductionLine() {
      return wastageFromProductionLine;
    }

    public Double getWastageFromStorage() {
      return wastageFromStorage;
    }
  }
}
