package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

public class SeedBatchTestIT {
	@Test
  public void testCreateSeedBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
    	Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      assertNotNull(builder.admin().seedBatches().create("code", seed.getId(), OffsetDateTime.now()));
    }
  }
	
	@Test
  public void testFindSeedBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().seedBatches().assertFindFailStatus(404, UUID.randomUUID());
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      SeedBatch createdSeedBatch = builder.admin().seedBatches().create("code", seed.getId(), OffsetDateTime.now());
      SeedBatch foundSeedBatch = builder.admin().seedBatches().findSeedBatch(createdSeedBatch.getId());
      assertEquals(createdSeedBatch.getId(), foundSeedBatch.getId());
      builder.admin().seedBatches().assertSeedsEqual(createdSeedBatch, foundSeedBatch);
    }
  }
	
	@Test
  public void testListSeedBatches() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
    	
    	Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
    	
      builder.admin().seedBatches().create("code", seed.getId(), OffsetDateTime.now());
      builder.admin().seedBatches().assertCount(1);
      builder.admin().seedBatches().create("code", seed.getId(), OffsetDateTime.now());
      builder.admin().seedBatches().assertCount(2);
    }
  }
	
	@Test
  public void testUpdateSeedBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      SeedBatch createdSeedBatch = builder.admin().seedBatches().create("code", seed.getId(), OffsetDateTime.now());
      builder.admin().seedBatches().assertSeedsEqual(createdSeedBatch, builder.admin().seedBatches().findSeedBatch(createdSeedBatch.getId()));
      
      SeedBatch updatedSeedBatch = new SeedBatch(); 
      updatedSeedBatch.setId(createdSeedBatch.getId());
      updatedSeedBatch.setCode("code 2");
     
      builder.admin().seedBatches().updateSeedBatch(updatedSeedBatch);
      builder.admin().seedBatches().assertSeedsEqual(updatedSeedBatch, builder.admin().seedBatches().findSeedBatch(createdSeedBatch.getId()));
    }
  }
}
