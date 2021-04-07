package fi.metatavu.famifarm.reporting.xlsx;

import fi.metatavu.famifarm.reporting.AbstractReport;

/**
 * Abstract base class for XLSX reports
 * 
 * @author Antti Leppä
 */
public abstract class AbstractXlsxReport extends AbstractReport {

  @Override
  public String getContentType() {
    return "application/vnd.ms-excel";
  }
}
