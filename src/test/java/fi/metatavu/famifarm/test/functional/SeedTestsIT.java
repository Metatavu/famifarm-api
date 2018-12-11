package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for seeds
 * 
 * @author Antti Leppä
 */
public class SeedTestsIT {
  
  @Test
  public void testFindSeed() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().seeds().assertFindFailStatus(404, UUID.randomUUID());
      Seed createdSeed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      Seed foundSeed = builder.admin().seeds().findSeed(createdSeed.getId());
      assertEquals(createdSeed.getId(), foundSeed.getId());
    }
  }
  
  @Test
  public void testListSeeds() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().seeds().assertCount(0);
      builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      builder.admin().seeds().assertCount(1);
      builder.admin().seeds().create(builder.createLocalizedEntry("lettuce", "Lehtisalaatti"));
      builder.admin().seeds().assertCount(2);
    }
  }
  
  @Test
  public void testDeleteSeeds() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed createdSeed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      Seed foundSeed = builder.admin().seeds().findSeed(createdSeed.getId());
      assertEquals(createdSeed.getId(), foundSeed.getId());
      builder.admin().seeds().delete(createdSeed);
      builder.admin().seeds().assertFindFailStatus(404, createdSeed.getId());     
    }
  }
  

}