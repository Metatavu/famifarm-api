package fi.metatavu.famifarm.persistence.model;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
  
  private Integer gutterNumber;
  
  @ManyToOne
  private SeedBatch seedBatch;
  
  private CellType cellType;
  
  private Double amount;

  public ProductionLine getProductionLine() {
    return productionLine;
  }

  public void setProductionLine(ProductionLine productionLine) {
    this.productionLine = productionLine;
  }

  public Integer getGutterNumber() {
    return gutterNumber;
  }

  public void setGutterNumber(Integer gutterNumber) {
    this.gutterNumber = gutterNumber;
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

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }  
  
}
