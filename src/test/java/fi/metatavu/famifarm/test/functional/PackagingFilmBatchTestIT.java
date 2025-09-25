package fi.metatavu.famifarm.test.functional;

import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(KeycloakResource.class)
public class PackagingFilmBatchTestIT extends AbstractFunctionalTest {

  @Test
  public void testCreatePackagingFilmBatch() throws Exception {
    try (var builder = new TestBuilder()) {
      var time = java.time.OffsetDateTime.now();
      var packagingFilmBatch = builder.admin().packagingFilmBatches().create("Test PackagingFilmBatch", true, time, fi.metatavu.famifarm.client.model.Facility.JOROINEN);

      assertEquals("Test PackagingFilmBatch", packagingFilmBatch.getName());
      assertEquals(true, packagingFilmBatch.getActive());
      assertEquals(time.toEpochSecond(), packagingFilmBatch.getTime().toEpochSecond());
    }
  }

  @Test
  public void testUpdatePackagingFilmBatch() throws Exception {
    try (var builder = new TestBuilder()) {
      var time = java.time.OffsetDateTime.now();
      var packagingFilmBatch = builder.admin().packagingFilmBatches().create("Test PackagingFilmBatch", true, time, fi.metatavu.famifarm.client.model.Facility.JOROINEN);

      assertEquals("Test PackagingFilmBatch", packagingFilmBatch.getName());
      assertTrue(packagingFilmBatch.getActive());
      assertEquals(time.toEpochSecond(), packagingFilmBatch.getTime().toEpochSecond());

      var updatedTime = time.plusDays(1);
      var updatedPackagingFilmBatch = builder.admin().packagingFilmBatches().updatePackagingFilmBatch(packagingFilmBatch.getId(), "Updated PackagingFilmBatch", false, updatedTime, fi.metatavu.famifarm.client.model.Facility.JOROINEN);

      assertEquals("Updated PackagingFilmBatch", updatedPackagingFilmBatch.getName());
      assertFalse(updatedPackagingFilmBatch.getActive());
      assertEquals(updatedTime.toEpochSecond(), updatedPackagingFilmBatch.getTime().toEpochSecond());
    }
  }

  @Test
  public void testListPackagingFilmBatches() throws Exception {
    try (var builder = new TestBuilder()) {
      var time = java.time.OffsetDateTime.now();
      builder.admin().packagingFilmBatches().create("Test PackagingFilmBatch 1", true, time, fi.metatavu.famifarm.client.model.Facility.JOROINEN);
      builder.admin().packagingFilmBatches().create("Test PackagingFilmBatch 2", false, time, fi.metatavu.famifarm.client.model.Facility.JOROINEN);
      builder.admin().packagingFilmBatches().create("Test PackagingFilmBatch 3", true, time, fi.metatavu.famifarm.client.model.Facility.JOROINEN);

      var packagingFilmBatches = builder.admin().packagingFilmBatches().listPackagingFilmBatches(fi.metatavu.famifarm.client.model.Facility.JOROINEN, false);
      assertEquals(2, packagingFilmBatches.size());

      var allPackagingFilmBatches = builder.admin().packagingFilmBatches().listPackagingFilmBatches(fi.metatavu.famifarm.client.model.Facility.JOROINEN, true);
      assertEquals(3, allPackagingFilmBatches.size());
    }
  }


  @Test
  public void testFindPackagingFilmBatch() throws Exception {
    try (var builder = new TestBuilder()) {
      var time = java.time.OffsetDateTime.now();
      var packagingFilmBatch = builder.admin().packagingFilmBatches().create("Test PackagingFilmBatch", true, time, fi.metatavu.famifarm.client.model.Facility.JOROINEN);

      var foundPackagingFilmBatch = builder.admin().packagingFilmBatches().findPackagingFilmBatch(packagingFilmBatch.getId(), fi.metatavu.famifarm.client.model.Facility.JOROINEN);
      assertEquals(packagingFilmBatch.getId(), foundPackagingFilmBatch.getId());
      assertEquals("Test PackagingFilmBatch", foundPackagingFilmBatch.getName());
      assertTrue(foundPackagingFilmBatch.getActive());
      assertEquals(time.toEpochSecond(), foundPackagingFilmBatch.getTime().toEpochSecond());
    }
  }

  @Test
  public void testDeletePackagingFilmBatch() throws Exception {
    try (var builder = new TestBuilder()) {
      var time = java.time.OffsetDateTime.now();
      var packagingFilmBatch = builder.admin().packagingFilmBatches().create("Test PackagingFilmBatch", true, time, fi.metatavu.famifarm.client.model.Facility.JOROINEN);

      builder.admin().packagingFilmBatches().deletePackagingFilmBatch(packagingFilmBatch.getId(), fi.metatavu.famifarm.client.model.Facility.JOROINEN);

      builder.admin().packagingFilmBatches().assertFindFailStatus(404, packagingFilmBatch.getId(), fi.metatavu.famifarm.client.model.Facility.JOROINEN);
    }
  }

}
