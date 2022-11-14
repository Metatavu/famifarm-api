package fi.metatavu.famifarm.seeds;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.dao.SeedDAO;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.Seed;
import fi.metatavu.famifarm.rest.model.Facility;

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
   * @param facility  facility
   * @param name      name
   * @param creatorId creatorId
   * @return created seed
   */
  public Seed createSeed(Facility facility, LocalizedEntry name, UUID creatorId) {
    return seedDAO.create(UUID.randomUUID(), facility, name, creatorId, creatorId);
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
   * @param facility required facility filter
   * @param firstResult first result
   * @param maxResults  max results
   * @return list of seeds
   */
  public List<Seed> listSeeds(Facility facility, Integer firstResult, Integer maxResults) {
    return seedDAO.list(facility, firstResult, maxResults);
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
