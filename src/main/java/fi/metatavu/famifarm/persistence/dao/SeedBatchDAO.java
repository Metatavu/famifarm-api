package fi.metatavu.famifarm.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import fi.metatavu.famifarm.persistence.model.Seed;
import fi.metatavu.famifarm.persistence.model.SeedBatch;

/**
 * DAO class for seed batches
 * 
 * @author Ville Koivukangas
 */
public class SeedBatchDAO extends AbstractDAO<SeedBatch> {

  /**
   * Creates new seed batch
   *
   * @param id id
   * @param code code
   * @param seed seed
   * @param time time
   * @param lastModifier modifier
   * @return created seed
   */
  public SeedBatch create(UUID id, String code, Seed seed, OffsetDateTime time, UUID creatorId, UUID lastModifierId) {
  	SeedBatch seedBatch = new SeedBatch();
  	seedBatch.setId(id);
  	seedBatch.setCode(code);
  	seedBatch.setSeed(seed);
  	seedBatch.setTime(time);
  	seedBatch.setCreatorId(creatorId);
  	seedBatch.setLastModifierId(lastModifierId);
    return persist(seedBatch);
  }

  /**
   * Updates code
   *
   * @param code code
   * @param lastModifier modifier
   * @return updated seed batch
   */
  public SeedBatch updateCode(SeedBatch seedBatch, String code, UUID lastModifierId) {
  	seedBatch.setLastModifierId(lastModifierId);
  	seedBatch.setCode(code);
    return persist(seedBatch);
  }
  
  /**
   * Updates seed
   *
   * @param seed seed
   * @param lastModifier modifier
   * @return updated seed batch
   */
  public SeedBatch updateSeed(SeedBatch seedBatch, Seed seed, UUID lastModifierId) {
  	seedBatch.setLastModifierId(lastModifierId);
  	seedBatch.setSeed(seed);
    return persist(seedBatch);
  }
  
  /**
   * Updates time
   *
   * @param time time
   * @param lastModifier modifier
   * @return updated seed batch
   */
  public SeedBatch updateTime(SeedBatch seedBatch, OffsetDateTime time, UUID lastModifierId) {
  	seedBatch.setLastModifierId(lastModifierId);
  	seedBatch.setTime(time);
    return persist(seedBatch);
  }

}
