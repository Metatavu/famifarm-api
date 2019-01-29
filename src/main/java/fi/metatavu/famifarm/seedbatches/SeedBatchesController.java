package fi.metatavu.famifarm.seedbatches;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

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
   * @return list of seed batches
   */
  public List<SeedBatch> listSeedBatches(Integer firstResult, Integer maxResults) {
    return seedBatchDAO.listAll(firstResult, maxResults);
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
  public SeedBatch updateSeedBatch(SeedBatch seedBatch, String code, Seed seed, OffsetDateTime time, UUID lastModifierId) {
    seedBatchDAO.updateCode(seedBatch, code, lastModifierId);
    seedBatchDAO.updateSeed(seedBatch, seed, lastModifierId);
    seedBatchDAO.updateTime(seedBatch, time, lastModifierId);
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
