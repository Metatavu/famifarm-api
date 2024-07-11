package fi.metatavu.famifarm.reporting.xlsx.summaryreports;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.xlsx.AbstractXlsxReport;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.inject.Inject;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

//TODO maybe abstract some things here if it seems smart
public abstract class XlsxSummaryReport extends AbstractXlsxReport {

  @Inject
  private EventController eventController;

  /**
   * Gets the localized report title
   *
   * @param locale locale
   * @return localized report title
   */
  protected abstract String getTitle(Locale locale);


  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {

  }
}
