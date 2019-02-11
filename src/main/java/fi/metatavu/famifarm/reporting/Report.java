package fi.metatavu.famifarm.reporting;

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
   */
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws Exception;
  
  /**
   * Returns report's content type
   * 
   * @return report's content type 
   */
  public String getContentType();
  
}