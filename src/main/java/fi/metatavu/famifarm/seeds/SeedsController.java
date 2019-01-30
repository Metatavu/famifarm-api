package fi.metatavu.famifarm.seeds;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.dao.SeedDAO;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.Seed;

/**
 * Controller class for seeds
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SeedsController {

  @Inject
  private LocalizedValueController localizedValueController;
  
  @Inject
  private SeedDAO seedDAO;
  
  /**
   * Creates new seed
   * 
   * @param name name
   * @param creatorId creatorId
   * @return created seed
   */
  public Seed createSeed(LocalizedEntry name, UUID creatorId) {
    return seedDAO.create(UUID.randomUUID(), name, creatorId, creatorId);
  }

  /**
   * Finds seed by id
   * 
   * @param seedId seed id
   * @return seed
   */
  public Seed findSeed(UUID seedId) {
    return seedDAO.findById(seedId);
  }

  /**
   * Lists seeds
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of seeds
   */
  public List<Seed> listSeeds(Integer firstResult, Integer maxResults) {
    return seedDAO.listAll(firstResult, maxResults);
  }
  
  /**
   * Update seed
   *
   * @param name name
   * @param lastModifierId lastModifierId
   * @return updated seed
   */
  public Seed updateSeed(Seed seed, LocalizedEntry name, UUID lastModifierId) {
    seedDAO.updateName(seed, name, lastModifierId);
    return seed;
  }

  /**
   * Deletes a seed
   * 
   * @param seed seed to be deleted
   */
  public void deleteSeed(Seed seed) {
    LocalizedEntry name = seed.getName();
    seedDAO.delete(seed);
    localizedValueController.deleteEntry(name);
  }
  
}
