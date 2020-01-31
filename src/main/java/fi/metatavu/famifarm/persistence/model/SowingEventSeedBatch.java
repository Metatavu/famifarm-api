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
 * JPA entity for sowing event seed batch
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class SowingEventSeedBatch {

  @Id
  @Column(nullable = false)
  @NotNull
  private UUID id;
  
  @ManyToOne
  private SeedBatch seedBatch;
  
  @ManyToOne
  private SowingEvent sowingEvent;
  
  public UUID getId() {
    return id;
  }
  
  public void setId(UUID id) {
    this.id = id;
  }
  
  public SeedBatch getSeedBatch() {
    return seedBatch;
  }
  
  public void setSeedBatch(SeedBatch seedBatch) {
    this.seedBatch = seedBatch;
  }
  
  public SowingEvent getSowingEvent() {
    return sowingEvent;
  }
  
  public void setSowingEvent(SowingEvent sowingEvent) {
    this.sowingEvent = sowingEvent;
  }
  
}
