package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.Seed;

/**
 * DAO class for seeds
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SeedDAO extends AbstractDAO<Seed> {

  /**
   * Creates new seed
   *
   * @param name name
   * @return created seed
   * @param lastModifier modifier
   */
  public Seed create(UUID id, LocalizedEntry name, UUID creatorId, UUID lastModifierId) {
    Seed seed = new Seed();
    seed.setName(name);
    seed.setId(id);
    seed.setCreatorId(creatorId);
    seed.setLastModifierId(lastModifierId);
    return persist(seed);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifier modifier
   * @return updated seed
   */
  public Seed updateName(Seed seed, LocalizedEntry name, UUID lastModifierId) {
    seed.setLastModifierId(lastModifierId);
    seed.setName(name);
    return persist(seed);
  }

}
