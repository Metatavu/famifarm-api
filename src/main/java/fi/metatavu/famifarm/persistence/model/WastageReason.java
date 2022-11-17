package fi.metatavu.famifarm.persistence.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import fi.metatavu.famifarm.rest.model.Facility;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * JPA entity for wastage reason
 * 
 * @author Ville Koivukangas
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class WastageReason {

  @Id
  @Column(nullable = false)
  @NotNull
  private UUID id;
  
  @ManyToOne (optional = false)
  private LocalizedEntry reason;
  
  @Column(nullable = false)
  @NotNull
  private UUID creatorId;
  
  @Column(nullable = false)
  @NotNull
  private UUID lastModifierId;

  @Column (nullable = false)
  private OffsetDateTime createdAt;

  @Column (nullable = false)
  private OffsetDateTime modifiedAt;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Facility facility;
  
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public LocalizedEntry getReason() {
    return reason;
  }

  public void setReason(LocalizedEntry reason) {
    this.reason = reason;
  }

  public UUID getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(UUID creatorId) {
    this.creatorId = creatorId;
  }

  public UUID getLastModifierId() {
    return lastModifierId;
  }

  public void setLastModifierId(UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getModifiedAt() {
    return modifiedAt;
  }

  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  public Facility getFacility() {
    return facility;
  }

  public void setFacility(Facility facility) {
    this.facility = facility;
  }

  @PrePersist
  public void onCreate() {
    setCreatedAt(OffsetDateTime.now());
    setModifiedAt(OffsetDateTime.now());
  }
  
  @PreUpdate
  public void onUpdate() {
    setModifiedAt(OffsetDateTime.now());
  }
}
