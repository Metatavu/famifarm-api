package fi.metatavu.famifarm.persistence.dao;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.LocalizedValue;
import fi.metatavu.famifarm.persistence.model.LocalizedValue_;

/**
 * DAO class for localized values
 * 
 * @author Antti Leppä
 */
public class LocalizedValueDAO extends AbstractDAO<LocalizedValue> {

  /**
   * Creates new localizedValue
   *
   * @param entry entry
   * @param locale locale
   * @param value value
   * 
   * @return created localizedValue
   */
  public LocalizedValue create(UUID id, LocalizedEntry entry, Locale locale, String value) {
    LocalizedValue localizedValue = new LocalizedValue();
    localizedValue.setEntry(entry);
    localizedValue.setLocale(locale);
    localizedValue.setValue(value);
    localizedValue.setId(id);
    return persist(localizedValue);
  }

  /**
   * Lists values by entries
   * 
   * @param entry entry
   * @return values by entries
   */
  public List<LocalizedValue> listByEntry(LocalizedEntry entry) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<LocalizedValue> criteria = criteriaBuilder.createQuery(LocalizedValue.class);
    Root<LocalizedValue> root = criteria.from(LocalizedValue.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(LocalizedValue_.entry), entry));
    
    return entityManager.createQuery(criteria).getResultList();
  }  
  
  /**
   * Finds value by entry and locale
   * 
   * @param entry entry
   * @param locale locale
   * @return value
   */
  public LocalizedValue findByEntryAndLocale(LocalizedEntry entry, Locale locale) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<LocalizedValue> criteria = criteriaBuilder.createQuery(LocalizedValue.class);
    Root<LocalizedValue> root = criteria.from(LocalizedValue.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(LocalizedValue_.entry), entry),
        criteriaBuilder.equal(root.get(LocalizedValue_.locale), locale)    
      )    
    );
    
    TypedQuery<LocalizedValue> query = entityManager.createQuery(criteria);
    
    return getSingleResult(query);
  }

  /**
   * Updates value
   *
   * @param value value
   * @return updated localizedValue
   */
  public LocalizedValue updateValue(LocalizedValue localizedValue, String value) {
    localizedValue.setValue(value);
    return persist(localizedValue);
  }

}
