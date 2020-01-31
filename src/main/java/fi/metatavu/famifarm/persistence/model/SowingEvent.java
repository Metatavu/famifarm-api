package fi.metatavu.famifarm.persistence.model;

import java.beans.Transient;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.PotType;

/**
 * JPA entity for sowing event
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class SowingEvent extends Event {
  
  @ManyToOne
  private ProductionLine productionLine;
  
  @Enumerated (EnumType.STRING)
  private PotType potType;
  
  @Column (nullable = false)
  private Integer amount;

  public ProductionLine getProductionLine() {
    return productionLine;
  }

  public void setProductionLine(ProductionLine productionLine) {
    this.productionLine = productionLine;
  }

  public PotType getPotType() {
    return potType;
  }

  public void setPotType(PotType potType) {
    this.potType = potType;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }  

  @Transient
  @Override
  public EventType getType() {
    return EventType.SOWING;
  }

}
