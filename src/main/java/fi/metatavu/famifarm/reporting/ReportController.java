package fi.metatavu.famifarm.reporting;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.reporting.xlsx.XlsxExampleReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxGrowthTimeReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxWastageReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxYieldReport;

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
    }
    
    return null;
  }
  
}
