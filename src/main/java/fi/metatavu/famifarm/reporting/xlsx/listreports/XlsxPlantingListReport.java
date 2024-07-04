package fi.metatavu.famifarm.reporting.xlsx.listreports;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.rest.model.EventType;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class XlsxPlantingListReport extends XlsxEventListReport {

  final int productIndex = 1;
  final int lineIndex = 2;
  final int plantingDateIndex = 3;
  final int sowingDateIndex = 4;
  final int saplingDurationIndex = 5;
  final int tableCountIndex = 6;
  final int cartCountIndex = 7;

  private final List<ListReportColumn> columns = List.of(
          new ListReportColumn("reports.plating_report.productHeader", productIndex),
          new ListReportColumn("reports.plating_report.lineHeader", lineIndex),
          new ListReportColumn("reports.plating_report.plantingDateHeader", plantingDateIndex),
          new ListReportColumn("reports.plating_report.sowingDateHeader", sowingDateIndex),
          new ListReportColumn("reports.plating_report.saplingDurationHeader", saplingDurationIndex),
          new ListReportColumn("reports.plating_report.tableCountHeader", tableCountIndex),
          new ListReportColumn("reports.plating_report.cartCountHeader", cartCountIndex)
  );

  @Override
  protected EventType getEventType() {
    return EventType.PLANTING;
  }

  @Override
  protected String getTitle(Locale locale) {
    return localesController.getString(locale, "reports.plating_report.title");
  }

  @Override
  protected List<ListReportColumn> getReportColumns() {
    return columns;
  }

  @Override
  protected void setColumnValueToXlsx(XlsxBuilder xlsxBuilder, ListReportColumn column, String sheetId, int rowIndex, Event event, Locale locale, LocalizedValueController localizedValueController) {
    PlantingEvent plantingEvent = (PlantingEvent) event;
    switch (column.getColumnIndex()) {
      case productIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(plantingEvent.getProduct().getName(), locale));
        break;
      case lineIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, lineIndex, plantingEvent.getProductionLine().getLineNumber());
        break;
      case plantingDateIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, plantingDateIndex, formatOffsetDateTime(plantingEvent.getStartTime()));
        break;
      case sowingDateIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, sowingDateIndex, formatOffsetDateTime(plantingEvent.getSowingDate()));
        break;
      case saplingDurationIndex:
        long saplingDuration = Duration.between(plantingEvent.getStartTime(), plantingEvent.getSowingDate()).toDays();
        xlsxBuilder.setCellValue(sheetId, rowIndex, saplingDurationIndex, Long.toString(saplingDuration));
        break;
      case tableCountIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, tableCountIndex, plantingEvent.getGutterCount());
        break;
      case cartCountIndex:
        double plantedCarts = ((double) (plantingEvent.getGutterCount() * plantingEvent.getGutterHoleCount())) / 32;
        xlsxBuilder.setCellValue(sheetId, rowIndex, cartCountIndex, plantedCarts);
        break;
    }
  }
}
