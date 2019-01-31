package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.PerformedCultivationActionsApi;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PerformedCultivationAction;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for performedCultivationActions
 * 
 * @author Ville Koivukangas
 */
public class PerformedCultivationActionTestBuilderResource extends AbstractTestBuilderResource<PerformedCultivationAction, PerformedCultivationActionsApi> {
  
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
  public PerformedCultivationAction create(LocalizedEntry name) {
    PerformedCultivationAction performedCultivationAction = new PerformedCultivationAction();
    performedCultivationAction.setName(name);
    return addClosable(getApi().createPerformedCultivationAction(performedCultivationAction));
  }

  /**
   * Finds a performedCultivationAction
   * 
   * @param performedCultivationActionId performedCultivationAction id
   * @return found performedCultivationAction
   */
  public PerformedCultivationAction findPerformedCultivationAction(UUID performedCultivationActionId) {
    return getApi().findPerformedCultivationAction(performedCultivationActionId);
  }

  /**
   * Updates a performedCultivationAction into the API
   * 
   * @param body body payload
   */
  public PerformedCultivationAction updatePerformedCultivationAction(PerformedCultivationAction body) {
    return getApi().updatePerformedCultivationAction(body, body.getId());
  }
  
  /**
   * Deletes a performedCultivationAction from the API
   * 
   * @param performedCultivationAction performedCultivationAction to be deleted
   */
  public void delete(PerformedCultivationAction performedCultivationAction) {
    getApi().deletePerformedCultivationAction(performedCultivationAction.getId());  
    removeClosable(closable -> !closable.getId().equals(performedCultivationAction.getId()));
  }
  
  /**
   * Asserts performedCultivationAction count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listPerformedCultivationActions(Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID performedCultivationActionId) {
    try {
      getApi().findPerformedCultivationAction(performedCultivationActionId);
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
  public void assertCreateFailStatus(int expectedStatus, LocalizedEntry name) {
    try {
      PerformedCultivationAction performedCultivationAction = new PerformedCultivationAction();
      performedCultivationAction.setName(name);
      getApi().createPerformedCultivationAction(performedCultivationAction);
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
  public void assertUpdateFailStatus(int expectedStatus, PerformedCultivationAction performedCultivationAction) {
    try {
      getApi().updatePerformedCultivationAction(performedCultivationAction, performedCultivationAction.getId());
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
  public void assertDeleteFailStatus(int expectedStatus, PerformedCultivationAction performedCultivationAction) {
    try {
      getApi().deletePerformedCultivationAction(performedCultivationAction.getId());
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
      getApi().listPerformedCultivationActions(Collections.emptyMap());
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
  public void assertPerformedCultivationActionsEqual(PerformedCultivationAction expected, PerformedCultivationAction actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(PerformedCultivationAction performedCultivationAction) {
    getApi().deletePerformedCultivationAction(performedCultivationAction.getId());  
  }

}
