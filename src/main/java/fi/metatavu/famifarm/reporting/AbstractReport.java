package fi.metatavu.famifarm.reporting;

import fi.metatavu.famifarm.users.UserController;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract class for reports which parses dates and users
 */
public abstract class AbstractReport implements Report {

  @Inject
  private UserController userController;

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
