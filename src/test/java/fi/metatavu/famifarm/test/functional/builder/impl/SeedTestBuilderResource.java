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
import fi.metatavu.famifarm.client.api.SeedsApi;
import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for seeds
 * 
 * @author Antti Leppä
 */
public class SeedTestBuilderResource extends AbstractTestBuilderResource<Seed, SeedsApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public SeedTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new seed
   * 
   * @param name name
   * @return created seed
   */
  public Seed create(List<LocalizedValue> name) {
    Seed seed = new Seed();
    seed.setName(name);
    return addClosable(getApi().createSeed(seed, Facility.JOROINEN));
  }

  /**
   * Finds a seed
   * 
   * @param seedId seed id
   * @return found seed
   */
  public Seed findSeed(UUID seedId) {
    return getApi().findSeed(Facility.JOROINEN, seedId);
  }

  /**
   * Updates a seed into the API
   * 
   * @param body body payload
   */
  public Seed updateSeed(Seed body) {
    return getApi().updateSeed(body, Facility.JOROINEN, body.getId());
  }
  
  /**
   * Deletes a seed from the API
   * 
   * @param seed seed to be deleted
   */
  public void delete(Seed seed) {
    getApi().deleteSeed(Facility.JOROINEN, seed.getId());
    removeClosable(closable -> !closable.getId().equals(seed.getId()));
  }
  
  /**
   * Asserts seed count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listSeeds(Facility.JOROINEN, Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID seedId) {
    try {
      getApi().findSeed(Facility.JOROINEN, seedId);
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
      Seed seed = new Seed();
      seed.setName(name);
      getApi().createSeed(seed, Facility.JOROINEN);
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
  public void assertUpdateFailStatus(int expectedStatus, Seed seed) {
    try {
      getApi().updateSeed(seed, Facility.JOROINEN, seed.getId());
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
  public void assertDeleteFailStatus(int expectedStatus, Seed seed) {
    try {
      getApi().deleteSeed(Facility.JOROINEN, seed.getId());
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
      getApi().listSeeds(Facility.JOROINEN, Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual seed equals expected seed when both are serialized into JSON
   * 
   * @param expectedStatus expected status code
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertSeedsEqual(Seed expected, Seed actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Seed seed) {
    getApi().deleteSeed(Facility.JOROINEN, seed.getId());
  }

}
