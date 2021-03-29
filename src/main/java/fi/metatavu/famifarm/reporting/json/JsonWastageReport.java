package fi.metatavu.famifarm.reporting.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.EventType;
import liquibase.pro.packaged.A;
import liquibase.pro.packaged.I;
import liquibase.pro.packaged.O;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Wastage report in json format
 */
@ApplicationScoped
public class JsonWastageReport extends AbstractJsonReport {

  @Inject
  private EventController eventController;

  @Inject
  private LocalizedValueController localizedValueController;

  /**
   * Creates report
   *
   * @param output     output stream
   * @param locale     locale
   * @param parameters report parameters
   * @throws ReportException when report creation fails
   */
  @Override
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    List<Event> events = eventController.listByStartTimeAfterAndStartTimeBefore(parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
    List<fi.metatavu.famifarm.reporting.json.models.Event> translatedEvents = translateEvents(events, locale);
    try {
      getObjectMapper().writeValue(output, translatedEvents);
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }

  private List<fi.metatavu.famifarm.reporting.json.models.Event> translateEvents(List<Event> events, Locale locale) {
    List<fi.metatavu.famifarm.reporting.json.models.Event> translatedEvents = new ArrayList<>(events.size());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    Map<UUID, String> userCache = new HashMap<>();

    for (Event original : events) {
      if (original.getType() == EventType.WASTAGE) {
        WastageEvent wastageEvent = (WastageEvent) original;
        OffsetDateTime endTime = wastageEvent.getEndTime();

        fi.metatavu.famifarm.reporting.json.models.Event translated = new fi.metatavu.famifarm.reporting.json.models.Event();
        translated.setLineNumber(wastageEvent.getProductionLine().getLineNumber());
        translated.setEndTime(endTime.format(formatter));
        translated.setUser(getFormattedUser(wastageEvent.getCreatorId(), userCache));
        translated.setProductName(localizedValueController.getValue(wastageEvent.getProduct().getName(), locale));
        translated.setEventPhase(wastageEvent.getPhase().toString());
        translated.setWastageReason(localizedValueController.getValue(wastageEvent.getWastageReason().getReason(), locale));
        translated.setAdditionalInformation(wastageEvent.getAdditionalInformation());
        translated.setAmount(wastageEvent.getAmount());
        translatedEvents.add(translated);
      }
    }
    return translatedEvents;
  }
}
