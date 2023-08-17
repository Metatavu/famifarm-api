package fi.metatavu.famifarm.test.functional.builder.impl;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.PerformedCultivationActionsApi;
import fi.metatavu.famifarm.client.model.Facility;
import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.client.model.PerformedCultivationAction;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;
import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test builder resource for performedCultivationActions
 *
 * @author Ville Koivukangas
 */
public class PerformedCultivationActionTestBuilderResource extends AbstractTestBuilderResource<PerformedCultivationAction, PerformedCultivationActionsApi> {

  private final HashMap<UUID, Facility> actionFacilityMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param apiClient initialized API client
   */
  public PerformedCultivationActionTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }

  /**
   * Creates new performedCultivationAction
   *
   * @param name name
   * @return created performedCultivationAction
   */
  public PerformedCultivationAction create(List<LocalizedValue> name, Facility facility) {
    PerformedCultivationAction performedCultivationAction = new PerformedCultivationAction();
    performedCultivationAction.setName(name);
    PerformedCultivationAction created = getApi().createPerformedCultivationAction(performedCultivationAction, facility);
    actionFacilityMap.put(created.getId(), facility);
    return addClosable(created);
  }

  /**
   * Finds a performedCultivationAction
   *
   * @param performedCultivationActionId performedCultivationAction id
   * @param facility facility
   * @return found performedCultivationAction
   */
  public PerformedCultivationAction findPerformedCultivationAction(UUID performedCultivationActionId, Facility facility) {
    return getApi().findPerformedCultivationAction(facility, performedCultivationActionId);
  }

  /**
   * Updates a performedCultivationAction into the API
   *
   * @param body body payload
   * @param facility facility
   */
  public PerformedCultivationAction updatePerformedCultivationAction(PerformedCultivationAction body, Facility facility) {
    return getApi().updatePerformedCultivationAction(body, facility, body.getId());
  }

  /**
   * Deletes a performedCultivationAction from the API
   *
   * @param performedCultivationAction performedCultivationAction to be deleted
   * @param facility facility
   */
  public void delete(PerformedCultivationAction performedCultivationAction, Facility facility) {
    getApi().deletePerformedCultivationAction(facility, performedCultivationAction.getId());
    removeClosable(closable -> !closable.getId().equals(performedCultivationAction.getId()));
  }

  /**
   * Asserts performedCultivationAction count within the system
   *
   * @param expected expected count
   * @param facility facility
   */
  public void assertCount(int expected, Facility facility) {
    assertEquals(expected, getApi().listPerformedCultivationActions(facility, Collections.emptyMap()).size());
  }

  /**
   * Asserts find status fails with given status code
   *
   * @param expectedStatus expected status code
   * @param facility facility
   */
  public void assertFindFailStatus(int expectedStatus, UUID performedCultivationActionId, Facility facility) {
    try {
      getApi().findPerformedCultivationAction(facility, performedCultivationActionId);
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
   * @param facility facility
   */
  public void assertCreateFailStatus(int expectedStatus, List<LocalizedValue> name, Facility facility) {
    try {
      PerformedCultivationAction performedCultivationAction = new PerformedCultivationAction();
      performedCultivationAction.setName(name);
      getApi().createPerformedCultivationAction(performedCultivationAction, facility);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code in Joroinen facility
   *
   * @param expectedStatus expected status code
   * @param name name
   */
  public void assertCreateFailStatus(int expectedStatus, List<LocalizedValue> name) {
    try {
      PerformedCultivationAction performedCultivationAction = new PerformedCultivationAction();
      performedCultivationAction.setName(name);
      getApi().createPerformedCultivationAction(performedCultivationAction, Facility.JOROINEN);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   *
   * @param expectedStatus expected status code
   * @param performedCultivationAction performed cultivation action
   * @param facility facility
   */
  public void assertUpdateFailStatus(int expectedStatus, PerformedCultivationAction performedCultivationAction, Facility facility) {
    try {
      getApi().updatePerformedCultivationAction(performedCultivationAction, facility, performedCultivationAction.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts delete status fails with given status code
   *
   * @param expectedStatus expected status code
   * @param performedCultivationAction performed cultivation action
   * @param facility facility
   */
  public void assertDeleteFailStatus(int expectedStatus, PerformedCultivationAction performedCultivationAction, Facility facility) {
    try {
      getApi().deletePerformedCultivationAction(facility, performedCultivationAction.getId());
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
      getApi().listPerformedCultivationActions(facility, Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual performedCultivationActions equals expected seed when both are serialized into JSON
   *
   * @param expected expected performedCultivationAction
   * @param actual   actual performedCultivationAction
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException   thrown when IO Exception occurs
   */
  public void assertPerformedCultivationActionsEqual(PerformedCultivationAction expected, PerformedCultivationAction actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(PerformedCultivationAction performedCultivationAction) {
    getApi().deletePerformedCultivationAction(actionFacilityMap.get(performedCultivationAction.getId()), performedCultivationAction.getId());
  }

}
