package fi.metatavu.famifarm.persistence.model;

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
public class CampaignProduct {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(nullable = false)
  private int count;

  @ManyToOne(optional = false)
  private Product product;

  @ManyToOne(optional = false)
  private Campaign campaign;

  @Column(nullable = false)
  private UUID lastModifierId;

  @Column(nullable = false)
  private UUID creatorId;

  @Column(nullable = false)
  private OffsetDateTime modifiedAt;

  @Column(nullable = false)
  private OffsetDateTime createdAt;

  public void setId (UUID id) {
    this.id = id;
  }

  public UUID getId () {
    return this.id;
  }

  public void setCount (int count) {
    this.count = count;
  }

  public int getCount () {
    return this.count;
  }

  public void setProduct (Product product) {
    this.product = product;
  }

  public Product getProduct () {
    return this.product;
  }

  public void setCampaign (Campaign campaign) {
    this.campaign = campaign;
  }

  public Campaign getCampaign () {
    return this.campaign;
  }

  public void setLastModifierId (UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
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
