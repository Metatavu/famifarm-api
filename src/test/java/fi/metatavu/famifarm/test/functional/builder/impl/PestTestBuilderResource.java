package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collections;
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
   * @return created performedCultivationAction
   */
  public Pest create(List<LocalizedValue> name) {
    Pest performedCultivationAction = new Pest();
    performedCultivationAction.setName(name);
    return addClosable(getApi().createPest(performedCultivationAction, Facility.JOROINEN));
  }

  /**
   * Finds a performedCultivationAction
   * 
   * @param performedCultivationActionId performedCultivationAction id
   * @return found performedCultivationAction
   */
  public Pest findPest(UUID performedCultivationActionId) {
    return getApi().findPest(Facility.JOROINEN, performedCultivationActionId);
  }

  /**
   * Updates a performedCultivationAction into the API
   * 
   * @param body body payload
   */
  public Pest updatePest(Pest body) {
    return getApi().updatePest(body, Facility.JOROINEN, body.getId());
  }
  
  /**
   * Deletes a performedCultivationAction from the API
   * 
   * @param performedCultivationAction performedCultivationAction to be deleted
   */
  public void delete(Pest performedCultivationAction) {
    getApi().deletePest(Facility.JOROINEN, performedCultivationAction.getId());
    removeClosable(closable -> !closable.getId().equals(performedCultivationAction.getId()));
  }
  
  /**
   * Asserts performedCultivationAction count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listPests(Facility.JOROINEN, Collections.emptyMap()).size());
  }

  /**
   * Asserts performedCultivationAction count within the system
   *
   * @param expected expected count
   */
  public void assertCount(int expected, Facility facility) {
    assertEquals(expected, getApi().listPests(facility, Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
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
   */
  public void assertDeleteFailStatus(int expectedStatus, Pest pest, Facility facility) {
    try {
      getApi().deletePest(facility, pest.getId());
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
      getApi().listPests(Facility.JOROINEN, Collections.emptyMap());
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
  public void clean(Pest performedCultivationAction) {
    getApi().deletePest(Facility.JOROINEN, performedCultivationAction.getId());
  }

}
