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

import fi.metatavu.famifarm.rest.model.CellType;
import fi.metatavu.famifarm.rest.model.EventType;

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
  
  @ManyToOne
  private SeedBatch seedBatch;
  
  @Enumerated (EnumType.STRING)
  private CellType cellType;
  
  @Column (nullable = false)
  private Integer amount;

  public ProductionLine getProductionLine() {
    return productionLine;
  }

  public void setProductionLine(ProductionLine productionLine) {
    this.productionLine = productionLine;
  }

  public SeedBatch getSeedBatch() {
    return seedBatch;
  }

  public void setSeedBatch(SeedBatch seedBatch) {
    this.seedBatch = seedBatch;
  }

  public CellType getCellType() {
    return cellType;
  }

  public void setCellType(CellType cellType) {
    this.cellType = cellType;
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
