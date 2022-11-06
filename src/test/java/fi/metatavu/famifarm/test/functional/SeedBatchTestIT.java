package fi.metatavu.famifarm.test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

import fi.metatavu.famifarm.client.model.Facility;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class SeedBatchTestIT extends AbstractFunctionalTest {
  @Test
  public void testCreateSeedBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      SeedBatch seedBatch = builder.admin().seedBatches().create("code", seed, OffsetDateTime.now(), Facility.JOROINEN);
      assertNotNull(seedBatch);

      builder.admin().seedBatches().assertCreateFailStatus(404, seedBatch, Facility.JUVA);
    }
  }

  @Test
  public void testFindSeedBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().seedBatches().assertFindFailStatus(404, UUID.randomUUID(), Facility.JUVA);
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      SeedBatch createdSeedBatch = builder.admin().seedBatches().create("code", seed, OffsetDateTime.now(), Facility.JOROINEN);
      SeedBatch foundSeedBatch = builder.admin().seedBatches().findSeedBatch(createdSeedBatch.getId());
      builder.admin().seedBatches().assertFindFailStatus(404, foundSeedBatch.getId(), Facility.JUVA);
      assertEquals(createdSeedBatch.getId(), foundSeedBatch.getId());
      builder.admin().seedBatches().assertSeedBatchesEqual(createdSeedBatch, foundSeedBatch);
      builder.admin().seedBatches().delete(foundSeedBatch);
    }
  }

  @Test
  public void testListSeedBatches() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {

      Seed seedJoroinen = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"), Facility.JOROINEN);
      Seed seedJuva = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"), Facility.JUVA);

      builder.admin().seedBatches().create("code", seedJoroinen, OffsetDateTime.now(), Facility.JOROINEN);
      builder.admin().seedBatches().assertCount(1);
      SeedBatch batch = builder.admin().seedBatches().create("code", seedJoroinen, OffsetDateTime.now(), Facility.JOROINEN);
      builder.admin().seedBatches().assertCount(2);
      builder.admin().seedBatches().create("code", seedJuva, OffsetDateTime.now(), Facility.JUVA);

      batch.setActive(false);
      builder.admin().seedBatches().updateSeedBatch(batch);
      assertEquals(1, builder.admin().seedBatches().listSeedBatches(Facility.JOROINEN, null, null, null).size());
      assertEquals(1, builder.admin().seedBatches().listSeedBatches(Facility.JOROINEN,null, null, false).size());
      assertEquals(2, builder.admin().seedBatches().listSeedBatches(Facility.JOROINEN,null, null, true).size());
      assertEquals(1, builder.admin().seedBatches().listSeedBatches(Facility.JUVA, null, null, null).size());
      assertEquals(1, builder.admin().seedBatches().listSeedBatches(Facility.JUVA, null, null, false).size());

    }
  }

  @Test
  public void testUpdateSeedBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      SeedBatch createdSeedBatch = builder.admin().seedBatches().create("code", seed, OffsetDateTime.now(), Facility.JOROINEN);
      builder.admin().seedBatches().assertSeedBatchesEqual(createdSeedBatch, builder.admin().seedBatches().findSeedBatch(createdSeedBatch.getId()));

      SeedBatch updatedSeedBatch = new SeedBatch();
      updatedSeedBatch.setId(createdSeedBatch.getId());
      updatedSeedBatch.setCode("code 2");
      updatedSeedBatch.setSeedId(createdSeedBatch.getSeedId());
      updatedSeedBatch.setTime(createdSeedBatch.getTime());
      updatedSeedBatch.setActive(false);

      builder.admin().seedBatches().assertUpdateFailStatus(404, updatedSeedBatch, Facility.JUVA);
      builder.admin().seedBatches().updateSeedBatch(updatedSeedBatch);
      builder.admin().seedBatches().assertSeedBatchesEqual(updatedSeedBatch, builder.admin().seedBatches().findSeedBatch(createdSeedBatch.getId()));
    }
  }
}
