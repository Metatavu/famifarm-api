package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.WastageReason;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for wastageReasons
 * 
 * @author Ville Koivukangas
 */
public class WastageReasonTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateWastageReason() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy")));
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
      WastageReason createdWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      WastageReason foundWastageReason = builder.admin().wastageReasons().findWastageReason(createdWastageReason.getId());
      assertEquals(createdWastageReason.getId(), foundWastageReason.getId());
      builder.admin().wastageReasons().assertWastageReasonsEqual(createdWastageReason, foundWastageReason);
    }
  }
  
  @Test
  public void testFindWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      assertNotNull(builder.admin().wastageReasons().findWastageReason(wastageReason.getId()));
      assertNotNull(builder.manager().wastageReasons().findWastageReason(wastageReason.getId()));
      assertNotNull(builder.worker1().wastageReasons().findWastageReason(wastageReason.getId()));
      builder.invalid().wastageReasons().assertFindFailStatus(401, wastageReason.getId());
      builder.anonymous().wastageReasons().assertFindFailStatus(401, wastageReason.getId());
    }
  }
  
  @Test
  public void testListwastageReasons() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      
      builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      builder.admin().wastageReasons().assertCount(1);
      builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason 2", "Testi Syy 2"));
      builder.admin().wastageReasons().assertCount(2);
    }
  }
  
  @Test
  public void testListWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
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
      WastageReason createdWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      builder.admin().wastageReasons().assertWastageReasonsEqual(createdWastageReason, builder.admin().wastageReasons().findWastageReason(createdWastageReason.getId()));
      
      WastageReason updateWastageReason = new WastageReason(); 
      updateWastageReason.setId(createdWastageReason.getId());
      updateWastageReason.setReason(builder.createLocalizedEntry("Updated WastageReason", "Päivitetty syy"));
     
      builder.admin().wastageReasons().updateWastageReason(updateWastageReason);
      builder.admin().wastageReasons().assertWastageReasonsEqual(updateWastageReason, builder.admin().wastageReasons().findWastageReason(createdWastageReason.getId()));
    }
  }
  
  @Test
  public void testUpdateWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      builder.worker1().wastageReasons().assertUpdateFailStatus(403, wastageReason);
      builder.anonymous().wastageReasons().assertUpdateFailStatus(401, wastageReason);
      builder.invalid().wastageReasons().assertUpdateFailStatus(401, wastageReason);
    }
  }
  
  @Test
  public void testDeletewastageReasons() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason createdWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      WastageReason foundWastageReason = builder.admin().wastageReasons().findWastageReason(createdWastageReason.getId());
      assertEquals(createdWastageReason.getId(), foundWastageReason.getId());
      builder.admin().wastageReasons().delete(createdWastageReason);
      builder.admin().wastageReasons().assertFindFailStatus(404, createdWastageReason.getId());     
    }
  }

  @Test
  public void testDeleteWastageReasonPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      builder.worker1().wastageReasons().assertDeleteFailStatus(403, wastageReason);
      builder.anonymous().wastageReasons().assertDeleteFailStatus(401, wastageReason);
      builder.invalid().wastageReasons().assertDeleteFailStatus(401, wastageReason);
    }
  }
  
}