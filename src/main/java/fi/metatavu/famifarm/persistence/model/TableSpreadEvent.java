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
  private Integer tableCount;

  @Column(nullable = false)
  private String location;

  public Integer getTableCount() {
    return tableCount;
  }

  public void setTableCount(Integer tableCount) {
    this.tableCount = tableCount;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  @Transient
  @Override
  public EventType getType() {
    return EventType.TABLE_SPREAD;
  }

}
