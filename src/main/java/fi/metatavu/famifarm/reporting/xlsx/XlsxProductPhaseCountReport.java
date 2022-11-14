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
 * Report for counting products in different phases
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class XlsxProductPhaseCountReport extends AbstractXlsxReport {

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
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.productPhaseCount.title"));
      
      int productIndex = 0;
      int sowingIndex = 1;
      int spreadIndex = 2;
      int plantingIndex = 3;
      int harvestIndex = 4;
      int packingIndex = 5;
      int wastageIndex = 6;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.productPhaseCount.title"));
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime)); 

      xlsxBuilder.setCellValue(sheetId, 4, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, 4, sowingIndex, localesController.getString(locale, "reports.sowed.title"));
      xlsxBuilder.setCellValue(sheetId, 4, spreadIndex, localesController.getString(locale, "reports.spread.title"));
      xlsxBuilder.setCellValue(sheetId, 4, plantingIndex, localesController.getString(locale, "reports.planted.title"));
      xlsxBuilder.setCellValue(sheetId, 4, harvestIndex, localesController.getString(locale, "reports.harvested.title"));
      xlsxBuilder.setCellValue(sheetId, 4, packingIndex, localesController.getString(locale, "reports.packed.title"));
      xlsxBuilder.setCellValue(sheetId, 4, wastageIndex, localesController.getString(locale, "reports.wastage.title"));

      // Values

      List<Event> events = eventController.listByStartTimeAfterAndStartTimeBefore(parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
      Map<UUID, ReportRow> rowLookup = new HashMap<>();
      events.stream().forEach(event -> {
        Product product = event.getProduct();
        if (!rowLookup.containsKey(product.getId())) {
          ReportRow row = new ReportRow(localizedValueController.getValue(product.getName(), locale));
          row.setSowedCount(eventCountController.countUnitsByProductAndEventType(events, product, EventType.SOWING));
          row.setSpreadCount(eventCountController.countUnitsByProductAndEventType(events, product, EventType.TABLE_SPREAD));
          row.setPlantedCount(eventCountController.countUnitsByProductAndEventType(events, product, EventType.PLANTING));
          row.setHarvestedCount(eventCountController.countUnitsByProductAndEventType(events, product, EventType.HARVEST));
          row.setWastedCount(eventCountController.countUnitsByProductAndEventType(events, product, EventType.WASTAGE));
        }
      });

      int rowIndex = 5;
      List<ReportRow> rows = new ArrayList<>(rowLookup.values());
      Collections.sort(rows);

      for (ReportRow row : rows) {
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, row.getProductName());
        xlsxBuilder.setCellValue(sheetId, rowIndex, sowingIndex, row.getSowedCount());
        xlsxBuilder.setCellValue(sheetId, rowIndex, spreadIndex, row.getSpreadCount());
        xlsxBuilder.setCellValue(sheetId, rowIndex, plantingIndex, row.getPlantedCount());
        xlsxBuilder.setCellValue(sheetId, rowIndex, harvestIndex, row.getHarvestedCount());
        xlsxBuilder.setCellValue(sheetId, rowIndex, packingIndex, row.getPackedCount());
        xlsxBuilder.setCellValue(sheetId, rowIndex, wastageIndex, row.getWastedCount());
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

    public ReportRow(String productName) {
      this.productName = productName;
    }

    private Double sowedCount;
    
    private Double spreadCount;
    
    private Double plantedCount;
    
    private Double harvestedCount;
    
    private Double packedCount;
    
    private Double wastedCount;

    private String productName;

    public String getProductName() {
      return productName;
    }

    public Double getSowedCount() {
      return sowedCount;
    }

    public void setSowedCount(Double sowedCount) {
      this.sowedCount = sowedCount;
    }

    public Double getSpreadCount() {
      return spreadCount;
    }

    public void setSpreadCount(Double spreadCount) {
      this.spreadCount = spreadCount;
    }

    public Double getPlantedCount() {
      return plantedCount;
    }

    public void setPlantedCount(Double plantedCount) {
      this.plantedCount = plantedCount;
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

    public Double getWastedCount() {
      return wastedCount;
    }

    public void setWastedCount(Double wastedCount) {
      this.wastedCount = wastedCount;
    }

    @Override
    public int compareTo(ReportRow other) {
      return this.productName.compareToIgnoreCase(other.getProductName());
    }
  }
}
