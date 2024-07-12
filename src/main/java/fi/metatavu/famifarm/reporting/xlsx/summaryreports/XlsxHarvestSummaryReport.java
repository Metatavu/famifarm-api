package fi.metatavu.famifarm.reporting.xlsx.summaryreports;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.HarvestEventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.xlsx.AbstractXlsxReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class XlsxHarvestSummaryReport extends AbstractXlsxReport {

  @Inject
  private EventController eventController;

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  @Inject
  private HarvestEventController harvestEventController;

  final int productIndex = 0;
  final int tableCountIndex = 1;
  final int yieldIndex = 2;
  final int basketCountIndex = 3;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String title = localesController.getString(locale, "reports.harvest_summary.title");
      String sheetId = xlsxBuilder.createSheet(title);
      OffsetDateTime toTime = parseDate(parameters.get("toTime"));
      OffsetDateTime fromTime = parseDate(parameters.get("fromTime"));

      List<HarvestEvent> events = getEntities(facility, fromTime, toTime);

      System.out.println("Found events: " + events.size());

      int rowIndex = 0;
      int columnIndex = 0;

      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, title);
      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, "reports.common.dateBetween", Date.from(fromTime.toInstant()), Date.from(toTime.toInstant())));
      rowIndex++;
      rowIndex++;

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, tableCountIndex, localesController.getString(locale, "reports.harvest_summary.tableCountHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex, localesController.getString(locale, "reports.harvest_summary.yieldHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, basketCountIndex, localesController.getString(locale, "reports.harvest_summary.basketCountHeader"));
      rowIndex++;

      Map<Product, RowInfo> products = new LinkedHashMap<>();

      for (HarvestEvent event : events) {
        Product product = event.getProduct();

        int tableCount = event.getGutterCount();
        double cropKg = getYield(event);
        int basketCount = getBasketCount(event);


        if (products.containsKey(product)) {
          RowInfo rowInfo = products.get(product);
          tableCount += rowInfo.getTableCount();
          cropKg += rowInfo.getYieldKg();
          basketCount += rowInfo.getBasketCount();
        }

        products.put(product, new RowInfo(tableCount, cropKg, basketCount));
      }

      for (Map.Entry<Product, RowInfo> productRow : products.entrySet()) {
        Product product = productRow.getKey();
        RowInfo rowInfo = productRow.getValue();

        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, tableCountIndex, rowInfo.getTableCount());
        xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex, rowInfo.getYieldKg());
        xlsxBuilder.setCellValue(sheetId, rowIndex, basketCountIndex, rowInfo.getBasketCount());
        rowIndex++;
      }

      int tableCountTotal = products.values().stream().mapToInt(RowInfo::getTableCount).sum();
      double yieldTotal = products.values().stream().mapToDouble(RowInfo::getYieldKg).sum();
      int basketCountTotal = products.values().stream().mapToInt(RowInfo::getBasketCount).sum();

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.harvest_summary.total"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, tableCountIndex, tableCountTotal);
      xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex, yieldTotal);
      xlsxBuilder.setCellValue(sheetId, rowIndex, basketCountIndex, basketCountTotal);

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  private List<HarvestEvent> getEntities(Facility facility, OffsetDateTime fromTime, OffsetDateTime toTime) {
    return eventController.listByTimeFrameAndType(facility, toTime, fromTime, EventType.HARVEST).stream()
      .map(event -> (HarvestEvent) event)
      .collect(Collectors.toList());
  }

  private Double getYield(HarvestEvent harvestEvent) {
    return harvestEventController.listHarvestBaskets(harvestEvent).stream().map(e -> (double) e.getWeight()).reduce(0d, Double::sum);
  }

  private Integer getBasketCount(HarvestEvent harvestEvent) {
    return harvestEventController.listHarvestBaskets(harvestEvent).size();
  }

  static class RowInfo {
    private final int tableCount;
    private final double yieldKg;
    private final int basketCount;

    public RowInfo(int tableCount, double yieldKg, int basketCount) {
      this.tableCount = tableCount;
      this.yieldKg = yieldKg;
      this.basketCount = basketCount;
    }

    public int getTableCount() {
      return tableCount;
    }

    public double getYieldKg() {
      return yieldKg;
    }

    public int getBasketCount() {
      return basketCount;
    }
  }

}
