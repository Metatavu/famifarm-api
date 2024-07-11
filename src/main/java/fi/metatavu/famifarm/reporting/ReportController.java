package fi.metatavu.famifarm.reporting;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.reporting.json.JsonWastageReport;
import fi.metatavu.famifarm.reporting.xlsx.*;
import fi.metatavu.famifarm.reporting.xlsx.listreports.XlsxHarvestListReport;
import fi.metatavu.famifarm.reporting.xlsx.listreports.XlsxPackingListReport;
import fi.metatavu.famifarm.reporting.xlsx.listreports.XlsxPlantingListReport;
import fi.metatavu.famifarm.reporting.xlsx.summaryreports.XlsxHarvestSummaryReport;
import fi.metatavu.famifarm.reporting.xlsx.summaryreports.XlsxPackingSummaryReport;
import fi.metatavu.famifarm.reporting.xlsx.summaryreports.XlsxPlantingSummaryReport;
import fi.metatavu.famifarm.reporting.xlsx.summaryreports.XlsxSowingSummaryReport;

/**
 * Report controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ReportController {
  
  @Inject
  private XlsxExampleReport xlsxExampleReport;
  
  @Inject
  private XlsxWastageReport xlsxWastageReport;
  
  @Inject
  private XlsxGrowthTimeReport xlsxGrowthTimeReport;
  
  @Inject
  private XlsxYieldReport xlsxYieldReport;
  
  @Inject
  private XlsxPlantingYieldReport xlsxPlantingYieldReport;
  
  @Inject
  private XlsxSowedReport xlsxSowedReport;

  @Inject
  private XlsxPlantedReport xlsxPlantedReport;

  @Inject
  private XlsxSpreadReport xlsxSpreadReport;

  @Inject
  private XlsxHarvestedReport xlsxHarvestedReport;

  @Inject
  private XlsxProductPhaseCountReport xlsxProductPhaseCountReport;

  @Inject
  private XlsxPackedReport xlsxPackedReport;

  @Inject
  private XlsxPackedCampaingsReport xlsxPackedCampaingsReport;

  @Inject
  private JsonWastageReport jsonWastageReport;

  @Inject
  private XlsxSeedlingTimeReport xlsxSeedlingTimeReport;

  @Inject
  private XlsxlSummaryReport xlsxSummaryReport;

  @Inject
  private XlsxPlantingListReport xlsxPlantingListReport;

  @Inject
  private XlsxHarvestListReport xlsxHarvestListReport;

  @Inject
  private XlsxPackingListReport xlsxPackingListReport;

  @Inject
  private XlsxSowingSummaryReport xlsxSowingSummaryReport;

  @Inject
  private XlsxPlantingSummaryReport xlsxPlantingSummaryReport;

  @Inject
  private XlsxPackingSummaryReport xlsxPackingSummaryReport;

  @Inject
  private XlsxHarvestSummaryReport xlsxHarvestSummaryReport;

  /**
   * Returns report for given report type
   * 
   * @param reportType report to
   * @param format format
   * @return report instance
   */
  @SuppressWarnings ("squid:S1301")
  public Report getReport(ReportType reportType, ReportFormat format) {
    switch (reportType) {
      case XLS_EXAMPLE:
        return xlsxExampleReport;
      case WASTAGE:
        if (format.equals(ReportFormat.JSON)) {
          return jsonWastageReport;
        }
        else return xlsxWastageReport;
      case GROWTH_TIME:
        return xlsxGrowthTimeReport;
      case YIELD:
        return xlsxYieldReport;
      case PLANTING_YIELD:
        return xlsxPlantingYieldReport;
      case SOWED:
        return xlsxSowedReport;
      case PLANTED:
        return xlsxPlantedReport;
      case SPREAD:
        return xlsxSpreadReport;
      case HARVESTED:
        return xlsxHarvestedReport;
      case PRODUCT_PHASE_COUNT:
        return xlsxProductPhaseCountReport;
      case PACKED:
        return xlsxPackedReport;
      case PACKED_CAMPAINGS:
        return xlsxPackedCampaingsReport;
      case SEEDLING_TIME:
        return xlsxSeedlingTimeReport;
      case SUMMARY:
        return xlsxSummaryReport;
      case JUVA_PLANTING_LIST_REPORT:
        return xlsxPlantingListReport;
      case JUVA_HARVEST_LIST_REPORT:
        return xlsxHarvestListReport;
      case JUVA_PACKING_LIST_REPORT:
        return xlsxPackingListReport;
      case JUVA_SOWING_SUMMARY_REPORT:
        return xlsxSowingSummaryReport;
      case JUVA_PLANTING_SUMMARY_REPORT:
        return xlsxPlantingSummaryReport;
      case JUVA_PACKING_SUMMARY_REPORT:
        return xlsxPackingSummaryReport;
      case JUVA_HARVEST_SUMMARY_REPORT:
        return xlsxHarvestSummaryReport;
    }
    
    return null;
  }
  
}
