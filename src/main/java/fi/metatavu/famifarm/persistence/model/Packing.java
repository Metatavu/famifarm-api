package fi.metatavu.famifarm.persistence.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.famifarm.rest.model.PackageType;

/**
 * JPA entity for Packing
 * 
 * @author simeon
 *
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Packing {
  @ManyToOne
  private PackageSize packageSize;
  
  @NotNull
  @Column(nullable = false)
  private Integer packedCount;
  
  @Id
  @Column(nullable = false)
  @NotNull
  private UUID id;
  
  @Column(nullable = false)
  @NotNull
  private UUID productId;
  
  @Column(nullable = false)
  @NotNull
  private PackageState packageState;
  
  @Column(nullable = false)
  private OffsetDateTime startTime;

  @Column
  private OffsetDateTime endTime;
  
  @Column(nullable = false)
  @NotNull
  private UUID creatorId;

  @Column(nullable = false)
  @NotNull
  private UUID lastModifierId;

  @Column(nullable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false)
  private OffsetDateTime modifiedAt;
  
  public PackageSize getPackageSize() {
    return packageSize;
  }

  public void setPackageSize(PackageSize packageSize) {
    this.packageSize = packageSize;
  }

  public Integer getPackedCount() {
    return packedCount;
  }

  public void setPackedCount(Integer packedCount) {
    this.packedCount = packedCount;
  }
  
  public PackageState getPackageState() {
    return this.packageState;
  }
  
  public void setPackageState(PackageState packageState) {
    this.packageState = packageState;
  }
  
  public UUID getProductId() {
    return this.productId;
  }
  
  public void setProductId(UUID productId) {
    this.productId = productId;
  }
  
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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
  
  public OffsetDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
  }

  public OffsetDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(OffsetDateTime endTime) {
    this.endTime = endTime;
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
