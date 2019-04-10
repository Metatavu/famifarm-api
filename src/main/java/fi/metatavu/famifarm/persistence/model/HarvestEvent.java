package fi.metatavu.famifarm.persistence.model;

import java.beans.Transient;

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

  @ManyToOne
  private Team team;
  
  @NotNull
  @Column (nullable = false)
  private Integer gutterCount;

  public fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum getHarvestType() {
    return harvestType;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
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

  @Transient
  @Override
  public EventType getType() {
    return EventType.HARVEST;
  }

}
