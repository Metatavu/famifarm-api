package fi.metatavu.famifarm.persistence.model;

import java.beans.Transient;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.famifarm.rest.model.Event.TypeEnum;

/**
 * JPA entity for sowing event
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PlantingEvent extends Event {

  @ManyToOne
  private ProductionLine productionLine;

  @NotNull
  @Column (nullable = false)
  private Integer gutterNumber;

  @NotNull
  @Column (nullable = false)
  private Integer gutterCount;

  @NotNull
  @Column (nullable = false)
  private Integer trayCount;

  @NotNull
  @Column (nullable = false)
  private Integer workerCount;

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

  public Integer getGutterCount() {
    return gutterCount;
  }

  public void setGutterCount(Integer gutterCount) {
    this.gutterCount = gutterCount;
  }

  public Integer getTrayCount() {
    return trayCount;
  }

  public void setTrayCount(Integer trayCount) {
    this.trayCount = trayCount;
  }

  public Integer getWorkerCount() {
    return workerCount;
  }

  public void setWorkerCount(Integer workerCount) {
    this.workerCount = workerCount;
  }

  @Transient
  @Override
  public TypeEnum getType() {
    return TypeEnum.PLANTING;
  }

}
