package fi.metatavu.famifarm.reporting.xlsx.workhoursummary;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.xlsx.AbstractXlsxReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.reporting.xlsx.workhours.AbstractWorkHoursReport;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.OutputStream;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

@ApplicationScoped
public class XlsxSowingWorkHourSummary extends AbstractWorkHoursReport {

  @Inject
  private EventController eventController;

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  final int productIndex = 0;
  final int durationIndex = 1;
  final int amountIndex = 2;
  final int cartsIndex = 3;
  final int tablesPerHourIndex = 4;
  final int cartsPerHourIndex = 5;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String title = localesController.getString(locale, "reports.sowing_summary.title");
      String sheetId = xlsxBuilder.createSheet(title);
      OffsetDateTime toTime = parseDate(parameters.get("toTime"));
      OffsetDateTime fromTime = parseDate(parameters.get("fromTime"));

      List<Event> events = eventController.listByTimeFrameAndType(facility, toTime, fromTime, EventType.SOWING);

      int rowIndex = 0;
      int columnIndex = 0;

      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, title);
      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, "reports.common.dateBetween", Date.from(fromTime.toInstant()), Date.from(toTime.toInstant())));
      rowIndex++;
      rowIndex++;

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, localesController.getString(locale, "reports.sowing_work_hours.duration"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, localesController.getString(locale, "reports.sowing_work_hours.amount"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, cartsIndex, localesController.getString(locale, "reports.sowing_work_hours.cartCount"));

      xlsxBuilder.setCellValue(sheetId, rowIndex, tablesPerHourIndex, localesController.getString(locale, "reports.sowing_work_hours.amountPerHour"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, cartsPerHourIndex, localesController.getString(locale, "reports.sowing_work_hours.cartPerHour"));

      rowIndex++;

      Map<Product, ProductSowingStats> productPlantingStatsMap = new LinkedHashMap<>();

      for (Event event : events) {
        SowingEvent sowingEvent = (SowingEvent) event;
        Product product = sowingEvent.getProduct();
        Duration duration = Duration.between(sowingEvent.getStartTime(), sowingEvent.getEndTime()).abs();
        long amount = sowingEvent.getAmount();

        productPlantingStatsMap.computeIfAbsent(product, k -> new ProductSowingStats()).durationMinutes += duration.toMinutes();
        productPlantingStatsMap.computeIfAbsent(product, k -> new ProductSowingStats()).amount += amount;
        productPlantingStatsMap.computeIfAbsent(product, k -> new ProductSowingStats()).carts += (double) amount / 32;
      }

      for (Map.Entry<Product, ProductSowingStats> entry : productPlantingStatsMap.entrySet()) {
        Product product = entry.getKey();
        ProductSowingStats stats = entry.getValue();
        Duration duration = Duration.ofMinutes(stats.durationMinutes);

        double durationHours = duration.toHours() + (double) duration.toMinutesPart() / 60;

        if (durationHours == 0) {
          durationHours = 1;
        }

        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, formatDuration(duration));
        xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, stats.amount);
        xlsxBuilder.setCellValue(sheetId, rowIndex, cartsIndex, stats.carts);
        xlsxBuilder.setCellValue(sheetId, rowIndex, tablesPerHourIndex, stats.amount / durationHours);
        xlsxBuilder.setCellValue(sheetId, rowIndex, cartsPerHourIndex, stats.carts / durationHours);

        rowIndex++;
      }

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.work_hours_summary.combined"));

      long combinedDuration = productPlantingStatsMap.values().stream().mapToLong(value -> value.durationMinutes).sum();
      long combinedAmount = productPlantingStatsMap.values().stream().mapToLong(value -> value.amount).sum();
      double combinedCarts = productPlantingStatsMap.values().stream().mapToDouble(value -> value.carts).sum();

      Duration duration = Duration.ofMinutes(combinedDuration);

      double durationHours = duration.toHours() + (double) duration.toMinutesPart() / 60;

      if (durationHours == 0) {
        durationHours = 1;
      }

      xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, formatDuration(duration));
      xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, combinedAmount);
      xlsxBuilder.setCellValue(sheetId, rowIndex, cartsIndex, combinedCarts);
      xlsxBuilder.setCellValue(sheetId, rowIndex, tablesPerHourIndex, combinedAmount / durationHours);
      xlsxBuilder.setCellValue(sheetId, rowIndex, cartsPerHourIndex, combinedCarts / durationHours);

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  static class ProductSowingStats {
    long durationMinutes;
    long amount;
    double carts;
  }
}
