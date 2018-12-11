package fi.metatavu.famifarm.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.LocaleUtils;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.rest.model.ErrorResponse;
import fi.metatavu.famifarm.rest.model.LocalizedValue;

/**
 * Abstract base class for all API services
 * 
 * @author Antti Leppä
 */
public abstract class AbstractApi {
  
  protected static final String NOT_FOUND_MESSAGE = "Not found";

  @Inject
  private Logger logger;
  
  @Inject
  private LocalizedValueController localizedValueController; 
  
  /**
   * Constructs ok response
   * 
   * @param entity payload
   * @return response
   */
  protected Response createOk(Object entity) {
    return Response
      .status(Response.Status.OK)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs not found response
   * 
   * @param message message
   * @return response
   */
  protected Response createNotFound(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.NOT_FOUND)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs ok response
   * 
   * @param entity payload
   * @param totalHits total hits
   * @return response
   */
  protected Response createOk(Object entity, Long totalHits) {
    return Response
      .status(Response.Status.OK)
      .entity(entity)
      .header("Total-Results", totalHits)
      .build();
  }
  
  /**
   * Constructs no content response
   * 
   * @return response
   */
  protected Response createNoContent() {
    return Response
      .status(Response.Status.NO_CONTENT)
      .build();
  }
  
  /**
   * Returns logged user id
   * 
   * @return logged user id
   */
  protected UUID getLoggerUserId() {
    HttpServletRequest httpServletRequest = getHttpServletRequest();
    String remoteUser = httpServletRequest.getRemoteUser();
    if (remoteUser == null) {
      return null;
    }
    
    return UUID.fromString(remoteUser);
  }
  
  /**
   * Return current HttpServletRequest
   * 
   * @return current http servlet request
   */
  protected HttpServletRequest getHttpServletRequest() {
    return ResteasyProviderFactory.getContextData(HttpServletRequest.class);
  }
  
  /**
   * Creates localized entry from values
   * 
   * @param localizedValues localized values
   * @return created entry
   */
  protected LocalizedEntry createLocalizedEntry(List<LocalizedValue> localizedValues) {
    return localizedValueController.setEntryValues(localizedValueController.createEntry(), getLocalizedValueMap(localizedValues));
  }
  
  /**
   * Updates localized entry with given values
   * 
   * @param entry entry to be updated
   * @param localizedValues localized values
   * @return updated entry
   */
  protected LocalizedEntry updateLocalizedEntry(LocalizedEntry entry, List<LocalizedValue> localizedValues) {
    return localizedValueController.setEntryValues(entry, getLocalizedValueMap(localizedValues));
  }

  /**
   * Validates localized list
   * 
   * @param localizedValues localized values
   * @return whether the values in list are vaild or nor
   */
  protected boolean isValidLocalizedList(List<LocalizedValue> localizedValues) {
    if (localizedValues == null) {
      return true;
    }

    try {
      for (int i = 0; i < localizedValues.size(); i++) {
        LocalizedValue localizedValue = localizedValues.get(i);
        if (LocaleUtils.toLocale(localizedValue.getLanguage()) == null) {
          return false;
        }
      }
    } catch (IllegalArgumentException e) {
      logger.warn("Error parsing localized value list", e);
      return false;
    }
    
    return true;
  }

  /**
   * Converts list of localized values into a map
   * 
   * @param localizedValues list of localized values
   * @return map
   */
  private Map<Locale, String> getLocalizedValueMap(List<LocalizedValue> localizedValues) {
    Map<Locale, String> result = new HashMap<>();
    
    if (localizedValues != null) {
      localizedValues.stream().forEach(localizedValue -> {
        Locale locale = LocaleUtils.toLocale(localizedValue.getLanguage());
        if (locale == null) {
          logger.error("Invalid locale {}, dropped", localizedValue.getLanguage());
          return;
        }
        
        result.put(locale, localizedValue.getValue());
      });
    }

    return result;
  }
  
}
