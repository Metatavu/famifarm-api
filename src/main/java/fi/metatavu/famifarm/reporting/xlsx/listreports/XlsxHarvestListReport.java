package fi.metatavu.famifarm.reporting.xlsx.listreports;

import fi.metatavu.famifarm.events.HarvestEventController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.rest.model.EventType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class XlsxHarvestListReport extends XlsxEventListReport {

  @Inject
  private HarvestEventController harvestEventController;

  final int productIndex = 0;
  final int lineIndex = 1;
  final int harvestDateIndex = 2;
  final int sowingDateIndex = 3;
  final int growthDurationIndex = 4;
  final int tableCountIndex = 5;
  final int cropYieldKgIndex = 6;
  final int basketCountIndex = 7;
  final int kgPerTableIndex = 8;
  final int gramsPerUnitIndex = 9;
  final int kgPerBasketIndex = 10;
  final int cuttingHeightIndex = 11;

  private final List<ListReportColumn> columns = List.of(
    new ListReportColumn("reports.harvest_report.productHeader", productIndex),
    new ListReportColumn("reports.harvest_report.lineHeader", lineIndex),
    new ListReportColumn("reports.harvest_report.harvestDateHeader", harvestDateIndex),
    new ListReportColumn("reports.harvest_report.sowingDateHeader", sowingDateIndex),
    new ListReportColumn("reports.harvest_report.growthTimeHeader", growthDurationIndex),
    new ListReportColumn("reports.harvest_report.tableCountHeader", tableCountIndex),
    new ListReportColumn("reports.harvest_report.cropYieldHeader", cropYieldKgIndex),
    new ListReportColumn("reports.harvest_report.basketCountHeader", basketCountIndex),
    new ListReportColumn("reports.harvest_report.kgPerTableHeader", kgPerTableIndex),
    new ListReportColumn("reports.harvest_report.gramsPerUnitHeader", gramsPerUnitIndex),
    new ListReportColumn("reports.harvest_report.kgPerBasketHeader", kgPerBasketIndex),
    new ListReportColumn("reports.harvest_report.cuttingHeightIndexHeader", cuttingHeightIndex)
  );

  @Override
  protected EventType getEventType() {
    return EventType.HARVEST;
  }

  @Override
  protected String getTitle(Locale locale) {
    return localesController.getString(locale, "reports.harvest_report.title");
  }

  @Override
  protected List<ListReportColumn> getReportColumns() {
    return columns;
  }

  @Override
  protected void setColumnValueToXlsx(XlsxBuilder xlsxBuilder, ListReportColumn column, String sheetId, int rowIndex, Event event, Locale locale, LocalizedValueController localizedValueController) {
    HarvestEvent harvestEvent = (HarvestEvent) event;
    switch (column.getColumnIndex()) {
      case productIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(harvestEvent.getProduct().getName(), locale));
        break;
      case lineIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, lineIndex, harvestEvent.getProductionLine().getLineNumber());
        break;
      case harvestDateIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, harvestDateIndex, formatOffsetDateTime(harvestEvent.getStartTime()));
        break;
      case sowingDateIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, sowingDateIndex, formatOffsetDateTime(harvestEvent.getSowingDate()));
        break;
      case growthDurationIndex:
        long saplingDuration = Duration.between(harvestEvent.getStartTime(), harvestEvent.getSowingDate()).abs().toDays();
        xlsxBuilder.setCellValue(sheetId, rowIndex, growthDurationIndex, Long.toString(saplingDuration));
        break;
      case tableCountIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, tableCountIndex, harvestEvent.getGutterCount());
        break;
      case cropYieldKgIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, cropYieldKgIndex, getYield(harvestEvent));
        break;
      case basketCountIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, basketCountIndex, getBasketCount(harvestEvent));
        break;
      case kgPerTableIndex:
        double kgPerTable = getYield(harvestEvent) / harvestEvent.getGutterCount();
        xlsxBuilder.setCellValue(sheetId, rowIndex, kgPerTableIndex, kgPerTable);
        break;
      case gramsPerUnitIndex:
        double kgPerUnit = (getYield(harvestEvent) / harvestEvent.getGutterCount() / harvestEvent.getGutterHoleCount() / 50) * 1000;
        xlsxBuilder.setCellValue(sheetId, rowIndex, gramsPerUnitIndex, kgPerUnit);
        break;
      case kgPerBasketIndex:
        double kgPerCart = getYield(harvestEvent) / getBasketCount(harvestEvent);
        xlsxBuilder.setCellValue(sheetId, rowIndex, kgPerBasketIndex, kgPerCart);
        break;
      case cuttingHeightIndex:
        xlsxBuilder.setCellValue(sheetId, rowIndex, cuttingHeightIndex, harvestEvent.getCuttingHeight());
        break;
    }
  }

  private Double getYield(HarvestEvent harvestEvent) {
    return harvestEventController.listHarvestBaskets(harvestEvent).stream().map(e -> (double) e.getWeight()).reduce(0d, Double::sum);
  }

  private Integer getBasketCount(HarvestEvent harvestEvent) {
    return harvestEventController.listHarvestBaskets(harvestEvent).size();
  }
}
