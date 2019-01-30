package fi.metatavu.famifarm.batches;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.BatchDAO;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Product;

/**
 * Controller for seed batches
 * 
 * @author Ville Koivukangas
 *
 */
public class BatchController {

  @Inject
  private BatchDAO batchDAO;

  /**
   * Creates new batch
   * 
   * @param product product
   * @param userId userId
   * @return created batch
   */
  public Batch createBatch(Product product, UUID userId) {
    return batchDAO.create(UUID.randomUUID(), product, userId, userId);
  }

  /**
   * Finds batch by id
   * 
   * @param batchId batchId
   * @return batch
   */
  public Batch findBatch(UUID batchId) {
    return batchDAO.findById(batchId);
  }

  /**
   * Lists batchs
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of batches
   */
  public List<Batch> listBatches(Integer firstResult, Integer maxResults) {
    return batchDAO.listAll(firstResult, maxResults);
  }

  /**
   * Updates batch
   * 
   * @param batch batch
   * @param product product
   * @param lastModifierId lastModifierId
   * @return updated batch
   */
  public Batch updateBatch(Batch batch, Product product, UUID lastModifierId) {
    batchDAO.updateProduct(batch, product, lastModifierId);
    return batch;
  }

  /**
   * Deletes batch
   * 
   * @param batch batch to be deleted
   */
  public void deleteBatch(Batch batch) {
    batchDAO.delete(batch);
  }

}
