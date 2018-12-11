package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.UUID;

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

  @Override
  public void clean(Seed seed) {
    getApi().deleteSeed(seed.getId());  
  }

}
