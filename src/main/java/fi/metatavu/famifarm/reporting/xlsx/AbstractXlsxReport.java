package fi.metatavu.famifarm.reporting.xlsx;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

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

  @Override
  public String getContentType() {
    return "application/vnd.ms-excel";
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
  
  /**
   * Parse date
   * 
   * @param date date as string
   * @return date as OffsetDateTime
   */
  protected OffsetDateTime parseDate(String date) {
    if (StringUtils.isEmpty(date)) {
      return null;
    }
    
    return OffsetDateTime.parse(date);
  }

}
