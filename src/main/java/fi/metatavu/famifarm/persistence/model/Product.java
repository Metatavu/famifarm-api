package fi.metatavu.famifarm.persistence.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import fi.metatavu.famifarm.rest.model.Facility;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * JPA entity for product
 * 
 * @author Ville Koivukangas
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Product {

  @Id
  @Column(nullable = false)
  @NotNull
  private UUID id;

  @ManyToOne(optional = false)
  private LocalizedEntry name;

  @Column(nullable = false)
  private boolean isSubcontractorProduct;

  @Column(nullable = false)
  private boolean isActive;

  @Column(nullable = false)
  private boolean isEndProduct;

  @Column(nullable = false)
  private boolean isRawMaterial;

  @Column(nullable = false)
  private Double salesWeight;

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

  @Column(nullable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false)
  private OffsetDateTime modifiedAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getCreatorId() {
    return creatorId;
  }

  public LocalizedEntry getName() {
    return name;
  }

  public void setName(LocalizedEntry name) {
    this.name = name;
  }

  public void setIsSubcontractorProduct(boolean isSubcontractorProduct) {
    this.isSubcontractorProduct = isSubcontractorProduct;
  }

  public boolean isSubcontractorProduct() {
    return this.isSubcontractorProduct;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setIsEndProduct(boolean isEndProduct) {
    this.isEndProduct = isEndProduct;
  }

  public boolean isEndProduct() { return isEndProduct; }

  public void setIsRawMaterial(boolean isRawMaterial) {
     this.isRawMaterial = isRawMaterial;
  }

  public boolean isRawMaterial() { return isRawMaterial; }

  public void setSalesWeight(Double salesWeight) {
    this.salesWeight = salesWeight;
  }

  public Double getSalesWeight () {
    return salesWeight;
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
