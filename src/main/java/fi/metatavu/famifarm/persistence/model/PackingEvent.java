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
 * JPA entity for sowing event
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PackingEvent extends Event {

  @ManyToOne
  private PackageSize packageSize;

  @NotNull
  @Column(nullable = false)
  private Integer packedCount;

  public PackageSize getPackageSize() {
    return packageSize;
  }

  public void setPackageSize(PackageSize packageSize) {
    this.packageSize = packageSize;
  }

  public Integer getPackedCount() {
    return packedCount;
  }

  public void setPackedCount(Integer packedCount) {
    this.packedCount = packedCount;
  }

  @Transient
  @Override
  public EventType getType() {
    return EventType.PACKING;
  }

}
