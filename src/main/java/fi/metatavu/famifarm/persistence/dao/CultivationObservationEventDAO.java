package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;

/**
 * DAO class for CultivationObservationEvent
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class CultivationObservationEventDAO extends AbstractEventDAO<CultivationObservationEvent> {

  /**
   * Creates new cultivationActionEvent
   * 
   * @param id id
   * @param weight weight
   * @param luminance luminance
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param remainingUnits remaining units
   * @param creatorId creator id
   * @param lastModifierId last modifier id
   * @return
   */
  @SuppressWarnings ("squid:S00107")
  public CultivationObservationEvent create(UUID id, Double weight, Double luminance, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime,  Integer remainingUnits, String additionalInformation, UUID creatorId, UUID lastModifierId) {
    CultivationObservationEvent cultivationActionEvent = new CultivationObservationEvent();
    cultivationActionEvent.setId(id);
    cultivationActionEvent.setRemainingUnits(remainingUnits);
    cultivationActionEvent.setWeight(weight);
    cultivationActionEvent.setLuminance(luminance);
    cultivationActionEvent.setBatch(batch);
    cultivationActionEvent.setStartTime(startTime);
    cultivationActionEvent.setEndTime(endTime);
    cultivationActionEvent.setId(id);
    cultivationActionEvent.setCreatorId(creatorId);
    cultivationActionEvent.setLastModifierId(lastModifierId);
    cultivationActionEvent.setAdditionalInformation(additionalInformation);
    return persist(cultivationActionEvent);
  }

  /**
   * Updates weight
   *
   * @param weight weight
   * @param lastModifier modifier
   * @return updated cultivationActionEvent
   */
  public CultivationObservationEvent updateWeight(CultivationObservationEvent cultivationActionEvent, Double weight, UUID lastModifierId) {
    cultivationActionEvent.setLastModifierId(lastModifierId);
    cultivationActionEvent.setWeight(weight);
    return persist(cultivationActionEvent);
  }

  /**
   * Updates luminance
   *
   * @param luminance luminance
   * @param lastModifier modifier
   * @return updated cultivationActionEvent
   */
  public CultivationObservationEvent updateLuminance(CultivationObservationEvent cultivationActionEvent, Double luminance, UUID lastModifierId) {
    cultivationActionEvent.setLastModifierId(lastModifierId);
    cultivationActionEvent.setLuminance(luminance);
    return persist(cultivationActionEvent);
  }

}
