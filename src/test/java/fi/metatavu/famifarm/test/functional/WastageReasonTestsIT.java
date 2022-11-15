package fi.metatavu.famifarm.test.functional;

import java.util.UUID;

import fi.metatavu.famifarm.client.model.Facility;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.client.model.WastageReason;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for wastageReasons
 * 
 * @author Ville Koivukangas
 */
@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class WastageReasonTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateWastageReason() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason joroinenWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test Joroinen WastageReason", "Testi Joroinen Syy"), Facility.JOROINEN);
      WastageReason juvaWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test Juva WastageReason", "Testi Juva Syy"), Facility.JUVA);
      assertNotNull(joroinenWastageReason);
      assertNotNull(juvaWastageReason);
    }
  }

  @Test
  public void testCreateWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.workerJoroinen().wastageReasons().assertCreateFailStatus(403, builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      builder.anonymous().wastageReasons().assertCreateFailStatus(401, builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JUVA);
      builder.invalid().wastageReasons().assertCreateFailStatus(401, builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
    }
  }
  
  @Test
  public void testFindWastageReason() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().wastageReasons().assertFindFailStatus(404, UUID.randomUUID(), Facility.JOROINEN);
      builder.admin().wastageReasons().assertFindFailStatus(404, UUID.randomUUID(), Facility.JUVA);
      WastageReason createdWastageReasonJoroinen = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      WastageReason createdWastageReasonJuva = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JUVA);
      WastageReason foundWastageReasonJoroinen = builder.admin().wastageReasons().findWastageReason(createdWastageReasonJoroinen.getId(), Facility.JOROINEN);
      WastageReason foundWastageReasonJuva = builder.admin().wastageReasons().findWastageReason(createdWastageReasonJuva.getId(), Facility.JUVA);

      assertEquals(createdWastageReasonJoroinen.getId(), foundWastageReasonJoroinen.getId());
      assertEquals(createdWastageReasonJuva.getId(), foundWastageReasonJuva.getId());
      builder.admin().wastageReasons().assertWastageReasonsEqual(createdWastageReasonJoroinen, foundWastageReasonJoroinen);
      builder.admin().wastageReasons().assertWastageReasonsEqual(createdWastageReasonJuva, foundWastageReasonJuva);
      builder.workerJoroinen().wastageReasons().assertFindFailStatus(400, createdWastageReasonJoroinen.getId(), Facility.JUVA);
      builder.workerJoroinen().wastageReasons().assertFindFailStatus(400, createdWastageReasonJuva.getId(), Facility.JOROINEN);
    }
  }
  
  @Test
  public void testFindWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      assertNotNull(builder.admin().wastageReasons().findWastageReason(wastageReason.getId(), Facility.JOROINEN));
      assertNotNull(builder.managerJoroinen().wastageReasons().findWastageReason(wastageReason.getId(), Facility.JOROINEN));
      assertNotNull(builder.workerJoroinen().wastageReasons().findWastageReason(wastageReason.getId(), Facility.JOROINEN));
      builder.invalid().wastageReasons().assertFindFailStatus(401, wastageReason.getId(), Facility.JOROINEN);
      builder.anonymous().wastageReasons().assertFindFailStatus(401, wastageReason.getId(),Facility.JOROINEN);
    }
  }
  
  @Test
  public void testListwastageReasons() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      builder.admin().wastageReasons().assertCount(1, Facility.JOROINEN);
      builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason 2", "Testi Syy 2"), Facility.JOROINEN);
      builder.admin().wastageReasons().assertCount(2, Facility.JOROINEN);
      builder.admin().wastageReasons().assertCount(0, Facility.JUVA);
    }
  }
  
  @Test
  public void testListWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      builder.workerJoroinen().wastageReasons().assertCount(1, Facility.JOROINEN);
      builder.managerJoroinen().wastageReasons().assertCount(1, Facility.JOROINEN);
      builder.admin().wastageReasons().assertCount(1, Facility.JOROINEN);
      builder.invalid().wastageReasons().assertFindFailStatus(401, wastageReason.getId(), Facility.JOROINEN);
      builder.anonymous().wastageReasons().assertFindFailStatus(401, wastageReason.getId(), Facility.JOROINEN);
    }
  }
  
  @Test
  public void testUpdateWastageReason() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason createdWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      builder.admin().wastageReasons().assertWastageReasonsEqual(createdWastageReason, builder.admin().wastageReasons().findWastageReason(createdWastageReason.getId(), Facility.JOROINEN));
      
      WastageReason updateWastageReason = new WastageReason(); 
      updateWastageReason.setId(createdWastageReason.getId());
      updateWastageReason.setReason(builder.createLocalizedEntry("Updated WastageReason", "Päivitetty syy"));

      WastageReason updateWastageReason2 = new WastageReason();
      updateWastageReason2.setId(UUID.randomUUID());
      updateWastageReason2.setReason(builder.createLocalizedEntry("Updated WastageReason", "Päivitetty syy"));

      WastageReason updatedWastageReason = builder.admin().wastageReasons().updateWastageReason(updateWastageReason, Facility.JOROINEN);
      builder.admin().wastageReasons().assertWastageReasonsEqual(updateWastageReason, updatedWastageReason);
      builder.admin().wastageReasons().assertUpdateFailStatus(400, updateWastageReason, Facility.JUVA);
      builder.admin().wastageReasons().assertUpdateFailStatus(404, updateWastageReason2, Facility.JOROINEN);
    }
  }
  
  @Test
  public void testUpdateWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      builder.workerJoroinen().wastageReasons().assertUpdateFailStatus(403, wastageReason, Facility.JOROINEN);
      builder.anonymous().wastageReasons().assertUpdateFailStatus(401, wastageReason, Facility.JOROINEN);
      builder.invalid().wastageReasons().assertUpdateFailStatus(401, wastageReason, Facility.JOROINEN);
    }
  }
  
  @Test
  public void testDeletewastageReasons() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason createdWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      WastageReason foundWastageReason = builder.admin().wastageReasons().findWastageReason(createdWastageReason.getId(), Facility.JOROINEN);
      assertEquals(createdWastageReason.getId(), foundWastageReason.getId());
      builder.admin().wastageReasons().assertDeleteFailStatus(400, createdWastageReason.getId(), Facility.JUVA);
      builder.admin().wastageReasons().delete(createdWastageReason, Facility.JOROINEN);
      builder.admin().wastageReasons().assertFindFailStatus(404, createdWastageReason.getId(), Facility.JOROINEN);
    }
  }

  @Test
  public void testDeleteWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      builder.workerJoroinen().wastageReasons().assertDeleteFailStatus(403, wastageReason.getId(), Facility.JOROINEN);
      builder.anonymous().wastageReasons().assertDeleteFailStatus(401, wastageReason.getId(), Facility.JOROINEN);
      builder.invalid().wastageReasons().assertDeleteFailStatus(401, wastageReason.getId(), Facility.JOROINEN);
    }
  }
  
}