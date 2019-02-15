package fi.metatavu.famifarm.reporting;

/**
 * Exception thrown when report creation fails
 * 
 * @author Antti Leppä
 */
public class ReportException extends Exception {

  private static final long serialVersionUID = 7396328018710243286L;

  public ReportException(Throwable cause) {
    super(cause);
  }
  
}
