package fi.metatavu.famifarm.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Locale.LanguageRange;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.LocaleUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.rest.model.ErrorResponse;
import fi.metatavu.famifarm.rest.model.LocalizedValue;
import io.vertx.core.http.HttpServerRequest;

/**
 * Abstract base class for all API services
 * 
 * @author Antti Leppä
 */
public abstract class AbstractApi {
  
  protected static final String NOT_FOUND_MESSAGE = "Not found";

  @Inject
  JsonWebToken jwt;

  @Context
  HttpServerRequest httpServerRequest;

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
    Response.ResponseBuilder rb = Response.noContent();
    rb = rb.type(MediaType.APPLICATION_JSON);
    rb = rb.status(Response.Status.NOT_FOUND);
    rb = rb.entity(entity);
    return rb.build();
  }
  
  /**
   * Constructs forbidden response
   * 
   * @param message message
   * @return response
   */
  protected Response createForbidden(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    Response.ResponseBuilder rb = Response.noContent();
    rb = rb.type(MediaType.APPLICATION_JSON);
    rb = rb.status(Response.Status.FORBIDDEN);
    rb = rb.entity(entity);
    return rb.build();
  }
  
  /**
   * Constructs bad request response
   * 
   * @param message message
   * @return response
   */
  protected Response createBadRequest(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    Response.ResponseBuilder rb = Response.noContent();
    rb = rb.type(MediaType.APPLICATION_JSON);
    rb = rb.status(Response.Status.BAD_REQUEST);
    rb = rb.entity(entity);
    return rb.build();
  }
  
  /**
   * Constructs internal server error response
   * 
   * @param message message
   * @return response
   */
  protected Response createInternalServerError(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    Response.ResponseBuilder rb = Response.noContent();
    rb = rb.type(MediaType.APPLICATION_JSON);
    rb = rb.status(Response.Status.INTERNAL_SERVER_ERROR);
    rb = rb.entity(entity);
    return rb.build();
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
   * Creates streamed response from byte array
   * 
   * @param data data
   * @param type content type
   * @return Response
   */
  protected Response streamResponse(byte[] data, String type) {
    try (InputStream byteStream = new ByteArrayInputStream(data)) {
      return streamResponse(type, byteStream, data.length);
    } catch (IOException e) {
      logger.error("Failed to stream data to client", e);
      return createInternalServerError("Failed to stream data to client");
    }
  }
  
  /**
   * Creates streamed response from input stream
   * 
   * @param inputStream data
   * @param type content type
   * @param contentLength content length
   * @return Response
   */
  protected Response streamResponse(String type, InputStream inputStream, int contentLength) {
    return Response.ok(new StreamingOutputImpl(inputStream), type)
      .header("Content-Length", contentLength)
      .build();
  }
  
  /**
   * Returns logged user id
   * 
   * @return logged user id
   */
  protected UUID getLoggerUserId() {
    String remoteUser = jwt.getSubject();
    if (remoteUser == null) {
      return null;
    }
    
    return UUID.fromString(remoteUser);
  }
  
  /**
   * Returns request locale
   * 
   * @return request locale
   */
  protected Locale getLocale() {
    String languageHeader = httpServerRequest.getHeader("Accept-language");
    if (languageHeader == null) {
      return Locale.ENGLISH;
    }
    List<LanguageRange> langs = Locale.LanguageRange.parse(languageHeader);
    LanguageRange lang = langs.stream().findFirst().orElse(null);
    if (lang != null) {
      return Locale.forLanguageTag(lang.getRange());
    }
    return Locale.ENGLISH;
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
