package fi.metatavu.famifarm.seedbatches;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.SeedBatchDAO;
import fi.metatavu.famifarm.persistence.model.Seed;
import fi.metatavu.famifarm.persistence.model.SeedBatch;

/**
 * Controller for seed batches
 * 
 * @author Ville Koivukangas
 *
 */
@ApplicationScoped
public class SeedBatchesController {

  @Inject
  private SeedBatchDAO seedBatchDAO;

  /**
   * Creates new seed batch
   * 
   * @param code code
   * @param seed seed
   * @param time time
   * @param userId userId
   * @return created seed batch
   */
  public SeedBatch createSeedBatch(String code, Seed seed, OffsetDateTime time, UUID userId) {
    return seedBatchDAO.create(UUID.randomUUID(), code, seed, time, userId, userId);
  }

  /**
   * Finds seed batch by id
   * 
   * @param seedBatchId seed batch id
   * @return seed batch
   */
  public SeedBatch findSeedBatch(UUID seedBatchId) {
    return seedBatchDAO.findById(seedBatchId);
  }

  /**
   * Lists seeds batches
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @param active if true or null, list only active seed batches
   * @return list of seed batches
   */
  public List<SeedBatch> listSeedBatches(Integer firstResult, Integer maxResults, Boolean active) {
    return seedBatchDAO.listAll(firstResult, maxResults, active);
  }

  /**
   * Creates new seed batch
   * 
   * @param seedBatch seedBatch
   * @param code code
   * @param seed seed
   * @param time time
   * @param lastModifierId lastModifierId
   * @return created seed batch
   */
  public SeedBatch updateSeedBatch(SeedBatch seedBatch, String code, Seed seed, OffsetDateTime time, Boolean active, UUID lastModifierId) {
    seedBatchDAO.updateCode(seedBatch, code, lastModifierId);
    seedBatchDAO.updateSeed(seedBatch, seed, lastModifierId);
    seedBatchDAO.updateTime(seedBatch, time, lastModifierId);
    seedBatchDAO.updateActive(seedBatch, active, lastModifierId);
    return seedBatch;
  }

  /**
   * Deletes a seed batch
   * 
   * @param seedBatch seedBatch to be deleted
   */
  public void deleteSeedBatch(SeedBatch seedBatch) {
    seedBatchDAO.delete(seedBatch);
  }

}
