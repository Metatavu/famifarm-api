package fi.metatavu.famifarm.reporting.xlsx.workhours;

import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.persistence.dao.PackingBasketDAO;
import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.xlsx.AbstractXlsxReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.reporting.xlsx.listreports.data.PackingData;
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
import java.util.stream.Collectors;

@ApplicationScoped
public class XlsxPackingWorkHoursReport extends AbstractWorkHoursReport {

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  @Inject
  private PackingBasketDAO packingBasketDAO;

  @Inject
  private PackingController packingController;

  final int dateIndex = 0;
  final int productIndex = 1;
  final int durationIndex = 2;
  final int bagsIndex = 3;
  final int boxesIndex = 4;
  final int bagsPerHourIndex = 5;
  final int boxesPerHourIndex = 6;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String title = localesController.getString(locale, "reports.sowing_summary.title");
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

      xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, localesController.getString(locale, "reports.packing_work_hours.packingDate"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, localesController.getString(locale, "reports.packing_work_hours.duration"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, bagsIndex, localesController.getString(locale, "reports.packing_work_hours.bags"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, boxesIndex, localesController.getString(locale, "reports.packing_work_hours.boxes"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, bagsPerHourIndex, localesController.getString(locale, "reports.packing_work_hours.bagsPerHour"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, boxesPerHourIndex, localesController.getString(locale, "reports.packing_work_hours.boxesPerHour"));

      rowIndex++;

      for (PackingData event : events) {
        Product product = event.getPacking().getProduct();
        String date = formatOffsetDateTime(event.getPacking().getStartTime());
        Duration duration = Duration.between(event.getPacking().getStartTime(), event.getPacking().getEndTime()).abs();

        long bags = (long) event.getPacking().getPackedCount() * event.getPacking().getPackageSize().getSize();
        long boxes = event.getPacking().getPackedCount();

        System.out.println("Bags: " + bags + ", Boxes: " + boxes + ", Duration: " + duration.toHours());

        double packingDurationHours = duration.toHours() + (double) duration.toMinutesPart() / 60;

        if (packingDurationHours == 0) {
          packingDurationHours = 1;
        }

        double bagsPerHour = (double) bags / packingDurationHours;
        double boxesPerHour = (double) boxes / packingDurationHours;

        String localizedProductName = localizedValueController.getValue(product.getName(), locale);

        xlsxBuilder.setCellValue(sheetId, rowIndex, dateIndex, date);
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedProductName);
        xlsxBuilder.setCellValue(sheetId, rowIndex, durationIndex, formatDuration(duration));
        xlsxBuilder.setCellValue(sheetId, rowIndex, bagsIndex, bags);
        xlsxBuilder.setCellValue(sheetId, rowIndex, boxesIndex, boxes);
        xlsxBuilder.setCellValue(sheetId, rowIndex, bagsPerHourIndex, bagsPerHour);
        xlsxBuilder.setCellValue(sheetId, rowIndex, boxesPerHourIndex, boxesPerHour);

        rowIndex++;
      }

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
}
