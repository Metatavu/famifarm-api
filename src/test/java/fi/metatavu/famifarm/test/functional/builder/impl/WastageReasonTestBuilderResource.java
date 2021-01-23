package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.WastageReasonsApi;
import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.client.model.WastageReason;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for wastageReasons
 * 
 * @author Ville Koivukangas
 */
public class WastageReasonTestBuilderResource extends AbstractTestBuilderResource<WastageReason, WastageReasonsApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public WastageReasonTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new wastageReason
   * 
   * @param reason reason
   * @return created wastageReason
   */
  public WastageReason create(List<LocalizedValue> reason) {
    WastageReason wastageReason = new WastageReason();
    wastageReason.setReason(reason);
    return addClosable(getApi().createWastageReason(wastageReason));
  }

  /**
   * Finds a wastageReason
   * 
   * @param wastageReasonId wastageReason id
   * @return found wastageReason
   */
  public WastageReason findWastageReason(UUID wastageReasonId) {
    return getApi().findWastageReason(wastageReasonId);
  }

  /**
   * Updates a wastageReason into the API
   * 
   * @param body body payload
   */
  public WastageReason updateWastageReason(WastageReason body) {
    return getApi().updateWastageReason(body, body.getId());
  }
  
  /**
   * Deletes a wastageReason from the API
   * 
   * @param wastageReason wastageReason to be deleted
   */
  public void delete(WastageReason wastageReason) {
    getApi().deleteWastageReason(wastageReason.getId());  
    removeClosable(closable -> !closable.getId().equals(wastageReason.getId()));
  }
  
  /**
   * Asserts wastageReason count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listWastageReasons(Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID wastageReasonId) {
    try {
      getApi().findWastageReason(wastageReasonId);
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
  public void assertCreateFailStatus(int expectedStatus, List<LocalizedValue> reason) {
    try {
      WastageReason wastageReason = new WastageReason();
      wastageReason.setReason(reason);
      getApi().createWastageReason(wastageReason);
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
  public void assertUpdateFailStatus(int expectedStatus, WastageReason wastageReason) {
    try {
      getApi().updateWastageReason(wastageReason, wastageReason.getId());
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
  public void assertDeleteFailStatus(int expectedStatus, WastageReason wastageReason) {
    try {
      getApi().deleteWastageReason(wastageReason.getId());
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
      getApi().listWastageReasons(Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual wastageReasons equals expected seed when both are serialized into JSON
   * 
   * @param expected expected wastageReason
   * @param actual actual wastageReason
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertWastageReasonsEqual(WastageReason expected, WastageReason actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(WastageReason wastageReason) {
    getApi().deleteWastageReason(wastageReason.getId());  
  }

}
