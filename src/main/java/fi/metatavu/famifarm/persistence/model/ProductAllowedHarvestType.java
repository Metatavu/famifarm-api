package fi.metatavu.famifarm.persistence.model;

import java.util.UUID;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.famifarm.rest.model.HarvestEventType;


/**
 * JPA entity for storing allowed harvest types for products
 * 
 * @author Heikki Kurhinen
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class ProductAllowedHarvestType {

  @Id
  private UUID id;
  
  @ManyToOne
  private Product product;

  @Enumerated(EnumType.STRING)
  private HarvestEventType harvestType;

  public void setId(UUID id) {
    this.id = id;
  }
  
  public UUID getId() {
    return id;
  }

  public HarvestEventType getHarvestType() {
    return harvestType;
  }

  public void setHarvestType(HarvestEventType harvestType) {
    this.harvestType = harvestType;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }
}
