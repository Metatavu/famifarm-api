package fi.metatavu.famifarm.reporting.xlsx.workhoursummary;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.persistence.dao.PackingBasketDAO;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.reporting.xlsx.listreports.data.PackingData;
import fi.metatavu.famifarm.reporting.xlsx.workhours.AbstractWorkHoursReport;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.OutputStream;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class XlsxPackingWorkHourSummary extends AbstractWorkHoursReport {

  @Inject
  private EventController eventController;

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  @Inject
  private PackingController packingController;

  @Inject
  private PackingBasketDAO packingBasketDAO;

  final int productIndex = 0;
  final int durationIndex = 1;
  final int bagsIndex = 2;
  final int boxesIndex = 3;
  final int bagsPerHourIndex = 4;
  final int boxesPerHourIndex = 5;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String title = localesController.getString(locale, "reports.packing_work_hours_summary.title");
      String sheetId = xlsxBuilder.createSheet(title);
      OffsetDateTime toTime = parseDate(parameters.get("toTime"));
      OffsetDateTime fromTime = parseDate(parameters.get("fromTime"));

      List<PackingData> events = getEntities(facility, fromTime, toTime);

      int rowIndex = 0;
      int columnIndex = 0;

      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, title);
      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, "reports.common.dateBetween", Date.from(fromTime.toInstant()), Date.from(toTime.toInstant())));
      rowIndex++;
      rowIndex++;

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, localesController.getString(locale, "reports.packing_work_hours.duration"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, bagsIndex, localesController.getString(locale, "reports.packing_work_hours.bags"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, boxesIndex, localesController.getString(locale, "reports.packing_work_hours.boxes"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, bagsPerHourIndex, localesController.getString(locale, "reports.packing_work_hours.bagsPerHour"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, boxesPerHourIndex, localesController.getString(locale, "reports.packing_work_hours.boxesPerHour"));

      rowIndex++;

      Map<Product, ProductPackingStats> ProductPackingStatsMap = new LinkedHashMap<>();

      System.out.println("Event list has " + events.size() + " entries");

      for (PackingData event : events) {
        Product product = event.getPacking().getProduct();
        Duration duration = Duration.between(event.getPacking().getStartTime(), event.getPacking().getEndTime()).abs();
        long boxes = event.getPacking().getPackedCount();
        long bags = event.getPacking().getPackageSize().getSize() * boxes;

        ProductPackingStatsMap.computeIfAbsent(product, k -> new ProductPackingStats()).durationMinutes += duration.toMinutes();
        ProductPackingStatsMap.computeIfAbsent(product, k -> new ProductPackingStats()).bags += bags;
        ProductPackingStatsMap.computeIfAbsent(product, k -> new ProductPackingStats()).boxes += boxes;
      }

      System.out.println("Packing map has " + ProductPackingStatsMap.size() + " entries");

      for (Map.Entry<Product, ProductPackingStats> entry : ProductPackingStatsMap.entrySet()) {
        Product product = entry.getKey();
        ProductPackingStats stats = entry.getValue();
        Duration duration = Duration.ofMinutes(stats.durationMinutes);

        double durationHours = duration.toHours() + (double) duration.toMinutesPart() / 60;

        if (durationHours == 0) {
          durationHours = 1;
        }

        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, formatDuration(duration));
        xlsxBuilder.setCellValue(sheetId, rowIndex, bagsIndex, stats.bags);
        xlsxBuilder.setCellValue(sheetId, rowIndex, boxesIndex, stats.boxes);
        xlsxBuilder.setCellValue(sheetId, rowIndex, bagsPerHourIndex, stats.bags / durationHours);
        xlsxBuilder.setCellValue(sheetId, rowIndex, boxesPerHourIndex, stats.boxes / durationHours);

        rowIndex++;
      }

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.work_hours_summary.combined"));
      long totalDurationMinutes = ProductPackingStatsMap.values().stream().mapToLong(value -> value.durationMinutes).sum();
      long totalBags = ProductPackingStatsMap.values().stream().mapToLong(value -> value.bags).sum();
      long totalBoxes = ProductPackingStatsMap.values().stream().mapToLong(value -> value.boxes).sum();

      Duration duration = Duration.ofMinutes(totalDurationMinutes);
      double durationHours = duration.toHours() + (double) duration.toMinutesPart() / 60;

      if (durationHours == 0) {
        durationHours = 1;
      }

      xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, formatDuration(duration));

      xlsxBuilder.setCellValue(sheetId, rowIndex, bagsIndex, totalBags);
      xlsxBuilder.setCellValue(sheetId, rowIndex, boxesIndex, totalBoxes);
      xlsxBuilder.setCellValue(sheetId, rowIndex, bagsPerHourIndex, totalBags / durationHours);
      xlsxBuilder.setCellValue(sheetId, rowIndex, boxesPerHourIndex, totalBoxes / durationHours);

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  private List<PackingData> getEntities(Facility facility, OffsetDateTime fromTime, OffsetDateTime toTime) {
    List<Packing> packings = packingController.listPackings(
      null,
      null,
      facility,
      null,
      null,
      null,
      toTime,
      fromTime
    );
    return packings.stream().map(
      packing -> new PackingData(
        packing,
        packingBasketDAO.listByPacking(packing)
      )
    ).collect(Collectors.toList());
  }

  static class ProductPackingStats {
    long durationMinutes;
    long bags;
    long boxes;
  }
}
