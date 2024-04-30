package fi.metatavu.famifarm.persistence.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Packing Basket entity
 */
@Entity
@Cacheable(true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PackingBasket {

  @Id
  @Column(nullable = false)
  @NotNull
  private UUID id;

  @ManyToOne
  private Packing packing;

  @ManyToOne
  private Product product;

  @Column(nullable = false)
  private Integer count;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Packing getPacking() {
    return packing;
  }

  public void setPacking(Packing packing) {
    this.packing = packing;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }
}
