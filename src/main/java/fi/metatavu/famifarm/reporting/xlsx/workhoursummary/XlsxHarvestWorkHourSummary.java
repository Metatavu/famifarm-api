package fi.metatavu.famifarm.reporting.xlsx.workhoursummary;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.HarvestEventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.Product;
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
public class XlsxHarvestWorkHourSummary extends AbstractWorkHoursReport {

  @Inject
  private EventController eventController;

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  @Inject
  private HarvestEventController harvestEventController;

  final int productIndex = 0;
  final int durationIndex = 1;
  final int amountIndex = 2;
  final int yieldIndex = 3;
  final int amountPerHourIndex = 4;
  final int yieldPerHour = 5;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String title = localesController.getString(locale, "reports.harvest_work_hours_summary.title");
      String sheetId = xlsxBuilder.createSheet(title);
      OffsetDateTime toTime = parseDate(parameters.get("toTime"));
      OffsetDateTime fromTime = parseDate(parameters.get("fromTime"));

      List<Event> events = eventController.listByTimeFrameAndType(facility, toTime, fromTime, EventType.HARVEST);

      int rowIndex = 0;
      int columnIndex = 0;

      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, title);
      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, "reports.common.dateBetween", Date.from(fromTime.toInstant()), Date.from(toTime.toInstant())));
      rowIndex++;
      rowIndex++;

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, localesController.getString(locale, "reports.harvest_work_hours.duration"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, localesController.getString(locale, "reports.harvest_work_hours.tableCount"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, amountPerHourIndex, localesController.getString(locale, "reports.harvest_work_hours.tablesPerHour"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex, localesController.getString(locale, "reports.harvest_work_hours.yield"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, yieldPerHour, localesController.getString(locale, "reports.harvest_work_hours.yieldPerHour"));

      rowIndex++;

      Map<Product, ProductHarvestStats> productPlantingStatsMap = new LinkedHashMap<>();

      for (Event event : events) {
        if (event.getStartTime() == null || event.getEndTime() == null) {
          continue;
        }

        HarvestEvent harvestEvent = (HarvestEvent) event;
        Product product = harvestEvent.getProduct();
        Duration duration = Duration.between(harvestEvent.getStartTime(), harvestEvent.getEndTime()).abs();

        productPlantingStatsMap.computeIfAbsent(product, k -> new ProductHarvestStats()).durationMinutes += duration.toMinutes();
        productPlantingStatsMap.computeIfAbsent(product, k -> new ProductHarvestStats()).amount += harvestEvent.getGutterCount();
        productPlantingStatsMap.computeIfAbsent(product, k -> new ProductHarvestStats()).yieldKg += getYield(harvestEvent);
      }

      for (Map.Entry<Product, ProductHarvestStats> entry : productPlantingStatsMap.entrySet()) {
        Product product = entry.getKey();
        ProductHarvestStats stats = entry.getValue();
        Duration duration = Duration.ofMinutes(stats.durationMinutes);

        double durationHours = duration.toHours() + (double) duration.toMinutesPart() / 60;

        if (durationHours == 0) {
          durationHours = 1;
        }

        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, formatDuration(duration));
        xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, stats.amount);
        xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex, stats.yieldKg);
        xlsxBuilder.setCellValue(sheetId, rowIndex, amountPerHourIndex, stats.amount / durationHours);
        xlsxBuilder.setCellValue(sheetId, rowIndex, yieldPerHour, stats.yieldKg / durationHours);

        rowIndex++;
      }

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.work_hours_summary.combined"));

      Long totalDurationMinutes = productPlantingStatsMap.values().stream().map(e -> e.durationMinutes).reduce(0L, Long::sum);
      Long totalAmount = productPlantingStatsMap.values().stream().map(e -> e.amount).reduce(0L, Long::sum);
      Double totalYield = productPlantingStatsMap.values().stream().map(e -> e.yieldKg).reduce(0d, Double::sum);

      Duration duration = Duration.ofMinutes(totalDurationMinutes);
      double durationHours = duration.toHours() + (double) duration.toMinutesPart() / 60;

      if (durationHours == 0) {
        durationHours = 1;
      }

      xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, formatDuration(duration));
      xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, totalAmount);
      xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex, totalYield);
      xlsxBuilder.setCellValue(sheetId, rowIndex, amountPerHourIndex, totalAmount / durationHours);
      xlsxBuilder.setCellValue(sheetId, rowIndex, yieldPerHour, totalYield / durationHours);

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  private Double getYield(HarvestEvent harvestEvent) {
    return harvestEventController.listHarvestBaskets(harvestEvent).stream().map(e -> (double) e.getWeight()).reduce(0d, Double::sum);
  }

  static class ProductHarvestStats {
    long durationMinutes;
    long amount;
    double yieldKg;
  }
}
