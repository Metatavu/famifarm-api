package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.SeedsApi;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
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
  public Seed create(LocalizedEntry name) {
    Seed seed = new Seed();
    seed.setName(name);
    return addClosable(getApi().createSeed(seed));
  }

  /**
   * Finds a seed
   * 
   * @param seedId seed id
   * @return found seed
   */
  public Seed findSeed(UUID seedId) {
    return getApi().findSeed(seedId);
  }

  /**
   * Updates a seed into the API
   * 
   * @param body body payload
   */
  public Seed updateSeed(Seed body) {
    return getApi().updateSeed(body, body.getId());
  }
  
  /**
   * Deletes a seed from the API
   * 
   * @param seed seed to be deleted
   */
  public void delete(Seed seed) {
    getApi().deleteSeed(seed.getId());  
    removeClosable(closable -> !closable.getId().equals(seed.getId()));
  }
  
  /**
   * Asserts seed count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listSeeds(Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID seedId) {
    try {
      getApi().findSeed(seedId);
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
      Seed seed = new Seed();
      seed.setName(name);
      getApi().createSeed(seed);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertUpdateFailStatus(int expectedStatus, Seed seed) {
    try {
      getApi().updateSeed(seed, seed.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
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
      getApi().listSeeds(Collections.emptyMap());
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
    getApi().deleteSeed(seed.getId());  
  }

}
