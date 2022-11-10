package fi.metatavu.famifarm.test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import fi.metatavu.famifarm.client.model.Facility;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.client.model.Pest;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

/**
 * Tests for pests
 * 
 * @author Antti Leppä
 */
@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class PestTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreatePest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JOROINEN));
      assertNotNull(builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JUVA));
    }
  }

  @Test
  public void testCreatePestPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.worker1().pests().assertCreateFailStatus(403, builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      builder.anonymous().pests().assertCreateFailStatus(401, builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      builder.invalid().pests().assertCreateFailStatus(401, builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
    }
  }
  
  @Test
  public void testFindPest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().pests().assertFindFailStatus(404, UUID.randomUUID(), Facility.JOROINEN);
      Pest createdPest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JOROINEN);
      Pest foundPest = builder.admin().pests().findPest(createdPest.getId(), Facility.JOROINEN);
      assertEquals(createdPest.getId(), foundPest.getId());
      builder.admin().pests().assertFindFailStatus(400, createdPest.getId(), Facility.JUVA);
      builder.admin().pests().assertPestsEqual(createdPest, foundPest);
    }
  }
  
  @Test
  public void testFindPestPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest pest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JOROINEN);
      assertNotNull(builder.admin().pests().findPest(pest.getId(), Facility.JOROINEN));
      assertNotNull(builder.manager().pests().findPest(pest.getId(), Facility.JOROINEN));
      assertNotNull(builder.worker1().pests().findPest(pest.getId(), Facility.JOROINEN));
      builder.invalid().pests().assertFindFailStatus(401, pest.getId(), Facility.JOROINEN);
      builder.anonymous().pests().assertFindFailStatus(401, pest.getId(), Facility.JOROINEN);
    }
  }
  
  @Test
  public void testListpests() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      
      builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JOROINEN);
      builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JUVA);
      builder.admin().pests().assertCount(1, Facility.JOROINEN);
      builder.admin().pests().create(builder.createLocalizedEntry("Test Pest 2", "Testi Loinen 2"), Facility.JOROINEN);
      builder.admin().pests().assertCount(2, Facility.JOROINEN);
      builder.admin().pests().assertCount(1, Facility.JUVA);
    }
  }
  
  @Test
  public void testListPestPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JOROINEN);
      builder.worker1().pests().assertCount(1, Facility.JOROINEN);
      builder.manager().pests().assertCount(1, Facility.JOROINEN);
      builder.admin().pests().assertCount(1, Facility.JOROINEN);
      builder.invalid().pests().assertListFailStatus(401, Facility.JOROINEN);
      builder.anonymous().pests().assertListFailStatus(401, Facility.JOROINEN);
    }
  }
  
  @Test
  public void testUpdatePest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest createdPest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JOROINEN);
      builder.admin().pests().assertPestsEqual(createdPest, builder.admin().pests().findPest(createdPest.getId(), Facility.JOROINEN));
      
      Pest updatePest = new Pest(); 
      updatePest.setId(createdPest.getId());
      updatePest.setName(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      Pest updatePestWrongId = new Pest();
      updatePestWrongId.setId(UUID.randomUUID());
      updatePestWrongId.setName(builder.createLocalizedEntry("Test Pest invalid id", "Testi Loinen epäkelpo yksilöintitunnus"));

      builder.admin().pests().assertUpdateFailStatus(400, updatePest, Facility.JUVA);
      builder.admin().pests().assertUpdateFailStatus(404, updatePestWrongId, Facility.JOROINEN);
      builder.admin().pests().updatePest(updatePest, Facility.JOROINEN);
      builder.admin().pests().assertPestsEqual(updatePest, builder.admin().pests().findPest(createdPest.getId(), Facility.JOROINEN));
    }
  }
  
  @Test
  public void testUpdatePestPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest pest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JOROINEN);
      builder.worker1().pests().assertUpdateFailStatus(403, pest, Facility.JOROINEN);
      builder.anonymous().pests().assertUpdateFailStatus(401, pest, Facility.JOROINEN);
      builder.invalid().pests().assertUpdateFailStatus(401, pest, Facility.JOROINEN);
    }
  }
  
  @Test
  public void testDeletePests() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest createdPest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JOROINEN);
      Pest foundPest = builder.admin().pests().findPest(createdPest.getId(), Facility.JOROINEN);
      assertEquals(createdPest.getId(), foundPest.getId());
      builder.admin().pests().assertDeleteFailStatus(404, UUID.randomUUID(), Facility.JOROINEN);
      builder.admin().pests().assertDeleteFailStatus(400, createdPest.getId(), Facility.JUVA);
      builder.admin().pests().delete(createdPest.getId(), Facility.JOROINEN);
      builder.admin().pests().assertFindFailStatus(404, createdPest.getId(), Facility.JOROINEN);
    }
  }

  @Test
  public void testDeletePestPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest pest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"), Facility.JOROINEN);
      builder.worker1().pests().assertDeleteFailStatus(403, pest.getId(), Facility.JOROINEN);
      builder.anonymous().pests().assertDeleteFailStatus(401, pest.getId(), Facility.JOROINEN);
      builder.invalid().pests().assertDeleteFailStatus(401, pest.getId(), Facility.JOROINEN);
    }
  }
  
}