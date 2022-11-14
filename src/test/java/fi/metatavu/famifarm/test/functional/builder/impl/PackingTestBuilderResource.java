package fi.metatavu.famifarm.test.functional.builder.impl;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.PackingsApi;
import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;
import org.json.JSONException;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test builder resource for PackingsApi
 *
 * @author simeon
 */
public class PackingTestBuilderResource extends AbstractTestBuilderResource<Packing, PackingsApi> {

  private final HashMap<UUID, Facility> packingFacilityMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param apiClient initialized API client
   */
  public PackingTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }

  /**
   * Creates packing object from parameters
   *
   * @param productId
   * @param campaignId
   * @param packingType
   * @param time
   * @param packedCount
   * @param packingState
   * @param packageSize
   * @return
   */
  public Packing buildPackingObject(UUID productId, UUID campaignId, PackingType packingType, OffsetDateTime time, Integer packedCount, PackingState packingState, PackageSize packageSize) {
    Packing packing = new Packing();
    packing.setProductId(productId);
    packing.setCampaignId(campaignId);
    packing.setType(packingType);
    packing.setTime(time);
    packing.setPackedCount(packedCount);
    packing.setState(packingState);

    if (packageSize != null) {
      packing.setPackageSizeId(packageSize.getId());
    }

    return packing;
  }

  /**
   * Creates a new packing for testing purposes
   *
   * @param productId    product id
   * @param campaignId   campaign id
   * @param packingType  packing type
   * @param time         packing time
   * @param packedCount  packed count
   * @param packingState packing state
   * @param packageSize  package size
   * @return created packing
   */
  public Packing create(UUID productId, UUID campaignId, PackingType packingType, OffsetDateTime time, Integer packedCount, PackingState packingState, PackageSize packageSize, Facility facility) {
    Packing packing = buildPackingObject(productId, campaignId, packingType, time, packedCount, packingState, packageSize);
    Packing created = getApi().createPacking(packing, facility);
    packingFacilityMap.put(created.getId(), facility);
    return addClosable(created);
  }

  /**
   * Finds packing that matches given parameters or lists all packings
   *
   * @param firstResult
   * @param maxResults
   * @param productId
   * @param packingState
   * @param createdAfter
   * @param createdBefore
   * @return
   */
  public List<Packing> list(Integer firstResult, Integer maxResults, UUID productId, PackingState packingState, OffsetDateTime createdAfter, OffsetDateTime createdBefore, Facility facility) {
    String createdAfterStr = null;
    if (createdAfter != null) {
      createdAfterStr = createdAfter.toString();
    }
    
    String createdBeforeStr = null;
    if (createdBefore != null) {
      createdBeforeStr = createdBefore.toString();
    }

    return getApi().listPackings(facility, firstResult, maxResults, productId, null, packingState, createdAfterStr, createdBeforeStr);
  }
  
  /**
   * Finds a packing by id
   * 
   * @param packingId
   * @return packing with corresponding id or null if not found
   */
  public Packing find(UUID packingId, Facility facility) {
    return getApi().findPacking(facility, packingId);
  }
  
  /**
   * Updates a packing
   * 
   * @param packing
   * @return packing
   */
  public Packing update(Packing packing, Facility facility) {
    return getApi().updatePacking(packing, facility, packing.getId());
  }
  
  /**
   * Deletes a packing
   * 
   * @param packing to be deleted
   * @return
   */
  public void delete(Packing packing, Facility facility) {
    getApi().deletePacking(facility, packing.getId());
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

  public void assertCreateFailStatus(int expectedStatus, UUID productId, UUID campaignId, PackingType packingType, OffsetDateTime time, Integer packedCount, PackingState packingState, PackageSize packageSize, Facility facility) {
    try {
      Packing packing = buildPackingObject(productId, campaignId, packingType, time, packedCount, packingState, packageSize);
      getApi().createPacking(packing, facility);
      fail(String.format("Expected find to fail with status %d.", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  public void assertFindFailStatus(int expectedStatus, UUID packingId, Facility facility) {
    try {
      getApi().findPacking(facility, packingId);
      fail(String.format("Expected find to fail with status %d.", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that deletion fails with given status
   * @param expectedStatus
   * @param packingId
   * @param facility
   */
  public void assertDeleteFailStatus(int expectedStatus, UUID packingId, Facility facility) {
    try {
      getApi().deletePacking(facility, packingId);
      fail(String.format("Expected find to fail with status %d.", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  @Override
  public void clean(Packing packing) {
    getApi().deletePacking(packingFacilityMap.get(packing.getId()), packing.getId());
  }
}
