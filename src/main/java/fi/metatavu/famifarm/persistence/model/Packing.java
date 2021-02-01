package fi.metatavu.famifarm.persistence.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import fi.metatavu.famifarm.rest.model.PackingType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.famifarm.rest.model.PackingState;

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

  @Column
  private Integer packedCount;
  
  @Id
  @Column(nullable = false)
  @NotNull
  private UUID id;

  @ManyToOne
  private Product product;

  @ManyToOne
  private Campaign campaign;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private PackingType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private PackingState packingState;
  
  @Column(nullable = false)
  private OffsetDateTime time;
  
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
  
  public PackingState getPackingState() {
    return this.packingState;
  }
  
  public void setPackingState(PackingState packageState) {
    this.packingState = packageState;
  }
  
  public Product getProduct() {
    return this.product;
  }
  
  public void setProduct(Product product) {
    this.product = product;
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
  
  public OffsetDateTime getTime() {
    return time;
  }

  public void setTime(OffsetDateTime time) {
    this.time = time;
  }

  public Campaign getCampaign() {
    return this.campaign;
  }

  public void setCampaign(Campaign campaign) {
    this.campaign = campaign;
  }

  public PackingType getType() {
    return this.type;
  }

  public void setType(PackingType type) {
    this.type = type;
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
