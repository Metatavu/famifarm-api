package fi.metatavu.famifarm.persistence.model;

import java.beans.Transient;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.famifarm.rest.model.EventType;

/**
 * JPA entity for wastage event
 * 
 * @author Heikki Kurhinen
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class WastageEvent extends Event {

  @ManyToOne(optional = false)
  private WastageReason wastageReason;
  
  @ManyToOne
  private ProductionLine productionLine;

  @NotNull
  @Column (nullable = false)
  private Integer amount;
  
  private EventType phase;

  public WastageReason getWastageReason() {
    return wastageReason;
  }

  public ProductionLine getProductionLine() {
    return productionLine;
  }

  public void setProductionLine(ProductionLine productionLine) {
    this.productionLine = productionLine;
  }

  public void setWastageReason(WastageReason wastageReason) {
    this.wastageReason = wastageReason;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }
  
  public EventType getPhase() {
    return phase;
  }
  
  public void setPhase(EventType phase) {
    this.phase = phase;
  }

  @Transient
  @Override
  public EventType getType() {
    return EventType.WASTAGE;
  }

}
