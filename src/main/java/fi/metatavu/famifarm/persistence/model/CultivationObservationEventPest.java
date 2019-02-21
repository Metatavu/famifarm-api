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
 * JPA entity for cultivation pest event pest
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CultivationObservationEventPest {

  @Id
  @Column(nullable = false)
  @NotNull
  private UUID id;

  @ManyToOne
  private CultivationObservationEvent event;

  @ManyToOne
  private Pest pest;

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

  public Pest getPest() {
    return pest;
  }

  public void setPest(Pest pest) {
    this.pest = pest;
  }

}
