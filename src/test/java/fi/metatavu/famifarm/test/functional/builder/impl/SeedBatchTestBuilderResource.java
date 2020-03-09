package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.SeedBatchesApi;
import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for seed batches
 * 
 * @author Ville Koivukangas
 */
public class SeedBatchTestBuilderResource extends AbstractTestBuilderResource<SeedBatch, SeedBatchesApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public SeedBatchTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new seed batch
   * 
   * @param name name
   * @param seed seed
   * @return created seed
   */
  public SeedBatch create(String code, Seed seed, OffsetDateTime time) {
    SeedBatch seedBatch = new SeedBatch();
    seedBatch.setCode(code);
    seedBatch.setSeedId(seed != null ? seed.getId() : null);
    seedBatch.setTime(time);
    return addClosable(getApi().createSeedBatch(seedBatch));
  }

  /**
   * Finds a seed batch
   * 
   * @param seedBatchId seed batch id
   * @return found seed batch
   */
  public SeedBatch findSeedBatch(UUID seedBatchId) {
    return getApi().findSeedBatch(seedBatchId);
  }

  /**
   * Updates a seed batch into the API
   * 
   * @param body body payload
   */
  public SeedBatch updateSeedBatch(SeedBatch body) {
    return getApi().updateSeedBatch(body, body.getId());
  }
  
  /**
   * Lists seed batches
   * 
   * @param firstResult
   * @param maxResults
   * @param isPassive
   * @return
   */
  public List<SeedBatch> listSeedBatches(Integer firstResult, Integer maxResults, Boolean isPassive) {
    return getApi().listSeedBatches(firstResult, maxResults, isPassive);
  }
  
  /**
   * Deletes a seed batch from the API
   * 
   * @param seedBatch seed batch to be deleted
   */
  public void delete(SeedBatch seedBatch) {
    getApi().deleteSeedBatch(seedBatch.getId());  
    removeClosable(closable -> !closable.getId().equals(seedBatch.getId()));
  }
  
  /**
   * Asserts seed count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listSeedBatches(Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID seedBatchId) {
    try {
      getApi().findSeedBatch(seedBatchId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param seedBatch seedBatch
   */
  public void assertUpdateFailStatus(int expectedStatus, SeedBatch seedBatch) {
    try {
      getApi().updateSeedBatch(seedBatch, seedBatch.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param seedBatch
   */
  public void assertDeleteFailStatus(int expectedStatus, SeedBatch seedBatch) {
    try {
      getApi().deleteSeedBatch(seedBatch.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertListFailStatus(int expectedStatus) {
    try {
      getApi().listSeedBatches(Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual seed batch equals expected seed when both are serialized into JSON
   * 
   * @param expectedStatus expected status code
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertSeedBatchesEqual(SeedBatch expected, SeedBatch actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(SeedBatch seedBatch) {
    getApi().deleteSeedBatch(seedBatch.getId());  
  }

}
