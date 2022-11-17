package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fi.metatavu.famifarm.client.model.Facility;
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

  private final HashMap<UUID, Facility> wastageReasonFacilityMap = new HashMap<>();
  
  /**
   * Creates new wastageReason
   * 
   * @param reason reason
   * @param facility facility
   * @return created wastageReason
   */
  public WastageReason create(List<LocalizedValue> reason, Facility facility) {
    WastageReason wastageReason = new WastageReason();
    wastageReason.setReason(reason);
    WastageReason createdWastageReason = getApi().createWastageReason(wastageReason, facility);
    wastageReasonFacilityMap.put(createdWastageReason.getId(), facility);
    return addClosable(createdWastageReason);
  }

  /**
   * Finds a wastageReason
   * 
   * @param wastageReasonId wastageReason id
   * @param facility facility
   * @return found wastageReason
   */
  public WastageReason findWastageReason(UUID wastageReasonId, Facility facility) {
    return getApi().findWastageReason(facility, wastageReasonId);
  }

  /**
   * Updates a wastageReason into the API
   * 
   * @param body body payload
   * @param facility facility
   */
  public WastageReason updateWastageReason(WastageReason body, Facility facility) {
    return getApi().updateWastageReason(body, facility, body.getId());
  }
  
  /**
   * Deletes a wastageReason from the API
   * 
   * @param wastageReason wastageReason to be deleted
   * @param facility facility
   */
  public void delete(WastageReason wastageReason, Facility facility) {
    getApi().deleteWastageReason(facility, wastageReason.getId());
    wastageReasonFacilityMap.remove(wastageReason.getId(), facility);
    removeClosable(closable -> !closable.getId().equals(wastageReason.getId()));
  }
  
  /**
   * Asserts wastageReason count within the system
   * 
   * @param expected expected count
   * @param facility facility
   */
  public void assertCount(int expected, Facility facility) {
    assertEquals(expected, getApi().listWastageReasons(facility, Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param wastageReasonId wastage reason id
   * @param facility facility
   */
  public void assertFindFailStatus(int expectedStatus, UUID wastageReasonId, Facility facility) {
    try {
      getApi().findWastageReason(facility, wastageReasonId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code
   *
   * @param facility facility
   * @param expectedStatus expected status code
   */
  public void assertCreateFailStatus(int expectedStatus, List<LocalizedValue> reason, Facility facility) {
    try {
      WastageReason wastageReason = new WastageReason();
      wastageReason.setReason(reason);
      getApi().createWastageReason(wastageReason, facility);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param wastageReason wastage reason
   * @param facility facility
   */
  public void assertUpdateFailStatus(int expectedStatus, WastageReason wastageReason, Facility facility) {
    try {
      getApi().updateWastageReason(wastageReason, facility, wastageReason.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param wastageReasonId wastage reason id
   * @param facility facility
   */
  public void assertDeleteFailStatus(int expectedStatus, UUID wastageReasonId, Facility facility) {
    try {
      getApi().deleteWastageReason(facility, wastageReasonId);
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param facility facility
   */
  public void assertListFailStatus(int expectedStatus, Facility facility) {
    try {
      getApi().listWastageReasons(facility, Collections.emptyMap());
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
    if (wastageReasonFacilityMap.containsKey(wastageReason.getId())) {
      getApi().deleteWastageReason(wastageReasonFacilityMap.get(wastageReason.getId()), wastageReason.getId());
    }
  }

}
