package fi.metatavu.famifarm.persistence.model;

import java.beans.Transient;
import java.time.OffsetDateTime;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.famifarm.rest.model.EventType;

/**
 * JPA entity for sowing event
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class HarvestEvent extends Event {

  @Enumerated(EnumType.STRING)
  private fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum harvestType;

  @ManyToOne
  private ProductionLine productionLine;
  
  @NotNull
  @Column (nullable = false)
  private Integer gutterCount;

  @NotNull
  @Column (nullable = false)
  private Integer gutterHoleCount;

  @Column(nullable = false)
  private OffsetDateTime sowingDate;

  public fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum getHarvestType() {
    return harvestType;
  }

  public void setHarvestType(fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum harvestType) {
    this.harvestType = harvestType;
  }

  public ProductionLine getProductionLine() {
    return productionLine;
  }

  public void setProductionLine(ProductionLine productionLine) {
    this.productionLine = productionLine;
  }

  public Integer getGutterCount() {
    return gutterCount;
  }

  public void setGutterCount(Integer gutterCount) {
    this.gutterCount = gutterCount;
  }

  public OffsetDateTime getSowingDate() {
    return sowingDate;
  }

  public void setSowingDate(OffsetDateTime sowingDate) {
    this.sowingDate = sowingDate;
  }

  public Integer getGutterHoleCount() {
    return gutterHoleCount;
  }

  public void setGutterHoleCount(Integer gutterHoleCount) {
    this.gutterHoleCount = gutterHoleCount;
  }

  @Transient
  @Override
  public EventType getType() {
    return EventType.HARVEST;
  }

}
