package fi.metatavu.famifarm.reporting.xlsx;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import fi.metatavu.famifarm.reporting.AbstractReport;
import org.apache.commons.lang3.StringUtils;

import fi.metatavu.famifarm.reporting.Report;
import fi.metatavu.famifarm.users.UserController;

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
