package fi.metatavu.famifarm.reporting;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.reporting.xlsx.*;

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

  /**
   * Returns report for given report type
   * 
   * @param reportType report to
   * @return report instance
   */
  @SuppressWarnings ("squid:S1301")
  public Report getReport(ReportType reportType) {
    switch (reportType) {
      case XLS_EXAMPLE:
        return xlsxExampleReport;
      case WASTAGE:
        return xlsxWastageReport;
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
    }
    
    return null;
  }
  
}
