package fi.metatavu.famifarm.persistence.model;

import java.util.Locale;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * JPA entity for storing localized values
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class LocalizedValue {

  @Id
  private UUID id;

  @ManyToOne (optional = false)
  private LocalizedEntry entry;
  
  @Column(nullable = false)
  @NotNull
  private Locale locale;

  @Column(nullable = false)
  @NotNull
  @NotEmpty
  @Lob
  private String value;
  
  public void setId(UUID id) {
    this.id = id;
  }
  
  public UUID getId() {
    return id;
  }
  
  public LocalizedEntry getEntry() {
    return entry;
  }
  
  public void setEntry(LocalizedEntry entry) {
    this.entry = entry;
  }
  
  public Locale getLocale() {
    return locale;
  }
  
  public void setLocale(Locale locale) {
    this.locale = locale;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
}
