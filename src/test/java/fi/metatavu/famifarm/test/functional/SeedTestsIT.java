package fi.metatavu.famifarm.test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import fi.metatavu.famifarm.client.model.Facility;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

/**
 * Tests for seeds
 * 
 * @author Antti Leppä
 */
@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class SeedTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateSeed() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola")));
    }
  }

  @Test
  public void testCreateSeedPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.worker1().seeds().assertCreateFailStatus(403, builder.createLocalizedEntry("Rocket", "Rucola"));
      builder.anonymous().seeds().assertCreateFailStatus(401, builder.createLocalizedEntry("Rocket", "Rucola"));
      builder.invalid().seeds().assertCreateFailStatus(401, builder.createLocalizedEntry("Rocket", "Rucola"));
    }
  }
  
  @Test
  public void testFindSeed() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().seeds().assertFindFailStatus(404, UUID.randomUUID());
      Seed createdSeed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      builder.admin().seeds().assertFindFailStatus(404, createdSeed.getId(), Facility.JUVA);
      Seed foundSeed = builder.admin().seeds().findSeed(createdSeed.getId());
      assertEquals(createdSeed.getId(), foundSeed.getId());
      builder.admin().seeds().assertSeedsEqual(createdSeed, foundSeed);
    }
  }
  
  @Test
  public void testFindSeedPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      assertNotNull(builder.admin().seeds().findSeed(seed.getId()));
      assertNotNull(builder.manager().seeds().findSeed(seed.getId()));
      assertNotNull(builder.worker1().seeds().findSeed(seed.getId()));
      builder.invalid().seeds().assertFindFailStatus(401, seed.getId());
      builder.anonymous().seeds().assertFindFailStatus(401, seed.getId());
    }
  }
  
  @Test
  public void testListSeeds() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"), Facility.JOROINEN);
      builder.admin().seeds().assertCount(1, Facility.JOROINEN);
      builder.admin().seeds().create(builder.createLocalizedEntry("lettuce", "Lehtisalaatti"), Facility.JOROINEN);
      builder.admin().seeds().assertCount(2, Facility.JOROINEN);
      builder.admin().seeds().create(builder.createLocalizedEntry("lettuce", "Lehtisalaatti"), Facility.JUVA);
      builder.admin().seeds().assertCount(1, Facility.JUVA);
    }
  }
  
  @Test
  public void testListSeedPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      builder.worker1().seeds().assertCount(1);
      builder.manager().seeds().assertCount(1);
      builder.admin().seeds().assertCount(1);
      builder.invalid().seeds().assertFindFailStatus(401, seed.getId());
      builder.anonymous().seeds().assertFindFailStatus(401, seed.getId());
    }
  }
  
  @Test
  public void testUpdateSeed() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed createdSeed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      builder.admin().seeds().assertSeedsEqual(createdSeed, builder.admin().seeds().findSeed(createdSeed.getId()));
      
      Seed updateSeed = new Seed(); 
      updateSeed.setId(createdSeed.getId());
      updateSeed.setName(builder.createLocalizedEntry("lettuce", "Lehtisalaatti"));
     
      builder.admin().seeds().updateSeed(updateSeed);
      builder.admin().seeds().assertUpdateFailStatus(404, updateSeed, Facility.JUVA);
      builder.admin().seeds().assertSeedsEqual(updateSeed, builder.admin().seeds().findSeed(createdSeed.getId()));
    }
  }
  
  @Test
  public void testUpdateSeedPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      builder.worker1().seeds().assertUpdateFailStatus(403, seed, Facility.JUVA);
      builder.anonymous().seeds().assertUpdateFailStatus(401, seed, Facility.JUVA);
      builder.invalid().seeds().assertUpdateFailStatus(401, seed, Facility.JUVA);
    }
  }
  
  @Test
  public void testDeleteSeeds() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed createdSeed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      Seed foundSeed = builder.admin().seeds().findSeed(createdSeed.getId());
      assertEquals(createdSeed.getId(), foundSeed.getId());
      builder.admin().seeds().assertDeleteFailStatus(404, createdSeed, Facility.JUVA);
      builder.admin().seeds().delete(createdSeed);
      builder.admin().seeds().assertFindFailStatus(404, createdSeed.getId());     
    }
  }

  @Test
  public void testDeleteSeedPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      builder.worker1().seeds().assertDeleteFailStatus(403, seed, Facility.JUVA);
      builder.anonymous().seeds().assertDeleteFailStatus(401, seed, Facility.JUVA);
      builder.invalid().seeds().assertDeleteFailStatus(401, seed, Facility.JUVA);
    }
  }
  
}