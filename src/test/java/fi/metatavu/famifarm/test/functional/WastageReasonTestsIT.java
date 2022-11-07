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
      builder.worker1().wastageReasons().assertCreateFailStatus(403, builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      builder.anonymous().wastageReasons().assertCreateFailStatus(401, builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      builder.invalid().wastageReasons().assertCreateFailStatus(401, builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
    }
  }
  
  @Test
  public void testFindWastageReason() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().wastageReasons().assertFindFailStatus(404, UUID.randomUUID());
      WastageReason createdWastageReasonJoroinen = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JOROINEN);
      WastageReason createdWastageReasonJuva = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), Facility.JUVA);
      WastageReason foundWastageReasonJoroinen = builder.admin().wastageReasons().findWastageReason(createdWastageReasonJoroinen.getId(), Facility.JOROINEN);
      WastageReason foundWastageReasonJuva = builder.admin().wastageReasons().findWastageReason(createdWastageReasonJuva.getId(), Facility.JUVA);
      assertEquals(createdWastageReasonJoroinen.getId(), foundWastageReasonJoroinen.getId());
      assertEquals(createdWastageReasonJuva.getId(), foundWastageReasonJuva.getId());
      builder.admin().wastageReasons().assertWastageReasonsEqual(createdWastageReasonJoroinen, foundWastageReasonJoroinen);
      builder.admin().wastageReasons().assertWastageReasonsEqual(createdWastageReasonJuva, foundWastageReasonJuva);
    }
  }
  
  @Test
  public void testFindWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Facility facility = Facility.JOROINEN;
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), facility);
      assertNotNull(builder.admin().wastageReasons().findWastageReason(wastageReason.getId(), facility));
      assertNotNull(builder.manager().wastageReasons().findWastageReason(wastageReason.getId(), facility));
      assertNotNull(builder.worker1().wastageReasons().findWastageReason(wastageReason.getId(), facility));
      builder.invalid().wastageReasons().assertFindFailStatus(401, wastageReason.getId());
      builder.anonymous().wastageReasons().assertFindFailStatus(401, wastageReason.getId());
    }
  }
  
  @Test
  public void testListwastageReasons() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Facility facility = Facility.JOROINEN;
      builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), facility);
      builder.admin().wastageReasons().assertCount(1);
      builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason 2", "Testi Syy 2"), facility);
      builder.admin().wastageReasons().assertCount(2);
    }
  }
  
  @Test
  public void testListWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Facility facility = Facility.JOROINEN;
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), facility);
      builder.worker1().wastageReasons().assertCount(1);
      builder.manager().wastageReasons().assertCount(1);
      builder.admin().wastageReasons().assertCount(1);
      builder.invalid().wastageReasons().assertFindFailStatus(401, wastageReason.getId());
      builder.anonymous().wastageReasons().assertFindFailStatus(401, wastageReason.getId());
    }
  }
  
  @Test
  public void testUpdateWastageReason() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Facility facility = Facility.JOROINEN;
      WastageReason createdWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), facility);
      builder.admin().wastageReasons().assertWastageReasonsEqual(createdWastageReason, builder.admin().wastageReasons().findWastageReason(createdWastageReason.getId(), facility));
      
      WastageReason updateWastageReason = new WastageReason(); 
      updateWastageReason.setId(createdWastageReason.getId());
      updateWastageReason.setReason(builder.createLocalizedEntry("Updated WastageReason", "Päivitetty syy"));
     
      builder.admin().wastageReasons().updateWastageReason(updateWastageReason);
      builder.admin().wastageReasons().assertWastageReasonsEqual(updateWastageReason, builder.admin().wastageReasons().findWastageReason(createdWastageReason.getId(), facility));
    }
  }
  
  @Test
  public void testUpdateWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Facility facility = Facility.JOROINEN;
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), facility);
      builder.worker1().wastageReasons().assertUpdateFailStatus(403, wastageReason);
      builder.anonymous().wastageReasons().assertUpdateFailStatus(401, wastageReason);
      builder.invalid().wastageReasons().assertUpdateFailStatus(401, wastageReason);
    }
  }
  
  @Test
  public void testDeletewastageReasons() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Facility facility = Facility.JOROINEN;
      WastageReason createdWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), facility);
      WastageReason foundWastageReason = builder.admin().wastageReasons().findWastageReason(createdWastageReason.getId(), facility);
      assertEquals(createdWastageReason.getId(), foundWastageReason.getId());
      builder.admin().wastageReasons().delete(createdWastageReason);
      builder.admin().wastageReasons().assertFindFailStatus(404, createdWastageReason.getId());     
    }
  }

  @Test
  public void testDeleteWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Facility facility = Facility.JOROINEN;
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"), facility);
      builder.worker1().wastageReasons().assertDeleteFailStatus(403, wastageReason);
      builder.anonymous().wastageReasons().assertDeleteFailStatus(401, wastageReason);
      builder.invalid().wastageReasons().assertDeleteFailStatus(401, wastageReason);
    }
  }
  
}