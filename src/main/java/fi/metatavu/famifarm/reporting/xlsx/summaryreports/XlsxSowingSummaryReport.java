package fi.metatavu.famifarm.reporting.xlsx.summaryreports;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
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

@ApplicationScoped
public class XlsxSowingSummaryReport extends AbstractXlsxReport {

  @Inject
  private EventController eventController;

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  final int productIndex = 0;
  final int amountIndex = 1;

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

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.product_header"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, localesController.getString(locale, "reports.sowing_summary.amount"));

      Map<Product, Integer> productCounts = new HashMap<Product, Integer>();

      for (Event event : events) {
        SowingEvent sowingEvent = (SowingEvent) event;
        int count = productCounts.getOrDefault(sowingEvent.getProduct(), 0);

        productCounts.put(sowingEvent.getProduct(), count + sowingEvent.getAmount());
      }

      for (Map.Entry<Product, Integer> entry : productCounts.entrySet()) {
        rowIndex++;
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(entry.getKey().getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, entry.getValue());
      }

      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.sowing_summary.total"));

      int total = productCounts.values().stream().mapToInt(Integer::intValue).sum();
      xlsxBuilder.setCellValue(sheetId, rowIndex, amountIndex, total);

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }
}
