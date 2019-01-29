package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.Batch;

/**
 * DAO class for batches
 * 
 * @author Ville Koivukangas
 */
public class BatchDAO extends AbstractDAO<Batch> {

  /**
   * Creates new batch
   *
   * @param id id
   * @param product product
   * @param creatorId creatorId
   * @param lastModifierId lastModifierId
   * @return created seed
   */
  public Batch create(UUID id, Product product, UUID creatorId, UUID lastModifierId) {
    Batch batch = new Batch();
    batch.setId(id);
    batch.setProduct(product);
    batch.setCreatorId(creatorId);
    batch.setLastModifierId(lastModifierId);
    return persist(batch);
  }

  /**
   * Updates product
   *
   * @param batch batch
   * @param product product
   * @param lastModifier modifier
   * @return updated batch
   */
  public Batch updateProduct(Batch batch, Product product, UUID lastModifierId) {
    batch.setLastModifierId(lastModifierId);
    batch.setProduct(product);
    return persist(batch);
  }
  
}
