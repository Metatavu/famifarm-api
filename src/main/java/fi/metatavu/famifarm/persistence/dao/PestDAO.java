package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.Pest;

/**
 * DAO class for pest
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PestDAO extends AbstractDAO<Pest> {

  /**
   * Creates new pest
   *
   * @param name name
   * @param lastModifier modifier
   * @return created pest
   */
  public Pest create(UUID id, LocalizedEntry name, UUID creatorId, UUID lastModifierId) {
    Pest pest = new Pest();
    pest.setName(name);
    pest.setId(id);
    pest.setCreatorId(creatorId);
    pest.setLastModifierId(lastModifierId);
    return persist(pest);
  }

  /**
   * Updates name
   *
   * @param pest pest
   * @param name name
   * @param lastModifier modifier
   * @return updated pest
   */
  public Pest updateName(Pest pest, LocalizedEntry name, UUID lastModifierId) {
    pest.setLastModifierId(lastModifierId);
    pest.setName(name);
    return persist(pest);
  }

}
