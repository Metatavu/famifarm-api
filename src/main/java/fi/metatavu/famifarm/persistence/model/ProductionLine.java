package fi.metatavu.famifarm.persistence.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import fi.metatavu.famifarm.rest.model.Facility;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * JPA entity for storing production lines
 * 
 * @author Ville Koivukangas
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class ProductionLine {

  @Id
  @Column(nullable = false)
  @NotNull
  private UUID id;
  
  @Column(nullable = false)
  @NotNull
  private String lineNumber;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Facility facility;

  @Column(nullable = false)
  @NotNull
  private UUID creatorId;
  
  @Column(nullable = false)
  @NotNull
  private UUID lastModifierId;

  @Column
  private Integer defaultGutterHoleCount;
  
  @Column (nullable = false)
  private OffsetDateTime createdAt;

  @Column (nullable = false)
  private OffsetDateTime modifiedAt;
  
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
  
  public String getLineNumber() {
    return lineNumber;
  }
  
  public void setLineNumber(String lineNumber) {
    this.lineNumber = lineNumber;
  }

  public Integer getDefaultGutterHoleCount() {
    return defaultGutterHoleCount;
  }

  public void setDefaultGutterHoleCount(Integer defaultGutterHoleCount) {
    this.defaultGutterHoleCount = defaultGutterHoleCount;
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
