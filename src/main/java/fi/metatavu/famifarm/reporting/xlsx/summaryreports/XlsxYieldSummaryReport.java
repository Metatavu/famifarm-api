package fi.metatavu.famifarm.reporting.xlsx.summaryreports;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.HarvestEventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
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
public class XlsxYieldSummaryReport extends AbstractXlsxReport {

  @Inject
  private LocalizedValueController localizedValueController;

  @Inject
  private EventController eventController;

  @Inject
  private LocalesController localesController;

  @Inject
  private PackingController packingController;

  @Inject
  private HarvestEventController harvestEventController;

  final int productIndex = 0;
  final int basketsCollectedIndex = 1;
  final int wastageIndex = 2;
  final int packingIndex = 3;
  final int packedFromTotalIndex = 4;
  final int packedFromCollectedIndex = 5;


  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String title = localesController.getString(locale, "reports.yield_summary.title");
      String sheetId = xlsxBuilder.createSheet(title);
      OffsetDateTime toTime = parseDate(parameters.get("toTime"));
      OffsetDateTime fromTime = parseDate(parameters.get("fromTime"));

      int rowIndex = 0;

      xlsxBuilder.setCellValue(sheetId, rowIndex, 0, title);
      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, 0, localesController.getString(locale, "reports.common.dateBetween", Date.from(fromTime.toInstant()), Date.from(toTime.toInstant())));
      rowIndex++;
      rowIndex++;

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, basketsCollectedIndex, localesController.getString(locale, "reports.harvest_summary.tableCountHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, wastageIndex, localesController.getString(locale, "reports.harvest_summary.yieldHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, packingIndex, localesController.getString(locale, "reports.harvest_summary.basketCountHeader"));
      rowIndex++;

      List<HarvestEvent> harvestEvents = eventController.listByTimeFrameAndType(facility, toTime, fromTime, EventType.HARVEST).stream().map(HarvestEvent.class::cast).collect(Collectors.toList());
      List<WastageEvent> wastageEvents = eventController.listByTimeFrameAndType(facility, toTime, fromTime, EventType.WASTAGE).stream().map(WastageEvent.class::cast).collect(Collectors.toList());
      List<Packing> packingEvents = packingController.listPackings(null,null,facility,null,null,null,toTime,fromTime);

      Map<Product, YieldSummaryRow> productYieldSummaryRows = new LinkedHashMap<>();

      for (HarvestEvent harvestEvent: harvestEvents) {
        Product product = harvestEvent.getProduct();
        YieldSummaryRow yieldSummaryRow = productYieldSummaryRows.computeIfAbsent(product, k -> new YieldSummaryRow());
        yieldSummaryRow.harvestAmount += getBasketCount(harvestEvent);
        productYieldSummaryRows.put(product, yieldSummaryRow);
      }

      System.out.println("Found: " + productYieldSummaryRows.size());

      for (WastageEvent wastageEvent: wastageEvents) {
        Product product = wastageEvent.getProduct();
        YieldSummaryRow yieldSummaryRow = productYieldSummaryRows.computeIfAbsent(product, k -> new YieldSummaryRow());
        yieldSummaryRow.wastageAmount += wastageEvent.getAmount();
        productYieldSummaryRows.put(product, yieldSummaryRow);
      }

      for (Packing packingEvent: packingEvents) {
        Product product = packingEvent.getProduct();
        YieldSummaryRow yieldSummaryRow = productYieldSummaryRows.computeIfAbsent(product, k -> new YieldSummaryRow());
        yieldSummaryRow.packingAmount += packingEvent.getPackedCount();
        productYieldSummaryRows.put(product, yieldSummaryRow);
      }

      for (Map.Entry<Product, YieldSummaryRow> entry: productYieldSummaryRows.entrySet()) {
        Product product = entry.getKey();
        System.out.println("Product: " + localizedValueController.getValue(product.getName(), locale) + " " + entry.getValue().harvestAmount + " " + entry.getValue().wastageAmount + " " + entry.getValue().packingAmount);
        YieldSummaryRow yieldSummaryRow = entry.getValue();
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, basketsCollectedIndex, yieldSummaryRow.harvestAmount);
        xlsxBuilder.setCellValue(sheetId, rowIndex, wastageIndex, yieldSummaryRow.wastageAmount);
        xlsxBuilder.setCellValue(sheetId, rowIndex, packingIndex, yieldSummaryRow.packingAmount);

        double packedFromCollected = (double) yieldSummaryRow.packingAmount / yieldSummaryRow.harvestAmount;
        xlsxBuilder.setCellValue(sheetId, rowIndex, packedFromTotalIndex, packedFromCollected * 100 + "%");

        double packedFromTotal = (double) yieldSummaryRow.packingAmount / (yieldSummaryRow.harvestAmount - yieldSummaryRow.wastageAmount);
        xlsxBuilder.setCellValue(sheetId, rowIndex, packedFromCollectedIndex, packedFromTotal * 100 + "%");
        rowIndex++;
      }

      int totalHarvestAmount = productYieldSummaryRows.values().stream().mapToInt(row -> row.harvestAmount).sum();
      int totalWastageAmount = productYieldSummaryRows.values().stream().mapToInt(row -> row.wastageAmount).sum();
      int totalPackingAmount = productYieldSummaryRows.values().stream().mapToInt(row -> row.packingAmount).sum();

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.harvest_summary.total"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, basketsCollectedIndex, totalHarvestAmount);
      xlsxBuilder.setCellValue(sheetId, rowIndex, wastageIndex, totalWastageAmount);
      xlsxBuilder.setCellValue(sheetId, rowIndex, packingIndex, totalPackingAmount);

      double totalPackedFromCollected = (double) totalPackingAmount / totalHarvestAmount;
      xlsxBuilder.setCellValue(sheetId, rowIndex, packedFromTotalIndex, totalPackedFromCollected * 100 + "%");

      double totalPackedFromTotal = (double) totalPackingAmount / (totalHarvestAmount - totalWastageAmount);
      xlsxBuilder.setCellValue(sheetId, rowIndex, packedFromCollectedIndex, totalPackedFromTotal * 100 + "%");

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  private Integer getBasketCount(HarvestEvent harvestEvent) {
    return harvestEventController.listHarvestBaskets(harvestEvent).size();
  }

  static class YieldSummaryRow {
    int harvestAmount;
    int wastageAmount;
    int packingAmount;
  }
}
