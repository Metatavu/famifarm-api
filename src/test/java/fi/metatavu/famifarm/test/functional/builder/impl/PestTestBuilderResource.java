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
import fi.metatavu.famifarm.client.api.PestsApi;
import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.client.model.Pest;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for performedCultivationActions
 * 
 * @author Ville Koivukangas
 */
public class PestTestBuilderResource extends AbstractTestBuilderResource<Pest, PestsApi> {

  private final HashMap<UUID, Facility> pestFacilityMap = new HashMap<>();

  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public PestTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new performedCultivationAction
   * 
   * @param name name
   * @param facility facility
   * @return created Pest
   */
  public Pest create(List<LocalizedValue> name, Facility facility) {
    Pest performedCultivationAction = new Pest();
    performedCultivationAction.setName(name);
    Pest createdPest = getApi().createPest(performedCultivationAction, facility);
    pestFacilityMap.put(createdPest.getId(), facility);
    return addClosable(createdPest);
  }

  /**
   * Finds a pest
   * 
   * @param pestId pest id
   * @param facility facility
   * @return found performedCultivationAction
   */
  public Pest findPest(UUID pestId, Facility facility) {
    return getApi().findPest(facility, pestId);
  }

  /**
   * Updates a pest into the API
   * 
   * @param body body payload
   * @param facility facility
   */
  public Pest updatePest(Pest body, Facility facility) {
    return getApi().updatePest(body, facility, body.getId());
  }
  
  /**
   * Deletes a pest from the API
   * 
   * @param pestId pest id to be deleted
   * @param facility facility
   */
  public void delete(UUID pestId, Facility facility) {
    getApi().deletePest(facility, pestId);
    removeClosable(closable -> !closable.getId().equals(pestId));
  }

  /**
   * Asserts performedCultivationAction count within the system
   *
   * @param expected expected count
   * @param facility facility
   */
  public void assertCount(int expected, Facility facility) {
    assertEquals(expected, getApi().listPests(facility, Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param facility facility
   */
  public void assertFindFailStatus(int expectedStatus, UUID pestId, Facility facility) {
    try {
      getApi().findPest(facility, pestId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param name name
   */
  public void assertCreateFailStatus(int expectedStatus, List<LocalizedValue> name) {
    try {
      Pest performedCultivationAction = new Pest();
      performedCultivationAction.setName(name);
      getApi().createPest(performedCultivationAction, Facility.JOROINEN);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param pestId pest id
   * @param facility facility
   */
  public void assertUpdateFailStatus(int expectedStatus, Pest pestId, Facility facility) {
    try {
      getApi().updatePest(pestId, facility, pestId.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param pestId pest id
   * @param facility facility
   */
  public void assertDeleteFailStatus(int expectedStatus, UUID pestId, Facility facility) {
    try {
      getApi().deletePest(facility, pestId);
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
      getApi().listPests(facility, Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual performedCultivationActions equals expected seed when both are serialized into JSON
   * 
   * @param expected expected performedCultivationAction
   * @param actual actual performedCultivationAction
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertPestsEqual(Pest expected, Pest actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Pest pest) {
    if (pestFacilityMap.containsKey(pest.getId())) {
      getApi().deletePest(pestFacilityMap.get(pest.getId()), pest.getId());
    }
  }

}
