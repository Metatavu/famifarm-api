package fi.metatavu.famifarm.reporting;

import fi.metatavu.famifarm.rest.model.Facility;

import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

/**
 * Interface describing a report
 * 
 * @author Antti Leppä
 */
public interface Report {

  /**
   * Creates report
   * 
   * @param parameters report parameters
   * @param locale locale
   * @param output output stream
   * @throws ReportException when report creation fails
   */
  void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException;
  
  /**
   * Returns report's content type
   * 
   * @return report's content type 
   */
  String getContentType();
  
}