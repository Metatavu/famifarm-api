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
import fi.metatavu.famifarm.client.api.DraftsApi;
import fi.metatavu.famifarm.client.model.Draft;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

public class DraftTestBuilderResource  extends AbstractTestBuilderResource<Draft, DraftsApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public DraftTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new draft
   *
   * @param type 
   * @param data 
   * @return created seed
   */
  public Draft create(String type, String data) {
    Draft draft = new Draft();
    draft.setData(data);
    draft.setType(type);
    return addClosable(getApi().createDraft(draft));
  }
  
  /**
   * Deletes a Draft from the API
   * 
   * @param draft Draft to be deleted
   */
  public void delete(Draft draft) {
    getApi().deleteDraft(draft.getId());  
    removeClosable(closable -> !closable.getId().equals(draft.getId()));
  }
  
  /**
   * Asserts Draft data equals
   * 
   * @param expected expected count
   */
  public void assertData(String expected, UUID userId, String type) {
    List<Draft> drafts = getApi().listDrafts(userId, type);
    assertEquals(1, drafts.size());
    assertEquals(expected, drafts.get(0).getData());
  }
  
  /**
   * Asserts Draft count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected, UUID userId, String type) {
    assertEquals(expected, getApi().listDrafts(userId, type).size());
  }
  
  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertCreateFailStatus(int expectedStatus, String type, String data) {
    try {
      Draft draft = new Draft();
      draft.setData(data);
      draft.setType(type);
      getApi().createDraft(draft);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertDeleteFailStatus(int expectedStatus, Draft draft) {
    try {
      getApi().deleteDraft(draft.getId());
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
      getApi().listDrafts(Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual draft equals expected draft when both are serialized into JSON
   * 
   * @param expected expected draft
   * @param actual actual draft
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertDraftEqual(Draft expected, Draft actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Draft draft) {
    getApi().deleteDraft(draft.getId());  
  }

}
