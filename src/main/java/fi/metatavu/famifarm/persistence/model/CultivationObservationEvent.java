package fi.metatavu.famifarm.persistence.model;

import java.beans.Transient;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.famifarm.rest.model.EventType;

/**
 * JPA entity for cultivation observation event
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CultivationObservationEvent extends Event {

  @Column
  private Double weight;

  @Column
  private Double luminance;

  @Transient
  @Override
  public EventType getType() {
    return EventType.CULTIVATION_OBSERVATION;
  }

  public Double getWeight() {
    return weight;
  }

  public void setWeight(Double weight) {
    this.weight = weight;
  }

  public Double getLuminance() {
    return luminance;
  }

  public void setLuminance(Double luminance) {
    this.luminance = luminance;
  }

}
