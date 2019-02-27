package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;

/**
 * DAO class for localized entries
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class LocalizedEntryDAO extends AbstractDAO<LocalizedEntry> {

  /**
   * Creates new LocalizedEntry
   *
   * @return created LocalizedEntry
   */
  public LocalizedEntry create(UUID id) {
    LocalizedEntry localizedEntry = new LocalizedEntry();
    localizedEntry.setId(id);
    return persist(localizedEntry);
  }

}
