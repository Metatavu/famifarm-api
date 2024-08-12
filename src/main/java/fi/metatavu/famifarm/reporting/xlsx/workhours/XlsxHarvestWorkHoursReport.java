package fi.metatavu.famifarm.reporting.xlsx.workhours;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.HarvestEventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.OutputStream;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class XlsxHarvestWorkHoursReport extends AbstractWorkHoursReport {

  @Inject
  private EventController eventController;

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  @Inject
  private HarvestEventController harvestEventController;

  final int dateIndex = 0;
  final int productIndex = 1;
  final int durationIndex = 2;
  final int tableCountIndex = 3;
  final int yieldIndex = 4;
  final int tablePerHourIndex = 5;
  final int yieldPerHourIndex = 6;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String title = localesController.getString(locale, "reports.harvest_work_hours.title");
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

      xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, localesController.getString(locale, "reports.harvest_work_hours.date"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, localesController.getString(locale, "reports.harvest_work_hours.duration"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, tableCountIndex, localesController.getString(locale, "reports.harvest_work_hours.tableCount"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex, localesController.getString(locale, "reports.harvest_work_hours.yield"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, tablePerHourIndex, localesController.getString(locale, "reports.harvest_work_hours.tablesPerHour"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, yieldPerHourIndex, localesController.getString(locale, "reports.harvest_work_hours.yieldPerHour"));

      rowIndex++;

      for (Event event : events) {
        HarvestEvent harvestEvent = (HarvestEvent) event;
        Product product = harvestEvent.getProduct();
        String date = formatOffsetDateTime(harvestEvent.getStartTime());
        Duration duration = Duration.between(harvestEvent.getStartTime(), harvestEvent.getEndTime()).abs();

        double harvestDuration = duration.toHours() + (double) duration.toMinutesPart() / 60;

        if (harvestDuration == 0) {
          harvestDuration = 1;
        }

        long tableCount = harvestEvent.getGutterCount();
        double yield = getYield(harvestEvent);
        double tablePerHour = tableCount / harvestDuration;
        double yieldPerHour = yield / harvestDuration;

        String localizedProductName = localizedValueController.getValue(product.getName(), locale);

        xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, date);
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedProductName);
        xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, formatDuration(duration));
        xlsxBuilder.setCellValue(sheetId, rowIndex, tableCountIndex, tableCount);
        xlsxBuilder.setCellValue(sheetId, rowIndex, yieldIndex, yield);
        xlsxBuilder.setCellValue(sheetId, rowIndex, tablePerHourIndex, tablePerHour);
        xlsxBuilder.setCellValue(sheetId, rowIndex, yieldPerHourIndex, yieldPerHour);

        rowIndex++;
      }

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  private Double getYield(HarvestEvent harvestEvent) {
    return harvestEventController.listHarvestBaskets(harvestEvent).stream().map(e -> (double) e.getWeight()).reduce(0d, Double::sum);
  }
}
