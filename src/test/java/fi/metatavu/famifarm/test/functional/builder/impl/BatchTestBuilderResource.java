package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.BatchesApi;
import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for batchs
 * 
 * @author Ville Koivukangas
 */
public class BatchTestBuilderResource extends AbstractTestBuilderResource<Batch, BatchesApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public BatchTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new batch
   * 
   * @param product product
   * @return created batch
   */
  public Batch create(Product product) {
    Batch batch = new Batch();
    batch.setProductId(product.getId());
    return addClosable(getApi().createBatch(batch));
  }

  /**
   * Finds a batch
   * 
   * @param batchId batch id
   * @return found batch
   */
  public Batch findBatch(UUID batchId) {
    return getApi().findBatch(batchId);
  }

  /**
   * Updates a batch into the API
   * 
   * @param body body payload
   */
  public Batch updateBatch(Batch body) {
    return getApi().updateBatch(body, body.getId());
  }
  
  /**
   * Deletes a batch from the API
   * 
   * @param batch batch to be deleted
   */
  public void delete(Batch batch) {
    getApi().deleteBatch(batch.getId());  
    removeClosable(closable -> !closable.getId().equals(batch.getId()));
  }
  
  /**
   * Asserts batch count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listBatches(Collections.emptyMap()).size());
  }
  
  /**
   * List batches
   * 
   * @param expected expected amount of results
   * @param firstResult firstResult
   * @param maxResults maxResults
   * @param createdBefore createdBefore
   * @param createdAfter createdAfter
   */
  public void assertCountWithCreatedTimes(int expected, String status, Integer firstResult, Integer maxResults, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
    String after = null;
    String before = null;
    
    if (createdBefore != null) {
      before = createdBefore.toString();
    }
    
    if (createdAfter != null) {
      after = createdAfter.toString();
    }
    
    assertEquals(expected, getApi().listBatches(status, firstResult, maxResults, before, after).size());
  }
  
  /** 
   * Asserts batch count within the system by status
   * 
   * @param status used status filter
   * @param expected expected count
   */
  public void assertCountByStatus(int expected, String status) {
    assertEquals(expected, getApi().listBatches(status, null, null, null, null).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID batchId) {
    try {
      getApi().findBatch(batchId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertCreateFailStatus(int expectedStatus, Product product) {
    try {
      Batch batch = new Batch();
      batch.setProductId(product.getId());
      getApi().createBatch(batch);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertUpdateFailStatus(int expectedStatus, Batch batch) {
    try {
      getApi().updateBatch(batch, batch.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertDeleteFailStatus(int expectedStatus, Batch batch) {
    try {
      getApi().deleteBatch(batch.getId());
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
      getApi().listBatches(Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual batchs equals expected seed when both are serialized into JSON
   * 
   * @param expected expected batch
   * @param actual actual batch
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertBatchsEqual(Batch expected, Batch actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Batch batch) {
    getApi().deleteBatch(batch.getId());  
  }

}
