package fi.metatavu.famifarm.test.functional.builder.impl;

import java.time.OffsetDateTime;
import java.util.UUID;

import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Packing;

/**
 * Test builder resource for PackingsApi
 * 
 * @author simeon
 *
 */
public class PackingTestBuilderResource extends AbstractTestBuilderResource<Packing, PackingsApi>{
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public PackingTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates a new packing for testing purposes
   * 
   * @param productId
   * @param time
   * @param packedCount
   * @param packageState
   * @param packageSize
   * @return
   */
  public Packing create(UUID productId, OffsetDateTime time, Integer packedCount, PackageState packageState, PackageSize packageSize) {
    Packing packing = new Packing();
    packing.setProductId(productId);
    packing.setTime(time);
    packing.setPackedCount(packedCount);
    packing.setPackageState(packageState);
    packing.setPackageSize(packageSize);
    return addClosable(getApi().createPacking(packing));
  }
  
  /**
   * Finds packing that matches given parameters or lists all packings
   * 
   * @param firstResult
   * @param maxResults
   * @param productId
   * @param packageState
   * @param createdAfter
   * @param createdBefore
   * @return
   */
  public List<Packing> list(int firstResult, int maxResults, UUID productId, PackageState packageState, OffsetDateTime createdAfter, OffsetDateTime createdBefore) {
    return addClosable(getApi.listPackings(firstResult, maxResults, productId, packageState, createdAfter, createdBefore));
  }
  
  /**
   * Finds a packing by id
   * 
   * @param packingId
   * @return packing with corresponding id or null if not found
   */
  public Packing find(UUID packingId) {
    return addClosable(getApi.findPacking(packingId));
  }
  
  /**
   * Updates a packing
   * 
   * @param packing
   * @return packing
   */
  public Packing update(Packing packing) {
    return addClosable(getApi().updatePacking(packing, packing.getId()));
  }
  
  /**
   * Deletes a packing
   * 
   * @param packing to be deleted
   * @return
   */
  public Packing delete(Packing packing) {
    getApi().deletePacking(packing.getId());
    removeClosable(closable -> !closable.getId().equals(packing.getId()));
  }
  
  @Override
  public void clean(Packing packing) {
    getApi().deletePacking(packing.getId());  
  }
}
