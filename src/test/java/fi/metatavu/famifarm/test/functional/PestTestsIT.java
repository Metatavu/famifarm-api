package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Pest;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for pests
 * 
 * @author Antti Leppä
 */
public class PestTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreatePest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen")));
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
      builder.admin().pests().assertFindFailStatus(404, UUID.randomUUID());
      Pest createdPest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      Pest foundPest = builder.admin().pests().findPest(createdPest.getId());
      assertEquals(createdPest.getId(), foundPest.getId());
      builder.admin().pests().assertPestsEqual(createdPest, foundPest);
    }
  }
  
  @Test
  public void testFindPestPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest pest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      assertNotNull(builder.admin().pests().findPest(pest.getId()));
      assertNotNull(builder.manager().pests().findPest(pest.getId()));
      assertNotNull(builder.worker1().pests().findPest(pest.getId()));
      builder.invalid().pests().assertFindFailStatus(401, pest.getId());
      builder.anonymous().pests().assertFindFailStatus(401, pest.getId());
    }
  }
  
  @Test
  public void testListpests() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      
      builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      builder.admin().pests().assertCount(1);
      builder.admin().pests().create(builder.createLocalizedEntry("Test Pest 2", "Testi Loinen 2"));
      builder.admin().pests().assertCount(2);
    }
  }
  
  @Test
  public void testListPestPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      builder.worker1().pests().assertCount(1);
      builder.manager().pests().assertCount(1);
      builder.admin().pests().assertCount(1);
      builder.invalid().pests().assertListFailStatus(401);
      builder.anonymous().pests().assertListFailStatus(401);
    }
  }
  
  @Test
  public void testUpdatePest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest createdPest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      builder.admin().pests().assertPestsEqual(createdPest, builder.admin().pests().findPest(createdPest.getId()));
      
      Pest updatePest = new Pest(); 
      updatePest.setId(createdPest.getId());
      updatePest.setName(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
     
      builder.admin().pests().updatePest(updatePest);
      builder.admin().pests().assertPestsEqual(updatePest, builder.admin().pests().findPest(createdPest.getId()));
    }
  }
  
  @Test
  public void testUpdatePestPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest pest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      builder.worker1().pests().assertUpdateFailStatus(403, pest);
      builder.anonymous().pests().assertUpdateFailStatus(401, pest);
      builder.invalid().pests().assertUpdateFailStatus(401, pest);
    }
  }
  
  @Test
  public void testDeletePests() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest createdPest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      Pest foundPest = builder.admin().pests().findPest(createdPest.getId());
      assertEquals(createdPest.getId(), foundPest.getId());
      builder.admin().pests().delete(createdPest);
      builder.admin().pests().assertFindFailStatus(404, createdPest.getId());     
    }
  }

  @Test
  public void testDeletePestPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Pest pest = builder.admin().pests().create(builder.createLocalizedEntry("Test Pest", "Testi Loinen"));
      builder.worker1().pests().assertDeleteFailStatus(403, pest);
      builder.anonymous().pests().assertDeleteFailStatus(401, pest);
      builder.invalid().pests().assertDeleteFailStatus(401, pest);
    }
  }
  
}