package fi.metatavu.famifarm.reporting.xlsx.workhours;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
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
public class XlsxPlantingWorkHoursReport extends AbstractWorkHoursReport {

  @Inject
  private EventController eventController;

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  final int dateIndex = 0;
  final int productIndex = 1;
  final int durationIndex = 2;
  final int amountIndex = 3;
  final int cartsIndex = 4;
  final int cartsPerHourIndex = 6;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String title = localesController.getString(locale, "reports.planting_work_hours.title");
      String sheetId = xlsxBuilder.createSheet(title);
      OffsetDateTime toTime = parseDate(parameters.get("toTime"));
      OffsetDateTime fromTime = parseDate(parameters.get("fromTime"));

      List<Event> events = eventController.listByTimeFrameAndType(facility, toTime, fromTime, EventType.PLANTING);

      int rowIndex = 0;
      int columnIndex = 0;

      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, title);
      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, "reports.common.dateBetween", Date.from(fromTime.toInstant()), Date.from(toTime.toInstant())));
      rowIndex++;
      rowIndex++;

      xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, localesController.getString(locale, "reports.planting_work_hours.plantingDate"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, localesController.getString(locale, "reports.planting_work_hours.duration"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, localesController.getString(locale, "reports.planting_work_hours.amount"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, cartsIndex, localesController.getString(locale, "reports.planting_work_hours.cartCount"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, cartsPerHourIndex, localesController.getString(locale, "reports.planting_work_hours.cartPerHour"));

      rowIndex++;

      for (Event event : events) {
        PlantingEvent plantingEvent = (PlantingEvent) event;
        Product product = plantingEvent.getProduct();
        String date = formatOffsetDateTime(plantingEvent.getStartTime());
        Duration duration = Duration.between(plantingEvent.getStartTime(), plantingEvent.getEndTime()).abs();
        long amount = plantingEvent.getGutterCount();
        double carts = (double) amount * plantingEvent.getGutterHoleCount() / 32;

        double plantingDuration = duration.toHours() + (double) duration.toMinutesPart() / 60;

        if (plantingDuration == 0) {
          plantingDuration = 1;
        }

        double cartsPerHour = carts / plantingDuration;

        String localizedProductName = localizedValueController.getValue(product.getName(), locale);

        xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, date);
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedProductName);
        xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, formatDuration(duration));
        xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, amount);
        xlsxBuilder.setCellValue(sheetId, rowIndex, cartsIndex, carts);
        xlsxBuilder.setCellValue(sheetId, rowIndex, cartsPerHourIndex, cartsPerHour);

        rowIndex++;
      }

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }
}
