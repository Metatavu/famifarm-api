package fi.metatavu.famifarm.reporting;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.reporting.xlsx.XlsGrowthTimeReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxExampleReport;

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
  private XlsGrowthTimeReport xlsxGrowthTimeReport;
  
  /**
   * Returns report for giveb report type
   * 
   * @param reportType report to
   * @return report instance
   */
  @SuppressWarnings ("squid:S1301")
  public Report getReport(ReportType reportType) {
    switch (reportType) {
      case XLS_EXAMPLE:
        return xlsxExampleReport;
      case GROWTH_TIME:
        return xlsxGrowthTimeReport;
    }
    
    return null;
  }
  
}
