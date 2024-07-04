package fi.metatavu.famifarm.reporting.xlsx.listreports;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.xlsx.AbstractXlsxReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.inject.Inject;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class XlsxEventListReport extends AbstractXlsxReport {

  @Inject
  protected LocalesController localesController;

  @Inject
  protected LocalizedValueController localizedValueController;

  @Inject
  private EventController eventController;

  /**
   * Formats a given offset
   *
   * @param date Date time
   *
   * @return formatted date string
   */
  protected String formatOffsetDateTime(OffsetDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    return date.format(formatter);
  }

  /**
   * Returns event type for each report
   *
   * @return event type for each report
   */
  protected abstract EventType getEventType();

  /**
   * Gets the localized report title
   *
   * @param locale locale
   * @return localized report title
   */
  protected abstract String getTitle(Locale locale);

  /**
   * Returns the columns for the report
   *
   * @return list columns
   */
  protected abstract List<ListReportColumn> getReportColumns();

  /**
   * Sets the column value to the xlsx
   *
   * @param xlsxBuilder xlsxBuilder
   * @param column column
   * @param sheetId sheetId
   * @param rowIndex rowIndex
   * @param event event
   * @param locale locale
   * @param localizedValueController localizedValueController
   */
  protected abstract void setColumnValueToXlsx(XlsxBuilder xlsxBuilder, ListReportColumn column, String sheetId, int rowIndex, Event event, Locale locale, LocalizedValueController localizedValueController);

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(getTitle(locale));

      List<ListReportColumn> listReportColumns = getReportColumns();
      int rowIndex = 0;
      int columnIndex = 0;

      OffsetDateTime toTime = parseDate(parameters.get("toTime"));
      OffsetDateTime fromTime = parseDate(parameters.get("fromTime"));

      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, getTitle(locale));
      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime));
      rowIndex++;
      rowIndex++;

      for (ListReportColumn listReportColumn : listReportColumns) {
        xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, listReportColumn.getHeader()));
        columnIndex++;
      }

      List<Event> events = eventController.listByTimeFrameAndType(
              facility,
              toTime,
              fromTime,
              getEventType()
      );

      for (Event event : events) {
        rowIndex++;
        columnIndex = 0;
        for (ListReportColumn listReportColumn : listReportColumns) {
          setColumnValueToXlsx(xlsxBuilder, listReportColumn, sheetId, rowIndex, event, locale, localizedValueController);
          columnIndex++;
        }
      }

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  protected static class ListReportColumn {
    private final String header;
    private final Integer columnIndex;

    public ListReportColumn(String header, Integer columnIndex) {
      this.header = header;
      this.columnIndex = columnIndex;
    }

    public String getHeader() {
      return header;
    }

    public Integer getColumnIndex() {
      return columnIndex;
    }
  }
}
