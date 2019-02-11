package fi.metatavu.famifarm.reporting.xlsx;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.reporting.Report;
import fi.metatavu.famifarm.users.UserController;

/**
 * Abstract base class for XLSX reports
 * 
 * @author Antti Leppä
 */
public abstract class AbstractXlsxReport implements Report {

  @Inject
  private UserController userController;

  @Inject
  private LocalizedValueController localizedValueController; 

  @Override
  public String getContentType() {
    return "application/vnd.ms-excel";
  }

  /**
   * Formats batch to be displayed in a spreadsheet report
   * 
   * @param locale locale
   * @param batch batch
   * @return batch formatted to be displayed in a spreadsheet report
   */
  protected String getFormattedBatch(Locale locale, Batch batch) {
    String productName = localizedValueController.getValue(batch.getProduct().getName(), locale);
    String createdAt = DateTimeFormatter.ISO_LOCAL_DATE.format(batch.getCreatedAt());
    return String.format("%s - %s", createdAt, productName);
  }

  /**
   * Returns formatted user name. Uses build-time cache to reduce requests into Keycloak
   * 
   * @param userId user id
   * @param userCache user cache instance
   * @return formatted user name
   */
  protected String getFormattedUser(UUID userId, Map<UUID, String> userCache) {
    if (!userCache.containsKey(userId)) {
      userCache.put(userId, userController.getUserDisplayName(userId));
    }
        
    return userCache.get(userId);
  }

}
