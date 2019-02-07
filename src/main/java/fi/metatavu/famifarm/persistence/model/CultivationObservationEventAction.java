package fi.metatavu.famifarm.persistence.model;

import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * JPA entity for cultivation action event action
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CultivationObservationEventAction {

  @Id
  @Column(nullable = false)
  @NotNull
  private UUID id;

  @ManyToOne
  private CultivationObservationEvent event;

  @ManyToOne
  private PerformedCultivationAction action;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public CultivationObservationEvent getEvent() {
    return event;
  }

  public void setEvent(CultivationObservationEvent event) {
    this.event = event;
  }

  public PerformedCultivationAction getAction() {
    return action;
  }

  public void setAction(PerformedCultivationAction action) {
    this.action = action;
  }

}
