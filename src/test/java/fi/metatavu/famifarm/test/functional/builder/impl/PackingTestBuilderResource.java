package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.PackingsApi;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Packing;
import fi.metatavu.famifarm.client.model.PackingState;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for PackingsApi
 * 
 * @author simeon
 *
 */
public class PackingTestBuilderResource extends AbstractTestBuilderResource<Packing, PackingsApi> {
  
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
  public Packing create(UUID productId, OffsetDateTime time, Integer packedCount, PackingState packingState, PackageSize packageSize) {
    Packing packing = new Packing();
    packing.setProductId(productId);
    packing.setTime(time);
    packing.setPackedCount(packedCount);
    packing.setState(packingState);
    packing.setPackageSizeId(packageSize.getId());
    return addClosable(getApi().createPackaging(packing));
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
  public List<Packing> list(int firstResult, int maxResults, UUID productId, PackingState packingState, OffsetDateTime createdAfter, OffsetDateTime createdBefore) {
    return addClosable(getApi().listPackings(firstResult, maxResults, productId, packingState, createdAfter.toString(), createdBefore.toString()));
  }
  
  /**
   * Finds a packing by id
   * 
   * @param packingId
   * @return packing with corresponding id or null if not found
   */
  public Packing find(UUID packingId) {
    return addClosable(getApi().findPacking(packingId));
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
  public void delete(Packing packing) {
    getApi().deletePacking(packing.getId());
    removeClosable(closable -> !closable.getId().equals(packing.getId()));
  }
  
  /**
   * Asserts that packings have identical properties
   * 
   * @param expected
   * @param actual
   * @throws IOException
   * @throws JSONException
   */
  public void assertPackingsEqual(Packing expected, Packing actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }
  
  public void assertFindFailStatus(int expectedStatus, UUID packingId) {
    try {
      getApi().findPacking(packingId);
      fail(String.format("Expected find to fail with status %d.", expectedStatus));
    } catch (FeignException e){
      assertEquals(expectedStatus, e.status());
    }
  }
  
  @Override
  public void clean(Packing packing) {
    getApi().deletePacking(packing.getId());  
  }
}
