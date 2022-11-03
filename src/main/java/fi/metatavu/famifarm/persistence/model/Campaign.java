package fi.metatavu.famifarm.persistence.model;

import fi.metatavu.famifarm.rest.model.Facility;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A JPA entity for campaign
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Campaign {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private UUID lastModifierId;

  @Column(nullable = false)
  private UUID creatorId;

  @Column(nullable = false)
  private OffsetDateTime modifiedAt;

  @Column(nullable = false)
  private OffsetDateTime createdAt;

  @Column
  @Enumerated(EnumType.STRING)
  private Facility facility;

  public void setId (UUID id) {
    this.id = id;
  }

  public UUID getId () {
    return this.id;
  }

  public void setName (String name) {
    this.name = name;
  }

  public String getName () {
    return this.name;
  }

  public void setLastModifierId (UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
  }

  public Facility getFacility() {
    return facility;
  }

  public void setFacility(Facility facility) {
    this.facility = facility;
  }

  public UUID getLastModifierId () {
    return this.lastModifierId;
  }

  public void setCreatorId (UUID creatorId) {
    this.creatorId = creatorId;
  }

  public UUID getCreatorId () {
    return this.creatorId;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getCreatedAt() {
    return this.createdAt;
  }

  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  public OffsetDateTime getModifiedAt() {
    return this.modifiedAt;
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
