package fi.metatavu.famifarm.persistence.model;

import java.beans.Transient;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.famifarm.rest.model.EventType;

/**
 * JPA entity for table spread event
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class TableSpreadEvent extends Event {

  @Column(nullable = false)
  private Integer trayCount;

  public Integer getTrayCount() {
    return trayCount;
  }

  public void setTrayCount(Integer trayCount) {
    this.trayCount = trayCount;
  }

  @Transient
  @Override
  public EventType getType() {
    return EventType.TABLE_SPREAD;
  }

}
